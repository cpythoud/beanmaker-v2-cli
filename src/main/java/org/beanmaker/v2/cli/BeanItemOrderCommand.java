package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "item-order", aliases = { "io" }, description = "Manage item_order configuration")
public class BeanItemOrderCommand implements Callable<Integer> {

    @ArgGroup(multiplicity = "1")
    Scope scope;

    static class Scope {
        @Option(names = { "--af", "--associated-field" }, description = "link item_order value to a table field")
        String associatedField;

        @Option(names = { "-g", "--global" }, description = "item_order scope is the whole table")
        boolean global;
    }

    @ParentCommand
    private BeanCommand parent;

    @Override
    public Integer call() throws XPathException, IOException, ParserConfigurationException, SAXException {
        var msg = new Console(ConsoleType.MESSAGES);

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

        // * Check & retrieve table data
        var tableData = CommandHelper.checkAndRetrieveTableData(msg).orElse(null);
        if (tableData == null)
            return ReturnCode.USER_ERROR.code();

        // * Retrieve columns information and check if table has item_order field
        var columns = assetsData.getDatabaseConfig(projectData.getDatabase()).getColumns(tableData.getName());
        if (!columns.hasItemOrder()) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .println("Table " + tableData.getName() + " has no item_order field.");
            return ReturnCode.USER_ERROR.code();
        }

        // * Process options
        if (scope.associatedField != null && !columns.hasSQLField(scope.associatedField)) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .print("Table " + tableData.getName() + " has no field " + scope.associatedField + ".");
            msg.status(Status.WARNING)
                    .print("You can use the ")
                    .print("bean show", Console.COMMAND_STYLE)
                    .println(" command to list existing fields.");
            return ReturnCode.USER_ERROR.code();
        }
        boolean configChanged = tableData.changeItemOrderAssociatedField(scope.associatedField);

        // * If options do not change configuration, notify and exit
        if (!configChanged) {
            msg.status(Status.NOTICE)
                    .printStatus()
                    .print("No difference with previous item_order configuration. Configuration file was not modified. To see the bean configuration use the ")
                    .print("bean show", Console.COMMAND_STYLE)
                    .println(" command.");
            return ReturnCode.SUCCESS.code();
        }

        // * Write new configuration to file
        tableData.writeConfigFile();
        msg.ok("item_order data was updated and written to table configuration file.");
        return ReturnCode.SUCCESS.code();
    }

}
