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

@Command(name = "field", description = "Manage field data")
class BeanFieldCommand implements Callable<Integer> {

    @Option(names = { "--jt", "--java-type" }, paramLabel = "<java-type>", description = "change java type")
    String javaType;

    @Option(names = { "--jn", "--java-name" }, paramLabel = "<java-name>", description = "change java field name")
    String javaName;

    @ArgGroup()
    Required required;

    static class Required {
        @Option(names = { "-r", "--required" }, description = "mark field as required")
        boolean required;

        @Option(names = { "--nr", "--not-required" }, description = "mark field as not required")
        boolean notRequired;
    }

    @ArgGroup()
    Unique unique;

    static class Unique {
        @Option(names = { "-u", "--unique" }, description = "mark field as unique")
        boolean unique;

        @Option(names = { "--nu", "--not-unique" }, description = "mark field as not unique")
        boolean notUnique;
    }

    @Option(names = { "--abc", "--associated-bean-class" }, paramLabel = "<associated-bean-class>", description = "bean class associated to id_field")
    String associatedBeanClass;

    @Option(names = { "--dec", "--decimals" }, paramLabel = "<decimals>", description = "number of decimals for numeric fields")
    Integer decimals;

    @ArgGroup
    Sign sign;

    static class Sign {
        @Option(names = { "--pos", "--positive" }, description = "mark numeric field as positive only")
        boolean positive;

        @Option(names = { "--neg", "--negative" }, description = "mark numeric field that can be negative")
        boolean negative;
    }

    @Parameters(index = "0", paramLabel = "<db-field>", description = "name of field in database (can type only beginning of field name)")
    String dbField;

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

        // * No option passed, notify and exit
        if (javaType == null && javaName == null && required == null && unique == null && associatedBeanClass == null
                && decimals == null && sign == null)
        {
            msg.status(Status.NOTICE)
                    .printStatus()
                    .print("No option has been provided. Configuration unchanged. To see the bean configuration use the ")
                    .print("bean show", Console.COMMAND_STYLE)
                    .println(" command.");
            return ReturnCode.SUCCESS.code();
        }

        // * Retrieve field data from config or Columns
        var columns = assetsData.getDatabaseConfig(projectData.getDatabase()).getColumns(tableData.getName());
        var column = columns.getColumn(dbField).orElse(null);
        if (column == null) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .print("Table " + tableData.getName() +" has no field " + dbField + ". Please use the ")
                    .print("table --show " + tableData.getName(), Console.COMMAND_STYLE)
                    .println(" to list fields.");
            return ReturnCode.USER_ERROR.code();
        }
        var fieldConfig = tableData.getFieldConfig(dbField, column);

        // * Process options
        boolean configChanged = false;

        if (javaType != null) {
            if (CommandHelper.unknownJavaType(javaType, msg))
                return ReturnCode.USER_ERROR.code();
            configChanged = fieldConfig.changeJavaType(javaType);
        }

        if (javaName != null)
            configChanged = fieldConfig.changeJavaName(javaName) || configChanged;

        if (required != null) {
            configChanged = fieldConfig.changeRequired(required.required) || configChanged;
        }

        if (unique != null) {
            configChanged = fieldConfig.changeUnique(unique.unique) || configChanged;
        }

        if (associatedBeanClass != null)
            configChanged = fieldConfig.changeAssociatedBeanClass(associatedBeanClass) || configChanged;

        if (decimals != null) {
            configChanged = fieldConfig.changeDecimals(decimals) || configChanged;
        }

        if (sign != null) {
            configChanged = fieldConfig.changePositiveOnly(sign.positive) || configChanged;
        }

        // * If options do not change configuration, notify and exit
        if (!configChanged) {
            msg.status(Status.NOTICE)
                    .printStatus()
                    .print("No difference with previous field configuration. Configuration file was not modified. To see the bean configuration use the ")
                    .print("bean show " + dbField, Console.COMMAND_STYLE)
                    .println(" command.");
            return ReturnCode.SUCCESS.code();
        }

        // * Write new configuration to file
        tableData.updateField(fieldConfig, column);
        tableData.writeConfigFile();
        msg.ok("Field " + dbField + " data was updated and written to table configuration file.");
        return ReturnCode.SUCCESS.code();
    }

}
