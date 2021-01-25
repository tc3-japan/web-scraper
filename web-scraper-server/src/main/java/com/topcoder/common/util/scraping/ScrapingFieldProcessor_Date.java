package com.topcoder.common.util.scraping;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.topcoder.common.util.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class ScrapingFieldProcessor_Date extends ScrapingFieldProcessor<Date> {

    public ScrapingFieldProcessor_Date(DomNode domNode, ScrapingFieldProperty scrapingFieldProperty) {
        super(domNode, scrapingFieldProperty);
    }

    @Override
    public Date process() {

        String dateStr = this.domNode.querySelector(this.scrapingFieldProperty.selector).getTextContent();
        try {
            return DateUtils.fromString(dateStr, scrapingFieldProperty.dateFormat);
        } catch (ParseException e) {
            return null;
        }
    }

}
