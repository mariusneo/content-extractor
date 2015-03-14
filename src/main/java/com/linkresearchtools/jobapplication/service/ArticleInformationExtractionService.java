package com.linkresearchtools.jobapplication.service;

import com.google.gson.Gson;
import com.linkresearchtools.jobapplication.contract.Article;
import com.linkresearchtools.jobapplication.contract.social.FacebookSharesCount;
import com.linkresearchtools.jobapplication.contract.SocialShareCount;
import com.linkresearchtools.jobapplication.contract.SocialSite;
import com.linkresearchtools.jobapplication.contract.social.TwitterSharesCount;
import com.linkresearchtools.jobapplication.domain.ArticleSiteTemplate;
import com.linkresearchtools.jobapplication.domain.ArticleSiteTemplateRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p><This service encapsulates the logic required for extracting structured data :
 * <pre>
 *  <ul>
 *      <li>title</li>
 *      <li>content</li>
 *      <li>author</li>
 *      <li>publish date</li>
 *      <li>social shares counts</li>
 *  </ul>
 * </pre>
 * from a web page specified by its URL.
 * In order to achieve this functionality, JSoup library is heavily used with its ability of retrieving
 * data from a HTML document by using CSS selectors.
 * /p>
 * <p>In the case of the element containing content of the article, some of its children elements may be not
 * relevant (e.g. : ads), reason why their content is omitted when performing the extraction.</p>
 * <p>Due to the fact that javascript needs to run in order to create the social share buttons on the page (which
 * can't be done currently via JSoup) share counts for a web page are retrieved separately by using APIs
 * exposed by the social websites.</p>
 * <p>In the case of youtube videos, the comments are also loaded dynamically via Javascript in an &lt;iframe&gt;
 * element. An extension in the functionality of this service that would be able to retrieve the comments from the
 * site, would need to have then a plugable mechanism in order to choose a particular strategy (parse the article
 * page html or retrieve via API the comments) for loading the comments.</p>
 */
public class ArticleInformationExtractionService {

    /**
     * Share counts API URLs :
     * for Twitter http://cdn.api.twitter.com/1/urls/count.json?url=YOUR_URL
     * for Facebook http://graph.facebook.com/?id==YOUR_URL
     */
    private static final String TWITTER_SHARES_COUNT_API_PREFIX_URL = "http://cdn.api.twitter.com/1/urls/count" +
            ".json?url=";

    private static final String FACEBOOK_SHARES_COUNT_API_PREFIX_URL = "http://graph.facebook.com/?id=";

    private static final String GOOGLE_PLUS_SHARES_COUNT_PREFIX_URL = "https://plusone.google.com/_/+1/fastbutton?url=";

    public ArticleInformationExtractionService() {
    }

    public Article extractArticleInformation(String url) throws ArticleInformationExtractionException {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new ArticleInformationExtractionException("Retrieval of the article website " + url + " failed. " +
                    "Reason : " + e.getMessage(), e);
        }
        // take into account also redirection (e.g. bit.ly links)
        String redirectUrl = doc.baseUri();

        ArticleSiteTemplateRepository articleSiteTemplateRepository = new ArticleSiteTemplateRepository();
        ArticleSiteTemplate articleSiteTemplate = articleSiteTemplateRepository.findArticleSiteTemplate(redirectUrl);
        if (articleSiteTemplate == null) {
            throw new ArticleInformationExtractionException("No template known for the article website " + url);
        }

        // retrieve the informations about the article
        String title = getTitleText(doc, articleSiteTemplate.getTitleSelector());
        String content = getContentText(doc, articleSiteTemplate.getContentSelectors());
        String author = getAuthorText(doc, articleSiteTemplate.getAuthorSelector());
        String publishDateText = getPublishDateText(doc, articleSiteTemplate.getPublishDateSelector());
        Date publishDate;
        try {
            SimpleDateFormat publishDateFormat = articleSiteTemplate.getPublishDateFormat();
            publishDate = publishDateFormat.parse(publishDateText);
        } catch (ParseException e) {
            throw new ArticleInformationExtractionException("The publish date '" + publishDateText +
                    "' of the web article " + url +
                    " does not follow the expected date format", e);
        }

        Article article = new Article(url, title, content, author, publishDate);
        // retrieve the social share counts
        addSocialSharesCounts(article, redirectUrl, url);

        return article;
    }


    private static String getPublishDateText(Document doc, String publishDateSelector) {
        Elements dateElements = doc.select(publishDateSelector);
        String publishDate = "";
        if (!dateElements.isEmpty()) {
            publishDate = dateElements.first().text();
        }
        return publishDate;
    }

    private static String getAuthorText(Document doc, String authorSelector) {
        Elements authorElements = doc.select(authorSelector);
        StringBuilder authorBuilder = new StringBuilder();
        for (Element authorElement : authorElements) {
            String text = authorElement.text();
            if (authorBuilder.length() > 0)
                authorBuilder.append(" ");
            authorBuilder.append(text);
        }

        return authorBuilder.toString();
    }

    private static String getTitleText(Document doc, String titleSelector) {
        Elements titleElements = doc.select(titleSelector);
        StringBuilder titleBuilder = new StringBuilder();
        for (Element titleElement : titleElements) {
            String text = titleElement.text();
            if (titleBuilder.length() > 0)
                titleBuilder.append(" ");
            titleBuilder.append(text);
        }

        return titleBuilder.toString();
    }

    private static String getContentText(Document doc, Map<String, List<String>> contentSelectors) {
        StringBuilder contentBuilder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : contentSelectors.entrySet()) {
            String contentSelector = entry.getKey();
            List<String> subcontentSelectors = entry.getValue();
            Elements contentElements = doc.select(contentSelector);
            for (Element contentElement : contentElements) {
                Element strippedContentElement = contentElement;
                if (subcontentSelectors != null && !subcontentSelectors.isEmpty()) {
                    // Clone the element to avoid removing useful items that may be needed in the future steps of the
                    // extraction process
                    Element contentElementClone = contentElement.clone();
                    subcontentSelectors
                            .stream()
                            .forEach(subcontentSelector -> contentElementClone.select(subcontentSelector).remove());
                    strippedContentElement = contentElementClone;
                }
                String text = strippedContentElement.text();
                contentBuilder.append(text);
            }
        }

        return contentBuilder.toString();
    }

    private static void addSocialSharesCounts(Article article, String redirectUrl, String url) {
        addFacebookSharesCounts(article, redirectUrl, url);
        addTwitterSharesCounts(article, redirectUrl, url);
        addGooglePlusSharesCounts(article, redirectUrl, url);
    }

    private static void addFacebookSharesCounts(Article article, String redirectUrl, String url) {
        try {
            InputStream fbInput = new URL(FACEBOOK_SHARES_COUNT_API_PREFIX_URL + redirectUrl).openStream();
            Reader fbReader = new InputStreamReader(fbInput, "UTF-8");
            FacebookSharesCount fbCount = new Gson().fromJson(fbReader, FacebookSharesCount.class);
            article.addSocialShareCounts(new SocialShareCount(SocialSite.Facebook, fbCount.getShares()));
        } catch (Exception e) {
            System.err.println("Failed to retrieve Facebook social share counts for the url " + url + " . Reason " + e
                    .getMessage());
        }
    }

    private static void addTwitterSharesCounts(Article article, String redirectUrl, String url) {
        try {
            InputStream twInput = new URL(TWITTER_SHARES_COUNT_API_PREFIX_URL + redirectUrl).openStream();
            Reader twReader = new InputStreamReader(twInput, "UTF-8");
            TwitterSharesCount twCount = new Gson().fromJson(twReader, TwitterSharesCount.class);
            article.addSocialShareCounts(new SocialShareCount(SocialSite.Twitter, twCount.getCount()));
        } catch (Exception e) {
            System.err.println("Failed to retrieve Twitter social share counts for the url " + url + " . Reason " + e
                    .getMessage());
        }
    }

    private static void addGooglePlusSharesCounts(Article article, String redirectUrl, String url) {
        try {
            Document gplusDoc = Jsoup.connect(GOOGLE_PLUS_SHARES_COUNT_PREFIX_URL + redirectUrl).get();
            String gplusCountText = gplusDoc.select("#aggregateCount").text();
            article.addSocialShareCounts(new SocialShareCount(SocialSite.GooglePlus, Integer.parseInt(gplusCountText)));
        } catch (Exception e) {
            System.err.println("Failed to retrieve Google social share counts for the url " + url + " . Reason " + e
                    .getMessage());
        }
    }
}
