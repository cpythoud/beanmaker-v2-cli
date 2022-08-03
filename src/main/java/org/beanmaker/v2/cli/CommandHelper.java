package org.beanmaker.v2.cli;

import org.apache.commons.io.FilenameUtils;
import org.beanmaker.v2.codegen.Column;
import org.beanmaker.v2.util.Strings;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.nio.file.Path;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
                    .print("There is no " + assetsData.getConfigFilename() + " in your home directory. To automatically create one, use either the ")
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
                    .println("No database configuration with code '" + dbCode + "' exists.");
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
                    .println("Project configuration references database with code '" + dbCode
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

    static boolean unknownJavaType(String javaType, Console msg) {
        if (!Column.JAVA_TYPES.contains(javaType)) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .println(javaType + " is not a supported java type.");
            msg.status(Status.WARNING)
                    .println("The supported java types are: " + Strings.concatWithSeparator(", ", Column.JAVA_TYPES));
            return true;
        }

        return false;
    }

    static Optional<TableData> checkAndRetrieveTableData(Console msg) throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        var tableData = TableData.getCurrent().orElse(null);

        if (tableData == null) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .print("Current table is unknown. Please use the ")
                    .print("table --current", Console.COMMAND_STYLE)
                    .println(" to specify the current table.");
            return Optional.empty();
        }

        if (!tableData.hasConfigFile()) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .print("Current table has no associated config file. To create an initial config, you need to specify the name of the associated bean with the ")
                    .print("table --bean", Console.COMMAND_STYLE)
                    .println(".");
            return Optional.empty();
        }

        return Optional.of(tableData);
    }

    static boolean missingRelationship(TableData tableData, Console msg, String javaName) {
        if (!tableData.relationshipExists(javaName)) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .println("There is no relationship anchored to java field " + javaName + ".");
            printShowRelationshipsHelp(msg);
            return true;
        }

        return false;
    }

    static void printShowRelationshipsHelp(Console msg) {
        msg.status(Status.WARNING)
                .print("You can use the ")
                .print("bean show", Console.COMMAND_STYLE)
                .println(" command to list existing relationships.");
    }

}
