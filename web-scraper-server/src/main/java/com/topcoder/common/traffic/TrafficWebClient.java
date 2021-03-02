package com.topcoder.common.traffic;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.topcoder.common.config.TrafficProperty;
import com.topcoder.common.config.TrafficProperty.Tactic;
import com.topcoder.common.dao.RequestEventDAO;
import com.topcoder.common.dao.TacticEventDAO;
import com.topcoder.common.model.TacticEventStatus;
import com.topcoder.common.repository.RequestEventRepository;
import com.topcoder.common.repository.TacticEventRepository;
import com.topcoder.common.util.Common;
import com.topcoder.common.util.SpringTool;

import lombok.Data;

/**
 * wrap webclient to add traffic controller
 * each client request will create new web client and the web client only used for this thread
 * so this is a thread safe class
 */
@Data
public class TrafficWebClient {

    /**
     * the html web client
     */
    private WebClient webClient;

    /**
     * the user id
     */
    private int userId;

    /**
     * is this need restore session
     */
    private boolean isNeedAuth;

    /**
     * traffic Property
     */
    private TrafficProperty trafficProperty;

    /**
     * logger instance
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * retry count
     */
    private int retryCount;

    /**
     * the tacticEvent Repository
     */
    private TacticEventRepository tacticEventRepository;

    /**
     * the requestEvent Repository
     */
    private RequestEventRepository requestEventRepository;

    private TacticEventDAO tacticEventDAO;

    /**
     * create new Traffic web client
     *
     * @param userId     the user id, 0 mean don't use Traffic controller
     * @param isNeedAuth restore session if true
     */
    public TrafficWebClient(int userId, boolean isNeedAuth) {
        this.userId = userId;
        this.isNeedAuth = isNeedAuth;


        // according user id set agent
        Tactic tactic = getTactic();
        if (tactic != null && tactic.getUserAgent(userId) != null) {
            webClient = new WebClient(new BrowserVersion.BrowserVersionBuilder(BrowserVersion.CHROME)
                    .setUserAgent(tactic.getUserAgent(userId)).build());
        } else {
            webClient = new WebClient();
        }
        //webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setJavaScriptEnabled(false);
//    webClient.getOptions().setJavaScriptEnabled(true);
        tacticEventRepository = SpringTool.getApplicationContext().getBean(TacticEventRepository.class);
        requestEventRepository = SpringTool.getApplicationContext().getBean(RequestEventRepository.class);

        beforeTraffic();
    }

    /**
     * process like saving traffic record to database when called at last of traffic.
     */
    public void finishTraffic() {
        afterTraffic(true);
    }

    /**
     * get getTrafficProperty from spring context
     *
     * @return the getTrafficProperty
     */
    private TrafficProperty getTrafficProperty() {
        if (this.trafficProperty == null) {
            this.trafficProperty = SpringTool.getApplicationContext().getBean(TrafficProperty.class);
        }
        return this.trafficProperty;
    }

    /**
     * get page
     *
     * @param url the url
     * @param <P> the page cass
     * @return the html page
     * @throws IOException if failed
     */
    public <P extends Page> P getPage(String url) throws IOException {
        return doRequest(new Request() {
            @Override
            public P invoke() throws IOException {
                if (isNeedAuth) {
                    WebRequest webRequest = Common.wrapURL(webClient, url);
                    return webClient.getPage(webRequest);
                }
                return webClient.getPage(url);
            }
        }, "Open page : " + url);
    }

    /**
     * when page html element clicked
     *
     * @param htmlElement the html element
     * @param <P>         the page
     * @return the new page
     * @throws IOException if failed
     */
    public <P extends Page> P click(HtmlElement htmlElement) throws IOException {
        String content = "Click htmlElement : " + htmlElement.getNodeName() + " tag";
        if (StringUtils.isNotEmpty(htmlElement.getAttribute("id"))) {
            content += ", attr(id)=" + htmlElement.getAttribute("id");
        }
        if (StringUtils.isNotEmpty(htmlElement.getAttribute("href"))) {
            content += ", attr(href)=" + htmlElement.getAttribute("href");
        }
        if (htmlElement.getEnclosingForm() != null && StringUtils.isNotEmpty(htmlElement.getEnclosingForm().getActionAttribute())) {
            content += ", form-action=" + htmlElement.getEnclosingForm().getActionAttribute();
        }

        return doRequest(htmlElement::click, content);
    }


