package com.topcoder.scraper.module;

import com.topcoder.scraper.module.IBasicModule;

import java.io.IOException;
import java.util.List;

/**
 * abstract change detection check module
 */
public interface IChangeDetectionCheckModule extends IBasicModule {

    /**
     * check change detection
     */
    void check(List<String> sites, String target) throws IOException;
}

