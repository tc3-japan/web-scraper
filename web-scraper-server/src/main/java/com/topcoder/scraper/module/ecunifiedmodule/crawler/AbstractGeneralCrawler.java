package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import com.topcoder.scraper.module.ecunifiedmodule.dryrun.DryRunUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.topcoder.common.dao.ConfigurationDAO;
import com.topcoder.common.model.scraper.ProductConfig;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.scraper.service.WebpageService;

public abstract class AbstractGeneralCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGeneralCrawler.class);

    protected WebpageService webpageService;
    protected String site;
    protected String jsonConfigText;
    protected ProductConfig productConfig;
    protected DryRunUtils dryRunUtils;

    public AbstractGeneralCrawler(String site, String type, WebpageService webpageService, ConfigurationRepository configurationRepository) {
        LOGGER.debug("[constructor] in");
        this.site = site;
        this.webpageService = webpageService;
        this.jsonConfigText = this.getConfigFromDB(site, type, configurationRepository);
    }

    /**
     * read json from database
     *
     * @param site                    the site name
     * @param type                    the json type
     * @param configurationRepository the database repository
     * @return json text
     */
    private String getConfigFromDB(String site, String type, ConfigurationRepository configurationRepository) {
        LOGGER.debug("[getConfigFromDB] in");
        LOGGER.debug("[getConfigFromDB] site:" + site + " type:" + type);
        ConfigurationDAO configurationDAO = configurationRepository.findBySiteAndType(site, type);
        return configurationDAO.getConfig();
    }

    /**
     * set config before run
     *
     * @param conf the config text
     */
    public void setConfig(String conf) {
        LOGGER.debug("[setConfig] in");
        LOGGER.debug("conf = " + conf);
        if (conf != null && !conf.equals("")) {
            this.jsonConfigText = conf;
        }
    }

    public void setDryRunUtils(DryRunUtils dru) {
        this.dryRunUtils = dru;
    }

}