    /**
     * when page html element clicked
     *
     * @param domElement the html element
     * @param <P>        the page
     * @return the new page
     * @throws IOException if failed
     */
    public <P extends Page> P click(DomElement domElement) throws IOException {
        String content = "Click domElement : " + domElement.getNodeName() + " tag";
        if (StringUtils.isNotEmpty(domElement.getAttribute("id"))) {
            content += ", attr(id)=" + domElement.getAttribute("id");
        }
        if (StringUtils.isNotEmpty(domElement.getAttribute("href"))) {
            content += ", attr(href)=" + domElement.getAttribute("href");
        }

        return doRequest(domElement::click, content);
    }


    /**
     * do request
     *
     * @param request the request
     * @param <P>     the page
     * @return return page
     * @throws IOException if error happened
     */
    private <P extends Page> P doRequest(Request request, String content) throws IOException {
        try {
            P p = doRequestUnderController(request, content, false);
            return p;
        } catch (IOException e) {
            afterTraffic(false);
            throw e;
        }
    }

    /**
     * do request under controller
     *
     * @param request the request
     * @param content request content
     * @param <P>     the page
     * @return the new page
     * @throws IOException if error happened
     */
    private <P extends Page> P doRequestUnderController(
            Request request,
            String content,
            boolean skipWait
    ) throws IOException {
        RequestEventDAO eventDAO = beforeRequest(content, skipWait);
        try {
            P p = request.invoke();
            afterRequest(eventDAO, true);
            return p;
        } catch (IOException e) {
            Common.ZabbixLog(logger, e);
            afterRequest(eventDAO, false);
            if (whenRequestFailed()) {
                return doRequestUnderController(request, content, true);
            } else {
                throw e;
            }
        }
    }

    /**
     * get tactic by user id
     *
     * @return the tactic
     */
    private Tactic getTactic() {
        if (userId == 0) {
            return getTrafficProperty().getDefaultTactic();
        }
        for (Tactic tactic : getTrafficProperty().getTactics()) {
            if (tactic.getIndexInRange(userId) >= 0) {
                return tactic;
            }
        }
        return null;
    }

    /**
     * is need skip the traffic controller
     *
     * @return the result
     */
    private boolean isNeedSkip() {
        // didn't found tactic instance
        return getTactic() == null;
    }

    /**
     * before traffic, write record into db
     *
     * @return the db record
     */
    private void beforeTraffic() {
        if (isNeedSkip()) {
            return;
        }
        Tactic tactic = getTactic();
        this.retryCount = Common.getValueOrDefault(tactic.getRetryTrailCount(), 0);
        TacticEventDAO tacticEventDAO = new TacticEventDAO();
        tacticEventDAO.setContents(tactic.toString());
        tacticEventDAO.setCreateAt(Date.from(Instant.now()));
        saveTacticEventDAO(tacticEventDAO);
        this.tacticEventDAO = tacticEventDAO;
    }

    /**
     * after Traffic, save time and status
     *
     * @param succeed the result
     */
    private void afterTraffic(boolean succeed) {
        if (isNeedSkip()) {
            return;
        }
        this.tacticEventDAO.setFinishAt(Date.from(Instant.now()));
        this.tacticEventDAO.setStatus(succeed ? TacticEventStatus.SUCCESS : TacticEventStatus.FAILED);
        saveTacticEventDAO(this.tacticEventDAO);
    }

