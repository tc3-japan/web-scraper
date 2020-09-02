package com.topcoder.scraper.module;

import java.io.IOException;

import com.topcoder.common.dao.ProductDAO;

/**
 * abstract product search module
 */
public interface IProductSearchModule extends IBasicModule {
    /**
     * search product for cross ec product
     */
    ProductDAO searchProductInfo(String siteName, String searchKey) throws IOException;
}
