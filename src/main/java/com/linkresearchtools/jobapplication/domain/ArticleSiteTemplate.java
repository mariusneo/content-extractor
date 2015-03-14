package com.linkresearchtools.jobapplication.domain;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArticleSiteTemplate {
    private String titleSelector;

    /**
     * Mapping between the selectors for the elements containing the content of the article and
     * the list of selectors that are to be ignored within this content (e.g. : ads sub-elements filled dynamically
     * within the content element that are not relevant for the content of the article).
     * Note that the mapping holds the order in which the selectors for the content were added (top - down for the
     * content).
     */
    private Map<String, List<String>> contentSelectors = new LinkedHashMap<>();

    private String authorSelector;

    private String publishDateSelector;

    /**
     * Due to the fact that the date is displayed in a different format on different article websites, this date
     * format is being used for parsing the date in order to retrieve a {@link java.util.Date} object.
     * One limitation that currently exists is that the date format is specified only for one language (depending on
     * the website). If the website would be retrieved in a different language than the one expected, the parsing of
     * the date will very likely fail.
     */
    private SimpleDateFormat publishDateFormat;

    public ArticleSiteTemplate() {
    }

    public String getTitleSelector() {
        return titleSelector;
    }

    public void setTitleSelector(String titleSelector) {
        this.titleSelector = titleSelector;
    }

    public void addContentSelector(String contentSelector) {
        contentSelectors.put(contentSelector, Collections.emptyList());
    }

    public void addContentSelector(String contentSelector, List<String> ignoredSubcontentSelectors) {
        contentSelectors.put(contentSelector, ignoredSubcontentSelectors);
    }

    public Map<String, List<String>> getContentSelectors() {
        return Collections.unmodifiableMap(contentSelectors);
    }

    public String getAuthorSelector() {
        return authorSelector;
    }

    public void setAuthorSelector(String authorSelector) {
        this.authorSelector = authorSelector;
    }

    public String getPublishDateSelector() {
        return publishDateSelector;
    }

    public void setPublishDateSelector(String publishDateSelector) {
        this.publishDateSelector = publishDateSelector;
    }

    public SimpleDateFormat getPublishDateFormat() {
        return publishDateFormat;
    }

    public void setPublishDateFormat(SimpleDateFormat publishDateFormat) {
        this.publishDateFormat = publishDateFormat;
    }
}
