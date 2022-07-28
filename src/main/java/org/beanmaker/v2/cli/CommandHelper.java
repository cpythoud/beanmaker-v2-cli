package org.beanmaker.v2.cli;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;

import java.util.Collection;
import java.util.List;

class CommandHelper {

    static boolean missingProjectConfiguration(ProjectData projectData, Console msg, String command) {
        if (!projectData.hasConfigFile()) {
            msg.print(Status.ERROR, projectData.getConfigFilename() + " does not exist. Please use ", true)
                    .print(Status.ERROR, "init", Console.COMMAND_STYLE)
                    .println(Status.ERROR, " command to create it. Or you might be in the wrong directory.");
            msg.print(Status.WARNING, "You need a project configuration file before you can use command ")
                    .print(Status.WARNING, command).println(Status.WARNING, ".");
            return true;
        }

        return false;
    }

    static boolean missingAssetConfiguration(AssetsData assetsData, Console msg) {
        if (!assetsData.hasConfigFile()) {
            msg.print(Status.ERROR, "there is no " + assetsData.getConfigFilename()
                            + " in your home directory. To automatically create one, use either the ", true)
                    .print(Status.ERROR, "ssh add", Console.COMMAND_STYLE)
                    .print(Status.ERROR, " or ")
                    .print(Status.ERROR, "database add", Console.COMMAND_STYLE)
                    .println(Status.ERROR, " command. Or if you have a " + assetsData.getConfigFilename()
                            + " somewhere else, please move it to your home directory ("
                            + Path.of(System.getProperty("user.home")) + ").");
            return true;
        }

        return false;
    }

    static boolean missingDatabaseConfiguration(AssetsData assetsData, Console msg, String dbCode) {
        if (!assetsData.hasDatabaseWithCode(dbCode)) {
            msg.println(Status.ERROR, "no database configuration with code '" + dbCode + "' exists.", true);
            printGeneralDatabaseTroubleshootingAdvice(msg);
            return true;
        }

        return false;
    }

    private static void printGeneralDatabaseTroubleshootingAdvice(Console msg) {
        msg.print(Status.ERROR, "You might want to use the ")
                .print(Status.ERROR, "database list", Console.COMMAND_STYLE)
                .print(Status.ERROR, " command to view a list of available database configuration or use the ")
                .print(Status.ERROR, "database add", Console.COMMAND_STYLE)
                .println(Status.ERROR, " command to create a new configuration.");
    }

    static boolean unknownDatabaseConfigurationInProject(AssetsData assetsData, Console msg, String dbCode) {
        if (!assetsData.hasDatabaseWithCode(dbCode)) {
            msg.println(Status.ERROR, "project configuration references database with code '" + dbCode
                    + ", but no such database is present in the assets config file. You might want to use the ", true);
            msg.print(Status.ERROR, "You might want to use the ")
                    .print(Status.ERROR, "project --show", Console.COMMAND_STYLE)
                    .println(Status.ERROR, " to check the database code.");
            printGeneralDatabaseTroubleshootingAdvice(msg);
            return true;
        }

        return false;
    }

    // TODO: find a better library than commons-io for this
    // TODO: need a library that can resolve things like *test*
    // TODO: or go regex on this...
    static List<String> wildcardFilter(Collection<String> list, String filter) {
        return list.stream()
                .filter(string -> FilenameUtils.wildcardMatch(string, filter))
                .sorted()
                .toList();
    }

    private CommandHelper() { }

}
