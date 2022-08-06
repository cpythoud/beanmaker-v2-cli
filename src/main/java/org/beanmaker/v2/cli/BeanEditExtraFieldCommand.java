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

@Command(name = "edit-extra-field", aliases = { "eef" }, description = "Manage extra non-database-based field")
class BeanEditExtraFieldCommand implements Callable<Integer> {

    @Option(names = { "--jt", "--java-type" }, paramLabel = "<java-type>", description = "change java type")
    String javaType;

    @Option(names = { "--ic", "--initialization-code" }, paramLabel = "<java expression>", description = "specify field initialization code")
    String initializationCode;

    @ArgGroup()
    IsFinal isFinal;

    static class IsFinal {
        @Option(names = { "-f", "--final" }, description = "mark field as final")
        boolean isFinal;

        @Option(names = { "--nf", "--not-final" }, description = "mark field as not final")
        boolean notFinal;
    }

    @Option(names = { "--ci", "--clear--imports" }, description = "mark field as final")
    boolean clearImports;

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

        // * Check if extra field is not defined
        if (CommandHelper.missingExtraField(tableData, msg, javaName))
            return ReturnCode.USER_ERROR.code();

        // * No option passed, notify and exit
        if (javaType == null && initializationCode == null && isFinal == null && !clearImports && (imports == null || imports.length == 0)) {
            CommandHelper.printNoBeanConfigChange(msg);
            return ReturnCode.SUCCESS.code();
        }

        // * Process options
        boolean configChanged = false;
        var extraField = tableData.getExtraField(javaName);
        if (javaType != null)
            configChanged = extraField.changeJavaType(javaType);
        if (initializationCode != null)
            configChanged = extraField.changeInitialization(initializationCode) || configChanged;
        if (isFinal != null)
            configChanged = extraField.changeFinal(isFinal.isFinal) || configChanged;
        if (clearImports) {
            if (extraField.hasImports()) {
                extraField.clearImports();
                configChanged = true;
            }
        }
        if (imports != null)
            for (String importData: imports)
                configChanged = extraField.updateImport(importData) || configChanged;
        // TODO: solve theoretical case when user clear the imports before reimporting the exact same data (= no change)

        // * If options do not change configuration, notify and exit
        if (!configChanged) {
            msg.status(Status.NOTICE)
                    .printStatus()
                    .print("No difference with previous extra field configuration. Configuration file was not modified. To see the configuration use the ")
                    .print("bean show", Console.COMMAND_STYLE)
                    .println(" command.");
            return ReturnCode.SUCCESS.code();
        }

        // * Write new configuration to file
        tableData.writeConfigFile();
        msg.ok("Extra field " + javaName + " was modified and project configuration file has been updated.");
        return ReturnCode.SUCCESS.code();
    }

}
