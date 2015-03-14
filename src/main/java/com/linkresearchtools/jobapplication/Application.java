package com.linkresearchtools.jobapplication;

import com.linkresearchtools.jobapplication.contract.Article;
import com.linkresearchtools.jobapplication.service.ArticleInformationExtractionException;
import com.linkresearchtools.jobapplication.service.ArticleInformationExtractionService;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>The purpose of the application is to extract structured data out of one or more specified webpage by their
 * URL.</p>
 * <p>The application uses a site template repository where the structure (CSS selectors for the elements containing the
 * title, author, content and publishing date) of each the article site to be retrieved is stored. The downside of
 * using this manual approach for extracting the structured data is that when one of the sites for which the
 * structure is stored in the repository changes its structure, the extraction will not work anymore until it will be
 * manually adjusted.</p>
 * <p>Jackson library is used for persisting the extracted data in JSON template.</p>
 */
public class Application {

    public static final String OUTPUT_FILE_PARAM = "outputfile";

    public static void main(String[] args) throws IOException {
        // create the command line parser and the associated options
        CommandLineParser parser = new BasicParser();

        Options options = createCommandLineOptions();

        CommandLine cmd;
        try {
            // parse the command line arguments
            cmd = parser.parse(options, args);
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            printHelp(options);
            return;
        }

        if (cmd.hasOption("help")) {
            printHelp(options);
            return;
        }

        String[] cmdArguments = cmd.getArgs();
        OutputStream out = System.out;
        if (cmd.hasOption(OUTPUT_FILE_PARAM)) {
            String outputFilePath = cmd.getOptionValue(OUTPUT_FILE_PARAM);
            if (outputFilePath == null || outputFilePath.trim().length() == 0) {
                System.err.println("Invalid file path specified for the argument " + OUTPUT_FILE_PARAM);
                printHelp(options);
                return;
            }
            try {
                out = new FileOutputStream(outputFilePath);
            } catch (FileNotFoundException e) {
                System.err.println("Invalid file path specified for the argument " + OUTPUT_FILE_PARAM);
                printHelp(options);
                return;
            }
        }
        final OutputStream outputStream = out;

        final ArticleInformationExtractionService articleInformationExtractionService = new
                ArticleInformationExtractionService();
        if (cmdArguments.length == 0 || cmdArguments.length > 1) {
            printHelp(options);
        } else {
            String url = cmdArguments[0];
            try {
                Article article = articleInformationExtractionService.extractArticleInformation(url);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(article);
                    outputStream.write(json.getBytes());
                } catch (IOException e) {
                    System.err.println("Serialization to JSON of the output for the article website " + url +
                            " failed. Reason : " + e.getMessage());
                } finally {
                    if (cmd.hasOption(OUTPUT_FILE_PARAM)) {
                        out.close();
                    }
                }
            } catch (ArticleInformationExtractionException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static Options createCommandLineOptions() {
        Options options = new Options();
        Option help = new Option("help", "print this message");
        options.addOption(help);
        Option outputFile = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("path to the file on which to save the output")
                .create(OUTPUT_FILE_PARAM);
        options.addOption(outputFile);
        return options;
    }

    private static void printHelp(Options options) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        String header = "Simple tool used to extract meaningful information (author, publish date, content, comments," +
                " etc.) about an web article page.";
        String footer = "LinkResearchTools job application test";
        formatter.printHelp("content-extractor [options] [url]", header, options, footer);
    }
}
