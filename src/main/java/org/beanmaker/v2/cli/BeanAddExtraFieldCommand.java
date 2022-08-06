package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Parameters;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "add-extra-field", aliases = { "aef" }, description = "Create extra non-database-based field")
class BeanAddExtraFieldCommand implements Callable<Integer> {

    @Option(names = { "--jt", "--java-type" }, paramLabel = "<java-type>", required = true, description = "change java type")
    String javaType;

    @Option(names = { "--ic", "--initialization-code" }, paramLabel = "<java expression>", description = "specify field initialization code")
    String initializationCode;

    @ArgGroup()
    BeanEditExtraFieldCommand.IsFinal isFinal;

    static class IsFinal {
        @Option(names = { "-f", "--final" }, description = "mark field as final")
        boolean isFinal;

        @Option(names = { "--nf", "--not-final" }, description = "mark field as not final")
        boolean notFinal;
    }

    @Option(names = { "--ri", "--required-import" }, paramLabel = "<import>", description = "specify a required import")
    String[] imports;

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

        // * Check if extra field has already been defined
        if (tableData.extraFieldExists(javaName)) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .println("Extra field " + javaName + " already exists.");
            CommandHelper.printShowExtraFieldHelp(msg);
            return ReturnCode.USER_ERROR.code();
        }

        // * Process options
        var extraField = new ExtraFieldConfig(javaType, javaName);
        if (initializationCode != null)
            extraField.setInitialization(initializationCode);
        extraField.setFinal(isFinal != null && isFinal.isFinal);
        if (imports != null)
            for (String importData: imports)
                extraField.addImport(importData);
        tableData.addExtraField(extraField);

        // * Write new configuration to file
        tableData.writeConfigFile();
        msg.ok("Extra field " + javaName + " was added and project configuration file has been updated.");
        return ReturnCode.SUCCESS.code();
    }

}
