package org.beanmaker.v2.cli;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

class CommandHelper {

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

    static boolean missingDatabaseConfiguration(AssetsData assetsData, Console msg, String code) {
        if (!assetsData.hasDatabaseWithCode(code)) {
            msg.print(Status.ERROR, "no database configuration with code '" + code
                            + "' exists. You might want to use the ", true)
                    .print(Status.ERROR, "database list", Console.COMMAND_STYLE)
                    .print(Status.ERROR, " command to view a list of available database configuration or use the ")
                    .print(Status.ERROR, "database add", Console.COMMAND_STYLE)
                    .println(Status.ERROR, " command to create a new configuration.");
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
