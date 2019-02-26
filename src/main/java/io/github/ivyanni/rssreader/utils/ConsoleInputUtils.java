package io.github.ivyanni.rssreader.utils;

import io.github.ivyanni.rssreader.RomeAttributesHolder;
import io.github.ivyanni.rssreader.constants.CLIConstants;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Ilia Vianni on 24.02.2019.
 */
public class ConsoleInputUtils {

    public static String inputFilename(Scanner scanner) {
        String resultFilename = null;
        while (resultFilename == null) {
            System.out.print(CLIConstants.ENTER_FILENAME_MESSAGE);
            String fileName = scanner.nextLine();
            File file = new File(fileName);
            if (!file.canRead() && !file.canWrite() && !file.isFile()) {
                resultFilename = fileName;
            }
        }
        return resultFilename;
    }

    public static Long inputNumber(Scanner scanner, String message) {
        Long resultTimeout = null;
        while (resultTimeout == null) {
            System.out.print(message);
            String timeoutStr = scanner.nextLine();
            try {
                Long timeout = Long.parseLong(timeoutStr);
                if (timeout > 0) {
                    resultTimeout = timeout;
                } else {
                    System.out.println(CLIConstants.INCORRECT_NUMBER_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                System.out.println(CLIConstants.INCORRECT_NUMBER_MESSAGE);
            }
        }
        return resultTimeout;
    }

    public static String inputFeedName(Scanner scanner, Set<String> existingNames, boolean unique) {
        String resultName = null;
        while (resultName == null) {
            System.out.print(CLIConstants.ENTER_FEED_NAME_MESSAGE);
            String feedName = scanner.nextLine();
            if (unique == existingNames.contains(feedName)) {
                System.out.println(CLIConstants.INCORRECT_FEEDNAME_MESSAGE);
            } else resultName = feedName;
        }
        return resultName;
    }

    public static List<String> inputParameters(Scanner scanner) {
        Set<String> allowedParams = RomeAttributesHolder.getAvailableAttributes();
        System.out.println(CLIConstants.ALLOWED_PARAMETERS_MESSAGE + String.join(", ", allowedParams));
        System.out.print(CLIConstants.ENTER_PARAMETERS_MESSAGE);
        String paramLine = scanner.nextLine();
        if (paramLine.isEmpty()) {
            return List.copyOf(allowedParams);
        } else {
            paramLine = paramLine.replaceAll("\\s*", "");
            return Arrays.asList(paramLine.split("\\s*,\\s*"));
        }
    }

    public static URL inputFeedUrl(Scanner scanner) {
        URL feedUrl = null;
        while (feedUrl == null) {
            System.out.print(CLIConstants.ENTER_CORRECT_URL_MESSAGE);
            String enteredUrl = scanner.nextLine();
            try {
                feedUrl = new URL(enteredUrl);
            } catch (MalformedURLException ex) {
                System.out.println(CLIConstants.INCORRECT_URL_ENTERED_MESSAGE);
            }
        }
        return feedUrl;
    }
}
