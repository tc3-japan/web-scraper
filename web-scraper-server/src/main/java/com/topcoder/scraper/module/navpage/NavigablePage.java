package com.topcoder.scraper.module.navpage;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.service.WebpageService;

import org.springframework.beans.factory.annotation.Autowired;

public class NavigablePage {

    protected TrafficWebClient webClient;
    protected HtmlPage page;

    public NavigablePage(HtmlPage page, TrafficWebClient webClient) {
        this.webClient = webClient;
        this.page = page;
    }

    public NavigablePage(String url, TrafficWebClient webClient) {
        this.webClient = webClient;
        setPage(url); // TODO: Will this work if page is uninitialized?
    }

    /* Get and Set */

    public void setPage(String url) {
        try {
            page = webClient.getPage(url);
        } catch (IOException e) {

            
            e.printStackTrace();
            System.out.println("Could not set page to " + url + " in NavigablePage.java");
        }
    }

    public HtmlPage getPage() {
        return page;
    }

    /* Navigate */

    public void click(String selector) {
        if (selector != null) {

            HtmlElement element = page.querySelector(selector);
            System.out.println("click() > Selected " + element + " from " + selector);

            if (element != null) {
                try {
                    HtmlPage result = webClient.click(element);
                    if (result != null)
                        page = result;
                    // savePage("pageClicked", "yahoo", webPageServiceWired);
                } catch (IOException e) {
                    System.out.println("Could not navigate to " + selector + " in NavigablePage.java");
                    e.printStackTrace();
                }
            }
        }
    }

    public void click(DomNode node, String selector) {
        if (node != null && selector != null) {
            DomElement element = node.querySelector(selector);
            System.out.println("click() > Selected " + element + " from " + selector);
            if (element != null) {
                try {
                    HtmlPage result = webClient.click(element);
                    System.out.println("CLICKED ELEMENT>>> " + result);
                    if (result != null)
                        page = result;
                    // savePage("pageClicked", "yahoo", webPageServiceWired);
                } catch (IOException e) {
                    System.out.println("Could not navigate to " + selector + " in NavigablePage.java");
                    e.printStackTrace();
                }
            }
        }
    }

    public void click(String selector, WebpageService webpageService) {
        if (selector != null && webpageService != null) {
            HtmlElement element = page.querySelector(selector);
            System.out.println("click() > Selected " + element + " from " + selector);
            if (element != null) {
                try {
                    HtmlPage result = webClient.click(element);
                    if (result != null)
                        page = result;
                    // savePage("pageClicked", "yahoo", webpageService);
                    webpageService.save("pageClicked", "yahoo", page.getWebResponse().getContentAsString());
                } catch (IOException e) {
                    System.out.println("Could not navigate to " + selector + " in NavigablePage.java");
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * public void click(DomNode orderNode, DomElement orderInfoLink, WebpageService
     * webpageService) {
     * webClient.getWebClient().getOptions().setJavaScriptEnabled(true); try {
     * HtmlPage result = webClient.click(orderInfoLink); if (result != null) page =
     * result; savePage("pageClicked", "yahoo", webpageService); //
     * webpageService.save("pageClicked", "yahoo", //
     * page.getWebResponse().getContentAsString()); } catch (IOException e) {
     * System.out.println("Could not navigate to page in NavigablePage.java");
     * e.printStackTrace(); } }
     */

    protected String getText(String selector) {
        HtmlElement node = page.querySelector(selector);
        String str = node != null ? node.asText().replaceAll("\\n", " ").trim() : null;
        System.out.println(">>> Got Text >>> " + str + " for " + selector);
        return str;
    }

    protected String getText(DomNode sourceNode, String selector) {
        HtmlElement node = sourceNode.querySelector(selector);
        String str = node != null ? node.asText().replaceAll("\\n", " ").trim() : null;
        System.out.println(">>> Got Text >>> " + str + " for " + selector);
        return str;
    }

    public void type(String input, String selector) {
        if (input != null && selector != null) {
            HtmlTextInput memberIdInput = page.querySelector(selector);
            if (memberIdInput != null) {
                System.out.println("type() > Selected " + memberIdInput + " from " + selector);
                // System.out.println("Typing " + input); //TODO: REMOVE
                try {
                    memberIdInput.type(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void typePassword(String input, String selector) {
        if (input != null && selector != null) {
            HtmlPasswordInput memberIdInput = page.querySelector(selector);
            System.out.println("typePassword() > Selected " + memberIdInput + " from " + selector);
            if (memberIdInput != null) {
                try {
                    memberIdInput.type(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void typeCheckbox(String input, String selector) {
        if (input != null && selector != null) {
            // value should be "on" or "off"
            HtmlCheckBoxInput memberIdInput = page.querySelector(selector);
            if (memberIdInput != null) {
                System.out.println("typeCheckBox() > Selected " + memberIdInput + " from " + selector);
                // System.out.println("Typing " + input); //TODO: REMOVE
                try {
                    memberIdInput.type(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String savePage(String fileName, String siteName, WebpageService webpageService) {
        // site name should be "yahoo", "amazon", etc.
        // see [...] for details
        // TODO: make proper documentation for these functions
        return webpageService.save(fileName, siteName, page.getWebResponse().getContentAsString());
    }

}