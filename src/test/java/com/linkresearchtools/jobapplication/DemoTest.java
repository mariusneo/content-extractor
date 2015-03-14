package com.linkresearchtools.jobapplication;


import com.linkresearchtools.jobapplication.contract.Article;
import com.linkresearchtools.jobapplication.service.ArticleInformationExtractionException;
import com.linkresearchtools.jobapplication.service.ArticleInformationExtractionService;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class DemoTest {

    @Test
    public void demo() {
        String[] urls = new String[]{
                "http://www.spiegel.de/wissenschaft/technik/solar-impulse-2-solarflugzeug-startet-erdumrundung-a" +
                        "-1022458.html",
                "http://www.spiegel.de/wissenschaft/natur/co2-reduzierung-bundeskabinett-beschliesst-klimapaket-a" +
                        "-1006371.html",
                "http://www.spiegel.de/kultur/musik/pharrell-williams-robin-thicke-blurred-lines-ist-plagiat-a" +
                        "-1022867.html",
                "http://seo.de/8963/seo-der-soeldner-sinniert-ueber-die-welt-von-heute/",
                "http://seo.de/8771/seoday-2014/",
                "https://www.youtube.com/watch?v=7ukCC9su-jg",
                "https://www.youtube.com/watch?v=oUm4MHEeFCY",
                "http://bit.ly/1C1rR8N" //redirect URLs are also supported.
        };

        final ArticleInformationExtractionService articleInformationExtractionService = new
                ArticleInformationExtractionService();
        final ObjectMapper mapper = new ObjectMapper();

        Arrays.stream(urls).parallel().forEach(url -> {
            Article article = null;
            try {
                article = articleInformationExtractionService.extractArticleInformation(url);
            } catch (ArticleInformationExtractionException e) {
                System.err.println(e.getMessage());
            }
            try {
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(article);
                System.out.println(json);
            } catch (IOException e) {
                System.err.println("Serialization to JSON of the output for the article website " + url +
                        " failed. Reason : " + e.getMessage());
            }
        });
    }
}
