Content Extractor Tool
============================================================

## Assignment
Write a small program which is able to extract the following data from some web pages:

- Main article title
- Main article content
- Author
- Publishing date of the article

All extract text should be UTF encoded and cleaned up in the best possible way
(so less artifacts form navigation or header/footer the better).
Output should be a simple JSON structure containing that information.

Additional information like Social Share Counts, comments and additional Meta Data will be appreciated but are not required.

The program should be written in Java 8 and be packaged as single JAR (including all Third Party ), using a build script
like Gradle or Maven (also use it for dependencies).

Please try to extract that information for the following pages:

- http://www.spiegel.de/wissenschaft/technik/solar-impulse-2-solarflugzeug-startet-erdumrundung-a-1022458.html
- http://seo.de/8963/seo-der-soeldner-sinniert-ueber-die-welt-von-heute/
- http://bit.ly/1C1rR8N

## General description of the solution
The purpose of the application is to extract structured data out of one or more specified webpage by their URL.

The application uses a site template repository where the structure (CSS selectors for the elements containing the
title, author, content and publishing date) of each the article site to be retrieved is stored.

### Limitations of this approach

The downside of using this manual approach for extracting the structured data is that when one of the sites for which
the structure is stored in the repository changes its structure, the extraction will not work anymore until it will be
manually adjusted.

Take the news portal spiegel.de for example. The website articles

- http://www.spiegel.de/wissenschaft/technik/solar-impulse-2-solarflugzeug-startet-erdumrundung-a-1022458.html
- http://www.spiegel.de/wissenschaft/natur/co2-reduzierung-bundeskabinett-beschliesst-klimapaket-a-1006371.html
- http://www.spiegel.de/kultur/musik/pharrell-williams-robin-thicke-blurred-lines-ist-plagiat-a-1022867.html

follow the expected format for retrieving structured data about the article. On the other hand, the website article

- http://www.spiegel.de/wissenschaft/technik/teilchenbeschleuniger-lhc-am-cern-startet-wieder-a-1021822.html

does not follow anymore the expected structure and a new template would need to be manually calibrated to fit this
specific web template.




## Setup of the project

This guideline assumes that Java 8 and Maven 3 are installed on your machine.

In order to build the tool use the command :
```bash
mvn clean assembly:assembly
```
in your base directory.

In order to run the tool use the command :

```bash
java -jar target/content-extractor-1.0.0-jar-with-dependencies.jar
```

This will display the following content :

<blockquote>
<pre>
usage: content-extractor [options] [url]
Simple tool used to extract meaningful information (author, publish date,
content, comments, etc.) about an web article page.
 -help                print this message
 -outputfile <file>   path to the file on which to save the output
LinkResearchTools job application test
</pre>
</blockquote>

So, the following command :

```bash
java -jar target/content-extractor-1.0.0-jar-with-dependencies.jar http://seo.de/8963/seo-der-soeldner-sinniert-ueber-die-welt-von-heute/
```
would retrieve the structured information retrieved from the webpage http://seo.de/8963/seo-der-soeldner-sinniert-ueber-die-welt-von-heute/
and it would display it in JSON format :

