package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.net.URISyntaxException;

import java.util.Collections;
import java.util.Set;

import java.util.concurrent.Callable;

@Command(name = "project", description = "Manage project parameters")
class ProjectCommand implements Callable<Integer> {

    @Option(names = { "-n", "--name" }, paramLabel = "<name>", description = "change project name")
    String name;

    @Option(names = "--description", paramLabel = "<description>", description = "set short description of the project")
    String description;

    @Option(names = { "--db", "--database"}, paramLabel = "<database code>", description = "specify database to connect to")
    String database;

    @Option(names = { "--dp", "--default-package" }, paramLabel = "<package name>", description = "set default package for generated beans")
    String defaultPackage;

    @Option(names = { "--gsd", "--gen-source-dir"}, paramLabel = "<path to source dir>", description = "set where to save generated source files")
    String genSourceDir;

    @Option(names = { "-s", "--show"}, description = "show project configuration")
    boolean show;

    @Option(names = { "--ap", "--activate-parameter"}, description = "activate code generator parameter")
    Set<ProjectParameter> activatedParameters;

    @Option(names = { "--dap", "--deactivate-parameter"}, description = "deactivate code generator parameter")
    Set<ProjectParameter> deactivatedParameters;


    @Override
    public Integer call() throws ParserConfigurationException, IOException, SAXException, URISyntaxException, XPathException {
        var msg = new Console(ConsoleType.MESSAGES);

        // * Load and check existence of asset config file
        var assetsData = new AssetsData();
        if (CommandHelper.missingAssetConfiguration(assetsData, msg))
            return ReturnCode.USER_ERROR.code();

        // * Check that we are effectively trying to edit an existing project
        var projectData = new ProjectData();
        if (CommandHelper.missingProjectConfiguration(projectData, msg, "project"))
            return ReturnCode.USER_ERROR.code();

        // * Check if data change options passed
        boolean dataChangeRequested = name != null
                || description != null
                || database != null
                || defaultPackage != null
                || genSourceDir != null
                || (activatedParameters != null && !activatedParameters.isEmpty())
                || (deactivatedParameters != null && !deactivatedParameters.isEmpty());

        // * No option passed, notify and exit
        if (!dataChangeRequested && !show) {
            msg.status(Status.NOTICE)
                    .printStatus()
                    .print("no option has been provided. Configuration unchanged. To see the configuration use the ")
                    .print("show", Console.COMMAND_STYLE)
                    .println(" command.");
            return ReturnCode.SUCCESS.code();
        }

        if (dataChangeRequested) {
            // * For each option present, record change and see if config has been modified
            boolean configChanged = false;
            if (name != null)
                configChanged = projectData.changeName(name);
            if (description != null)
                configChanged = projectData.changeDescription(description) || configChanged;
            if (database != null)
                configChanged = projectData.changeDatabase(database) || configChanged;
            if (defaultPackage != null)
                configChanged = projectData.changeDefaultPackage(defaultPackage) || configChanged;
            if (genSourceDir != null)
                configChanged = projectData.changeGenSourceDir(genSourceDir) || configChanged;
            if ((activatedParameters != null && !activatedParameters.isEmpty()) ||
                    (deactivatedParameters != null && !deactivatedParameters.isEmpty()))
                configChanged = projectData.updateParameters(activatedParameters, deactivatedParameters);

            // * If options do not change configuration, notify and exit
            if (!configChanged) {
                msg.status(Status.NOTICE)
                        .printStatus()
                        .print("no difference with previous config. Configuration file was not modified. To see the configuration use the ")
                        .print("show", Console.COMMAND_STYLE)
                        .println(" command.");
                return ReturnCode.SUCCESS.code();
            }

            // * Check database code
            if (CommandHelper.unknownDatabaseConfigurationInProject(assetsData, msg, projectData.getDatabase()))
                return ReturnCode.USER_ERROR.code();

            // * Check that parameters are not activated and deactivated at the same time
            if (activatedParameters != null && deactivatedParameters != null && !Collections.disjoint(activatedParameters, deactivatedParameters)) {
                msg.error("Cannot activate and deactivate the same parameter at the same time.");
                return ReturnCode.USER_ERROR.code();
            }

            // * Write new configuration to file
            projectData.writeConfigFile();
            msg.ok("Configuration file changed successfully.");
        }

        // * Show configuration if requested
        if (show)
            ConsoleType.DATA.println(projectData.getTabularRepresentation());

        return ReturnCode.SUCCESS.code();
    }

}