    /**
     * be request, set agent, proxy, and sleep some times
     *
     * @param skipWait is need skip wait
     * @return the request event dao
     */
    private RequestEventDAO beforeRequest(String content, boolean skipWait) {
        if (isNeedSkip()) {
            return null;
        }

        Tactic tactic = getTactic();
        // proxy settings
        if (tactic.getProxyServerByUserId(userId) != null) {
            ProxyConfig proxyConfig = new ProxyConfig();
            try {
                String urlStr = tactic.getProxyServerByUserId(userId);
                String proxyMsg = "proxy = " + urlStr;

                // create proxy info and set it to web client
                if (urlStr.startsWith("sock")) { // adhoc code: java.net.URL doesn't correspond to socks protocol
                    proxyConfig.setSocksProxy(true);
                    urlStr = urlStr.replaceAll("sock.*://", "http://");
                }
                URL url = new URL(urlStr);
                proxyConfig.setProxyPort(url.getPort());
                proxyConfig.setProxyHost(url.getHost());
                webClient.getOptions().setProxyConfig(proxyConfig);

                // traffic record to database
                content = content + ", " + proxyMsg;
                logger.info(proxyMsg + " for this request");
            } catch (MalformedURLException e) {
                String message = "proxy server parse error, url = " + tactic.getProxyServerByUserId(userId);
                Common.ZabbixLog(logger, message, e);
            }
        }

        logger.info("user-agent = " + webClient.getBrowserVersion().getUserAgent());

        sleep(skipWait);

        RequestEventDAO requestEventDAO = new RequestEventDAO();
        requestEventDAO.setCreateAt(Date.from(ZonedDateTime.now().toInstant()));

        requestEventDAO.setContents(content);
        requestEventDAO.setTacticEventId(this.tacticEventDAO.getId());
        saveRequestEventDAO(requestEventDAO);

        return requestEventDAO;
    }

    public void sleep() {
        sleep(false);
    }

    public void sleep(boolean skipWait) {
        Tactic tactic = getTactic();

        long waitTime = 1000 * Common.getValueOrDefault(tactic.getRequestInterval(), 1);
        /*
         * Request Interval Random: Yes/No (Default No)
         * a. Random value(α) is set to 0-1.
         * b. Derived Request Interval：N * (1 + α)
         */
        if (Boolean.TRUE.equals(tactic.getRequestIntervalRandom())) {
            waitTime = (int) (waitTime * (1.0 + Math.random()));
        }

        if (!skipWait && waitTime > 0) {
            try {
                logger.info("sleep " + waitTime + "ms, then send request");
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                // ignore this, we don't care
            }
        }
    }

    /**
     * the request failed
     *
     * @return is need continue retry
     */
    private boolean whenRequestFailed() {
        if (isNeedSkip()) {
            return false;
        }

        if (retryCount > 0) {
            retryCount -= 1;
            long waitTime = 1000 * Common.getValueOrDefault(getTactic().getRetryInterval(), 1);
            try {
                logger.info("request failed, now sleep " + waitTime + "ms to try again, and left retry count = " + retryCount);
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                // ignore this, we don't care
            }
            return true;
        }
        return false;
    }

    /**
     * when a user request finished
     *
     * @param requestEventDAO the request event
     * @param succeed         the result
     */
    private void afterRequest(RequestEventDAO requestEventDAO, boolean succeed) {
        if (isNeedSkip()) {
            return;
        }
        requestEventDAO.setFinishAt(Date.from(ZonedDateTime.now().toInstant()));
        requestEventDAO.setStatus(succeed ? TacticEventStatus.SUCCESS : TacticEventStatus.FAILED);
        saveRequestEventDAO(requestEventDAO);
    }

    /**
     * save TacticEventDAO to DB
     * this method overwride by TrafficWebClientForDryRun
     *
     * @param tacticEventDAO
     */
    //TODO do not write to tactic_event table.
    protected void saveTacticEventDAO(TacticEventDAO tacticEventDAO) {
        tacticEventRepository.save(tacticEventDAO);
    }

    /**
     * save RequestEventDAO to DB
     * this method overwride by TrafficWebClientForDryRun
     *
     * @param requestEventDAO
     */
    protected void saveRequestEventDAO(RequestEventDAO requestEventDAO) {
        requestEventRepository.save(requestEventDAO);
    }

    /**
     * this class only used for DryRunModule
     */
    public class TrafficWebClientForDryRun extends TrafficWebClient {

        public TrafficWebClientForDryRun(int userId, boolean isNeedAuth) {
            super(userId, isNeedAuth);
        }

        //TODO do not write to tactic_event table.
    /*
    @Override
    protected void saveTacticEventDAO(TacticEventDAO tacticEventDAO) {
      // do not save tacticEventDAO
    }
    */

        @Override
        protected void saveRequestEventDAO(RequestEventDAO requestEventDAO) {
            // do not save requestEventDAO
        }

    }

}
