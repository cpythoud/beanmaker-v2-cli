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

@Command(name = "add-relationship", aliases = { "ar" }, description = "Create a relationship with an other table")
class BeanAddRelationshipCommand implements Callable<Integer> {

    @Option(names = { "--jt", "--java-type" }, paramLabel = "<java-type>", required = true, description = "change java type")
    String javaType;

    @Option(names = { "--at", "--associated-table" }, paramLabel = "<table>", required = true, description = "specify associated table")
    String table;

    @Option(names = { "--sif", "--sql-id-field" }, paramLabel = "<id_field>", required = true, description = "specify id field in associated table")
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

        // * Check if relationship has already been defined
        if (tableData.relationshipExists(javaName)) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .println("Field " + javaName + " is already in use as a relationship marker.");
            msg.status(Status.WARNING)
                    .print("You can use the ")
                    .print("bean show", Console.COMMAND_STYLE)
                    .println(" to list existing relationships.");
            return ReturnCode.USER_ERROR.code();
        }

        // * Process options
        tableData.addRelationship(new RelationshipConfig(table, javaType, javaName, idField));

        // * Write new configuration to file
        tableData.writeConfigFile();
        msg.ok("Relationship " + javaName + " was added and project configuration file has been updated.");
        return ReturnCode.SUCCESS.code();
    }

}
