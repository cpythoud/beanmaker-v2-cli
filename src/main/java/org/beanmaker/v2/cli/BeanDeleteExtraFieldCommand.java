package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "delete-extra-field", aliases = { "def" }, description = "Delete extra non-database-based field")
class BeanDeleteExtraFieldCommand implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "<java-fieldname>", description = "name of extra field (can type only beginning of field name)")
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

        // * Check if extra field is not defined
        if (CommandHelper.missingExtraField(tableData, msg, javaName))
            return ReturnCode.USER_ERROR.code();

        // * Obtain confirmation of deletion
        if (CommandHelper.confirm("Are you sure you want to delete the extra field referenced by " + javaName + "?", msg)) {
            // * Update configuration
            tableData.deleteExtraField(javaName);

            // * Write new configuration to file
            tableData.writeConfigFile();
            msg.ok("Extra field " + javaName + " has been deleted.");
        } else
            msg.notice("Extra field " + javaName + " was NOT deleted.");

        return ReturnCode.SUCCESS.code();
    }

}
