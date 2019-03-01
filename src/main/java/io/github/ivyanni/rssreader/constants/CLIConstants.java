package io.github.ivyanni.rssreader.constants;

/**
 * Constants used in Command Line Interface.
 *
 * @author Ilia Vianni on 24.02.2019.
 */
public class CLIConstants {
    /* Console messages */
    public static final String WELCOME_MESSAGE = "Welcome to RSS Reader";
    public static final String ENTER_COMMAND_MESSAGE = "Enter command (add|modify|remove|list|exit): ";
    public static final String ENTER_COMMAND_MODIFY_MESSAGE = "What do you want to modify (url|delay|chunk|filename|params): ";
    public static final String ENTER_CORRECT_URL_MESSAGE = "Enter correct source URL: ";
    public static final String ENTER_DELAY_MESSAGE = "Enter delay (sec): ";
    public static final String ENTER_CHUNK_SIZE_MESSAGE = "Enter chunk size: ";
    public static final String ENTER_FILENAME_MESSAGE = "Enter output file name  (or leave empty for default): ";
    public static final String INCORRECT_URL_ENTERED_MESSAGE = "Specified incorrect URL";
    public static final String EXISTING_FEEDS_MESSAGE = "Existing feeds:";
    public static final String NO_EXISTING_FEEDS_MESSAGE = "No existing feeds";
    public static final String FEED_ADDED_MESSAGE = "Feed was successfully added";
    public static final String FEED_REMOVED_MESSAGE = "Feed was successfully removed";
    public static final String ENTER_FEED_NAME_MESSAGE = "Enter feed name: ";
    public static final String INCORRECT_FEEDNAME_MESSAGE = "Specified feed name is incorrect";
    public static final String ALLOWED_PARAMETERS_MESSAGE = "Allowed parameters: ";
    public static final String ENTER_PARAMETERS_MESSAGE = "Specify parameters (separated by comma): ";
    public static final String INCORRECT_NUMBER_MESSAGE = "Specified number is incorrect";
    public static final String INCORRECT_FILENAME_MESSAGE = "Incorrect file name";
    public static final String INCORRECT_FEED_MESSAGE = "Provided incorrect feed";

    /* Console commands */
    public static final String ADD_NEW_FEED_COMMAND = "add";
    public static final String CHANGE_FEED_PARAMS_COMMAND = "modify";
    public static final String REMOVE_FEED_COMMAND = "remove";
    public static final String SHOW_EXISTING_FEEDS_COMMAND = "list";
    public static final String EXIT_COMMAND = "exit";
    public static final String CHANGE_URL_COMMAND = "url";
    public static final String CHANGE_CHUNK_SIZE_COMMAND = "chunk";
    public static final String CHANGE_PARAMETERS_COMMAND = "params";
    public static final String CHANGE_DELAY_COMMAND = "delay";
    public static final String CHANGE_FILENAME_COMMAND = "filename";
}
