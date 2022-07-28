package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.net.URISyntaxException;

import java.util.concurrent.Callable;

@Command(name = "table", description = "Manage table to bean configuration")
public class TableCommand implements Callable<Integer> {

    @Option(names = { "-b", "--bean" }, paramLabel = "<bean-name>", description = "name of bean associated with table")
    String beanName;

    @Option(names = { "-c", "--current"}, description = "make table the current one for 'bean' commands")
    boolean current = false;

    @Option(
            names = { "-p", "--package" },
            paramLabel = "<package-name>",
            description = "package containing the bean, will use default project package if not provided " +
                    "when creating new configuration file")
    String packageName;

    @Option(names = { "-s", "--show"}, description = "show table fields and configuration")
    boolean show = false;

    @CommandLine.Parameters(index = "0", paramLabel = "<table-name>", description = "Database table to work on")
    String table;

    @Override
    public Integer call() throws ParserConfigurationException, IOException, SAXException, URISyntaxException, XPathException {
        var msg = Console.MESSAGES;

        // * Load and check existence of asset config file
        var assetsData = new AssetsData();
        if (CommandHelper.missingAssetConfiguration(assetsData, msg))
            return ReturnCode.USER_ERROR.code();

        // * Load and check existence of project config file
        var projectData = new ProjectData();
        if (CommandHelper.missingProjectConfiguration(projectData, msg, "table"))
            return ReturnCode.USER_ERROR.code();

        // * Check database code
        if (CommandHelper.unknownDatabaseConfigurationInProject(assetsData, msg, projectData.getDatabase()))
            return ReturnCode.USER_ERROR.code();

        // * Retrieve table data
        var tableData = new TableData(table);

        // * Obtain database data and check table existence if no configuration exist for it yet
        var databaseConfig = assetsData.getDatabaseConfig(projectData.getDatabase());
        if (!tableData.hasConfigFile() && !databaseConfig.hasTable(table)) {  // ! for performance reasons (and for now) we assume that the table exists if there is a config file
            msg.println(Status.ERROR, "no table '" + table + "' in database '" + projectData.getDatabase() + "'.", true);
            msg.print(Status.ERROR, "You might want to check the table and/or use the ")
                    .print(Status.ERROR, "database tables", Console.COMMAND_STYLE)
                    .println(Status.ERROR, " command to list the available tables.");
            return ReturnCode.USER_ERROR.code();
        }

        // * No option passed, notify and exit
        if (beanName == null && !current && packageName == null && !show) {
            msg.print(Status.NOTICE, "no option has been provided. Configuration unchanged. To see the table configuration use the ", true)
                    .print(Status.NOTICE, "show", Console.COMMAND_STYLE)
                    .println(Status.NOTICE, " command.");
            return ReturnCode.SUCCESS.code();
        }

        // * Check if table must be made current
        if (current) {
            tableData.makeCurrent();
            msg.println(Status.OK, table + " is now the current table.");
        }

        // * Retrieve or create table data

        if (tableData.hasConfigFile()) {
            boolean configChanged = false;
            if (beanName != null)
                configChanged = tableData.changeBeanName(beanName);
            if (packageName != null)
                configChanged = tableData.changePackageName(packageName) || configChanged;
            if (configChanged) {
                msg.println(Status.OK, "Config file " + tableData.getConfigFilename() + " has been successfully updated.");
                tableData.writeConfigFile();
            }
        } else {
            if (beanName == null) {
                if (packageName != null) {
                    msg.println(Status.ERROR, "can't create new config file for table if --bean value has not been specified", true);
                    return ReturnCode.USER_ERROR.code();
                }
            } else {
                tableData.setName(table);
                tableData.setBeanName(beanName);
                if (packageName == null)
                    tableData.setPackageName(projectData.getDefaultPackage());
                else
                    tableData.setPackageName(packageName);
                msg.println(Status.OK, "Config file " + tableData.getConfigFilename() + " has been successfully created.");
                tableData.writeConfigFile();
            }
        }

        // * Check if configuration must be shown
        if (show)
            Console.DATA.println(tableData.getTabularRepresentation(databaseConfig.getColumns(table)));

        return ReturnCode.SUCCESS.code();
    }

}
