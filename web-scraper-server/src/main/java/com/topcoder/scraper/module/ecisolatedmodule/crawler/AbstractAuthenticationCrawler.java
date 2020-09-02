package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ecunifiedmodule.AuthStep;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public abstract class AbstractAuthenticationCrawler {

    public abstract AbstractAuthenticationCrawlerResult authenticate(
            TrafficWebClient webClient,
            String username,
            String password,
            String code,
            boolean init
    ) throws IOException;

    public abstract AbstractAuthenticationCrawlerResult authenticate(
            TrafficWebClient webClient,
            String username,
            String password,
            String code
    ) throws IOException;

    @Getter
    @Setter
    protected AuthStep authStep = AuthStep.FIRST;
}
