package io.github.ivyanni.rssreader.utils;

import io.github.ivyanni.rssreader.model.constants.CLIConstants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Contains methods that ask user to input value and validate it.
 *
 * @author Ilia Vianni on 24.02.2019.
 */
public class CommandLineInputUtils {

    /**
     * Asks user to input file name and validates it.
     *
     * @param scanner User's input Scanner
     * @param feedUrl Feed's URL
     * @return String that contains valid file name
     */
    public static String inputFilename(Scanner scanner, URL feedUrl) {
        System.out.print(CLIConstants.ENTER_FILENAME_MESSAGE);
        String fileName = scanner.nextLine();
        if (fileName.isBlank()) {
            int divider = feedUrl.toString().lastIndexOf('/');
            fileName = feedUrl.toString().substring(divider + 1);
        }
        fileName = fileName.replaceAll("[\\\\/:\"*?<>|]+", "_");
        return "output\\" + fileName + ".out";
    }

    /**
     * Asks user to input number value and validates it.
     *
     * @param scanner User's input Scanner
     * @param message Message that will be shown to user
     * @return valid Long value
     */
    public static Long inputNumber(Scanner scanner, String message) {
        Long resultNumber = null;
        while (resultNumber == null) {
            System.out.print(message);
            String numberStr = scanner.nextLine();
            try {
                Long number = Long.parseLong(numberStr);
                if (number > 0) {
                    resultNumber = number;
                } else {
                    System.out.println(CLIConstants.INCORRECT_NUMBER_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                System.out.println(CLIConstants.INCORRECT_NUMBER_MESSAGE);
            }
        }
        return resultNumber;
    }

    /**
     * Asks user to input feed's name and validates it.
     *
     * @param scanner       User's input Scanner
     * @param existingNames Collection with existing names
     * @param unique        Shows should user enter unique name or existing
     * @return valid unique/existing feed's name
     */
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

    /**
     * Asks user to input feed entry parameters and validates it.
     *
     * @param scanner User's input Scanner
     * @return collection of selected parameters
     */
    public static List<String> inputParameters(Scanner scanner) {
        Set<String> allowedParams = RomeAttributesMapper.getAvailableAttributes();
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

    /**
     * Asks user to input feed URL and validates it.
     *
     * @param scanner User's input Scanner
     * @return valid feed's URL
     */
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
