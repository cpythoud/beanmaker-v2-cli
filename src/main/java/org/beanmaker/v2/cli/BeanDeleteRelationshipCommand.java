package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "delete-relationship", aliases = { "dr" }, description = "Delete a relationship with an other table")
class BeanDeleteRelationshipCommand implements Callable<Integer> {

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

        // * Obtain confirmation of deletion
        if (CommandHelper.confirm("Are you sure you want to delete the relationship anchored to java field " + javaName + "?", msg)) {
            // * Update configuration
            tableData.deleteRelationship(javaName);

            // * Write new configuration to file
            tableData.writeConfigFile();
            msg.ok("Relationship " + javaName + " has been deleted.");
        } else
            msg.notice("Relationship " + javaName + " was NOT deleted.");

        return ReturnCode.SUCCESS.code();
    }

}
