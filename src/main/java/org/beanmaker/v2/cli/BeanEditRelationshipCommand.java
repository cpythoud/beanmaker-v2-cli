package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "edit-relationship", aliases = { "er" }, description = "Manage a relationship with an other table")
class BeanEditRelationshipCommand implements Callable<Integer> {

    @Option(names = { "--jt", "--java-type" }, paramLabel = "<java-type>", description = "change java type")
    String javaType;

    @Option(names = { "--at", "--associated-table" }, paramLabel = "<table>", description = "specify associated table")
    String table;

    @Option(names = { "--sif", "--sql-id-field" }, paramLabel = "<id_field>", description = "specify id field in associated table")
    String idField;

    @Parameters(index = "0", paramLabel = "<java-fieldname>", description = "name of java field for referencing the list")
    String javaName;

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

        // * Check if relationship is not defined
        if (CommandHelper.missingRelationship(tableData, msg, javaName))
            return ReturnCode.USER_ERROR.code();

        // * No option passed, notify and exit
        if (table == null && javaType == null && idField == null) {
            CommandHelper.printNoBeanConfigChange(msg);
            return ReturnCode.SUCCESS.code();
        }

        // * Process options
        var relationship = tableData.getRelationship(javaName);
        if (table == null)
            table = relationship.table();
        if (javaType == null)
            javaType = relationship.javaType();
        if (idField == null)
            idField = relationship.idField();
        var updatedRelationship = new RelationshipConfig(table, javaType, javaName, idField);

        // * No configuration change, notify and exit
        if (updatedRelationship.equals(relationship)) {
            msg.status(Status.NOTICE)
                    .printStatus()
                    .print("No difference with previous relationship configuration. Configuration file was not modified. To see the configuration use the ")
                    .print("bean show", Console.COMMAND_STYLE)
                    .println(" command.");
            return ReturnCode.SUCCESS.code();
        }

        // * Update configuration
        tableData.changeRelationship(new RelationshipConfig(table, javaType, javaName, idField));

        // * Write new configuration to file
        tableData.writeConfigFile();
        msg.ok("Relationship " + javaName + " was added and project configuration file has been updated.");
        return ReturnCode.SUCCESS.code();
    }

}
