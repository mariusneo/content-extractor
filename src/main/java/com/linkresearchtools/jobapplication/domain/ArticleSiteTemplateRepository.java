package com.linkresearchtools.jobapplication.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * <p>This class acts like a data access object for the templates of the websites on which the tool can operate.</p>
 * <p/>
 * <p>The public interface exposed by this class makes it easy to possibly expand this repository so that it uses
 * a database store for storing the article site template information.</p>
 */
public class ArticleSiteTemplateRepository {
    private static final Map<String, ArticleSiteTemplate> regexUrl2SiteTemplateMap = new HashMap<>();

    private static final Set<String> regexUrls = new HashSet<>();

    static {
        init();
    }

    public ArticleSiteTemplate findArticleSiteTemplate(final String url) {
        if (url == null || url.trim().length() == 0) return null;
        Optional<ArticleSiteTemplate> articleSiteTemplateOptional = regexUrls.parallelStream()
                .filter(regexUrl -> url.matches(regexUrl))
                .findFirst()
                .map(regexUrl -> regexUrl2SiteTemplateMap.get(regexUrl));
        if (articleSiteTemplateOptional.isPresent())
            return articleSiteTemplateOptional.get();
        return null;
    }

    private static void init() {
        ArticleSiteTemplate spiegelDeSiteTemplate = new ArticleSiteTemplate();
        regexUrl2SiteTemplateMap.put(
                "http:\\/\\/www\\.spiegel\\.de\\/([a-zA-Z\\d\\-]+\\/)+[a-zA-Z\\d\\-]*\\.html",
                spiegelDeSiteTemplate);
        spiegelDeSiteTemplate.setTitleSelector("#content-main .article-title span");
        spiegelDeSiteTemplate.addContentSelector("#content-main .article-intro strong");
        List<String> spiegelDeIgnoredArticleSectionSelectors = new ArrayList<>();
        spiegelDeIgnoredArticleSectionSelectors.add("#content_ad_1");
        spiegelDeIgnoredArticleSectionSelectors.add(".asset-box");
        spiegelDeSiteTemplate.addContentSelector("#js-article-column .article-section",
                spiegelDeIgnoredArticleSectionSelectors);
        spiegelDeSiteTemplate.setAuthorSelector("#js-article-column .article-section + p:first-of-type i");
        spiegelDeSiteTemplate.setPublishDateSelector("#content-main .article-function-date time");
        // spiegel.de publish date example : Montag, 09.03.2015 – 14:11 Uhr
        spiegelDeSiteTemplate.setPublishDateFormat(
                new SimpleDateFormat("EEEE',' dd.MM.yyyy' '–' 'HH:mm 'Uhr'", Locale.GERMAN));

        ArticleSiteTemplate seoDeSiteTemplate = new ArticleSiteTemplate();
        seoDeSiteTemplate.setTitleSelector("#contentleft .postarea h1");
        seoDeSiteTemplate.addContentSelector("#contentleft .postarea .date ~ p");
        seoDeSiteTemplate.setAuthorSelector("#contentleft .postarea .dateleft a[rel=\"author\"]");
        seoDeSiteTemplate.setPublishDateSelector("#contentleft .postarea .dateleft .time");
        // seo.de publish date example : Dezember 19, 2014
        seoDeSiteTemplate.setPublishDateFormat(new SimpleDateFormat("MMMMM dd, yyyy", Locale.GERMAN));
        regexUrl2SiteTemplateMap.put("http:\\/\\/seo\\.de\\/[\\d]+\\/[a-zA-Z\\d\\-]*\\/?", seoDeSiteTemplate);

        ArticleSiteTemplate youtubeSiteTemplate = new ArticleSiteTemplate();
        youtubeSiteTemplate.setTitleSelector("#eow-title");
        youtubeSiteTemplate.addContentSelector("#eow-description");
        youtubeSiteTemplate.setAuthorSelector("#watch7-user-header .yt-user-info a");
        youtubeSiteTemplate.setPublishDateSelector("#watch-uploader-info strong");
        // regex used to extract the publish date from texts like the following : "Uploaded on Mar 31, 2010"
//        String youtubePublishDateRegex = "(?<dateFormatEN>(?<month>(?i)\\b" +
//                "(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))" +
//                "(?<day>\\s(?:[1-9]|[12][0-9]|3[01])),(?<year>\\s(?:19|20)\\d\\d))";
        youtubeSiteTemplate.setPublishDateFormat(new SimpleDateFormat("'Uploaded on' MMM dd, yyyy", Locale.US));
        regexUrl2SiteTemplateMap.put("(http|https):\\/\\/www\\.youtube\\.com\\/watch\\?v=[a-zA-Z\\d\\-]*\\/?",
                youtubeSiteTemplate);

        regexUrls.addAll(regexUrl2SiteTemplateMap.keySet());
    }
}