<blockquote>
<pre>
{
  "url" : "http://seo.de/8963/seo-der-soeldner-sinniert-ueber-die-welt-von-heute/",
  "title" : "Seo, der Söldner, sinniert über die Welt von heute",
  "content" : "Wenn Seo der Söldner, von seiner Wolke herabblickt, sieht er eine in vielerlei Hinsicht veränderte Welt. Das Reich von Königin Googela ist deutlich größer geworden, obwohl Nachbarn wie Fürst Bingo ihren Einfluss langsam ausbauen. Die Menschen fürchten nicht mehr Räuber mit Messern, sondern Diebe mit Laptops. Und seinen Nachfahren sind die Fußstapfen, die er einst voller Stolz hinterließ, längst zu klein geworden. Sie setzen neue, eigene Zeichen, die vom Wirbel der Zeit immer schneller in die Vergessenheit getragen werden.„War das schön“, murmelt Seo. Mit Freude denkt er daran zurück, wie er Googela bezirzte und das Ranking der Dörfer verbesserte. Die Königin hielt ihre Karten zwar immer verdeckt, war seit jeher wankelmütig und ganz sicher nicht leicht zufriedenzustellen. Dafür ließ sich zumindest erahnen, welche Reaktionen auf einzelne Aktionen folgen würden. Mit bunten Bannern, positiven Erwähnungen und Wegweisern schaffte man es relativ zügig, in der Gunst Googelas aufzusteigen. Inzwischen entlocken solche Bemühungen der mächtigen Königin mitunter nur ein müdes Lächeln.Zu seiner Ära hätte man angesichts der Algorithmen, die heute von geheimen Zirkeln in gut bewachten Kammern programmiert werden, von Zauberei gesprochen. Die Königin deshalb der Hexerei bezichtigen? Viel zu gefährlich, einst wie jetzt. Wer es wagt, die Hand zu heben oder sich den Gelüsten der Herrscherin zu verweigern, wird bestraft. Vom Sandkasten, der Seo ein Schaudern über den Rücken laufen ließ, hört man nur noch wenig. Doch erst ist noch da.Umso gespannter beobachtet Seo die Bemühungen seiner Nachfolger. Kaum dreht Googela an einem neuen Rädchen oder legt einen weiteren Schalter um, müssen sie springen, damit ihre Arbeit nicht von der Maschinerie der Königin zerrissen wird. In der Stimme des Söldners schwingt Mitleid mit, wenn er leise murmelt: „Hartes Brot.“Mit Tools und Tabellen, Technik und Trends kämpfen die Seos der Neuzeit um jeden Rang. Minimale Halbwertzeiten vieler Faktoren machen ihnen das Leben nicht gerade leichter. Das Rüstzeug eines guten Seo-Söldners wiegt daher längst schwerer als die Marschmontur eines Elitesoldaten. Ich packe meinen Koffer und nehme mit: Die missfeldtsche Bildoptimierung, kratzige Marketingstrategien, einen Superhelden, der OnPage für Ordnung sorgt – oder wie man damals sagte, das Dorf sauber hält –, die aus Millionen Daten erbaute sistrixsche Kapelle, ein paar spitze Bleistifte und nach Möglichkeit Magentabletten und Baldrian.Zu allem Überfluss, findet Seo, der Söldner, müssen sich die modernen Recken jetzt auch noch als Dompteure beweisen. Googela hat mit Panda und Pinguin gleich zwei extrem gefährliche Tiere ins Rennen geschickt. Sie zu bändigen, ohne gepickt und gebissen zu werden oder in einen großen Haufen Mist zu treten, hat so manches Dorf überfordert. „Was kommt wohl als nächstes“, überlegt Seo. Er tippt auf ein Zebra. Das entspräche dem schwarz-weißen Weltbild der Königin. Ob es wohl während der Dekade der White-Hats und Black-Hats geprägt wurde?. Wer weiß das schon.Die Zukunft ist auch für Seo ein Mysterium. Hätte er eine Glaskugel, würde er den tapferen Helden von heute sein Wissen leise ins Ohr flüstern. So bleiben ihm auf seiner Wolke nur Gedankenspiele und die Erkenntnis: Wer nach oben möchte, darf sich nicht auf halber Strecke ausruhen und sollte auf dem Gipfel aufpassen, nicht von der nächsten Googela-Böhe zurück ins Tal geweht zu werden.",
  "author" : "Andre Massmann",
  "publishingDate" : 1418943600000,
  "socialShareCounts" : [ {
    "socialSite" : "Facebook",
    "count" : 19
  }, {
    "socialSite" : "Twitter",
    "count" : 11
  }, {
    "socialSite" : "GooglePlus",
    "count" : 7
  } ]
}
</pre>
</blockquote>

## Libraries used

### [JSoup](http://jsoup.org/)

The application makes heavy use of this library for selecting, through CSS selectors, the structured data from the DOM
tree of the retrieved web pages that are to be analysed.

### [Commons CLI](commons.apache.org/proper/commons-cli)

This library is used for parsing the command line arguments.

### [Jackson](https://github.com/FasterXML/jackson)

Library is used for persisting Java object in JSON format.

## Utils

JSON Twitter Shares Count API URL
http://cdn.api.twitter.com/1/urls/count.json?url=YOUR_URL

JSON Facebook Shares Count API URL
http://graph.facebook.com/?id=YOUR_URL

HTML Google+ Shares Count Button URL (webpage content needs to be parsed in order to retrieve the number of counts)
https://plusone.google.com/_/+1/fastbutton?url=YOUR_URL

XML Youtube Comments API URL : https://gdata.youtube.com/feeds/api/videos/VIDEO_ID/comments