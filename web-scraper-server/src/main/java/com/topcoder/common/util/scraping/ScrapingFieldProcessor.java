package com.topcoder.common.util.scraping;

import com.gargoylesoftware.htmlunit.html.DomNode;

/**
 * Base Class for scrape each field of Html Pages.
 * This is also Factory of each concrete scrape field Class (ScrapeField_Xxx)
 *
 * @param <T>
 */
public abstract class ScrapingFieldProcessor<T> {

    protected DomNode domNode;
    protected ScrapingFieldProperty scrapingFieldProperty;

    public ScrapingFieldProcessor(DomNode domNode, ScrapingFieldProperty scrapingFieldProperty) {
        this.domNode = domNode;
        this.scrapingFieldProperty = scrapingFieldProperty;
    }

    public abstract T process();

    public static <T> ScrapingFieldProcessor<T> prepare(DomNode dom, ScrapingFieldProperty scrapingFieldProperty) {
        switch (scrapingFieldProperty.type) {
            case "String":
                return (ScrapingFieldProcessor<T>) new ScrapingFieldProcessor_String(dom, scrapingFieldProperty);
            case "Date":
                return (ScrapingFieldProcessor<T>) new ScrapingFieldProcessor_Date(dom, scrapingFieldProperty);
            case "List<DomNode>":
                return (ScrapingFieldProcessor<T>) new ScrapingFieldProcessor_DomList(dom, scrapingFieldProperty);
//      case "DomNode" :
//        return new ScrapeField_Dom(dom, scrapingFieldProperty);
//      case "HTMLInput" :
//        return new ScrapeField_HTMLInput(dom, scrapingFieldProperty);
            default:
                return null;
        }
    }
}
