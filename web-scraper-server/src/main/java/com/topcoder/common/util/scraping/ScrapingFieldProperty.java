package com.topcoder.common.util.scraping;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScrapingFieldProperty {

    public String selector;
    public String type;
    public String extractRegex;
    public String dateFormat;
}
