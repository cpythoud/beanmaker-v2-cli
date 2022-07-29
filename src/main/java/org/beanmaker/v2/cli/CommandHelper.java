package org.beanmaker.v2.cli;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;

import java.util.Collection;
import java.util.List;

class CommandHelper {

    static boolean missingProjectConfiguration(ProjectData projectData, Console msg, String command) {
        if (!projectData.hasConfigFile()) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .print(projectData.getConfigFilename() + " does not exist. Please use ")
                    .print("init", Console.COMMAND_STYLE)
                    .println(" command to create it. Or you might be in the wrong directory.");
            msg.status(Status.WARNING)
                    .print("You need a project configuration file before you can use command ")
                    .print(command, Console.COMMAND_STYLE)
                    .println(".");
            return true;
        }

        return false;
    }

    static boolean missingAssetConfiguration(AssetsData assetsData, Console msg) {
        if (!assetsData.hasConfigFile()) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .print("there is no " + assetsData.getConfigFilename() + " in your home directory. To automatically create one, use either the ")
                    .print("ssh add", Console.COMMAND_STYLE)
                    .print(" or ")
                    .print("database add", Console.COMMAND_STYLE)
                    .println(" command. Or if you have a " + assetsData.getConfigFilename()
                            + " somewhere else, please move it to your home directory ("
                            + Path.of(System.getProperty("user.home")) + ").");
            return true;
        }

        return false;
    }

    static boolean missingDatabaseConfiguration(AssetsData assetsData, Console msg, String dbCode) {
        if (!assetsData.hasDatabaseWithCode(dbCode)) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .println("no database configuration with code '" + dbCode + "' exists.");
            printGeneralDatabaseTroubleshootingAdvice(msg);
            return true;
        }

        return false;
    }

    private static void printGeneralDatabaseTroubleshootingAdvice(Console msg) {
        msg.status(Status.WARNING)
                .print("You might want to use the ")
                .print("database list", Console.COMMAND_STYLE)
                .print(" command to view a list of available database configuration or use the ")
                .print("database add", Console.COMMAND_STYLE)
                .println(" command to create a new configuration.");
    }

    static boolean unknownDatabaseConfigurationInProject(AssetsData assetsData, Console msg, String dbCode) {
        if (!assetsData.hasDatabaseWithCode(dbCode)) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .println("project configuration references database with code '" + dbCode
                            + ", but no such database is present in the assets config file.");
            msg.status(Status.WARNING)
                    .print("You might want to use the ")
                    .print("project --show", Console.COMMAND_STYLE)
                    .println(" to check the database code.");
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
