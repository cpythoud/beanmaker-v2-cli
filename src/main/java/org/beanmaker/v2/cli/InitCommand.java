package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;

import java.io.IOException;

import java.util.concurrent.Callable;

@Command(name = "init", description = "Initialize a new project")
class InitCommand implements Callable<Integer> {

    @Option(
            names = { "-n", "--name" },
            defaultValue =  "unnamed",
            paramLabel = "<name>",
            description = "project name (default: ${DEFAULT-VALUE})"
    )
    String name;

    @Option(names = "--description", paramLabel = "<description>", description = "short description of the project")
    String description;

    @Option(names = { "--db", "--database"}, required = true, paramLabel = "<database code>", description = "database to connect to")
    String database;

    @Option(names = { "--dp", "--default-package" }, required = true, paramLabel = "<package name>", description = "default package for generated beans")
    String defaultPackage;

    @Option(
            names = "--gen-source-dir",
            defaultValue = "src/main/java",
            paramLabel = "<path to source dir>",
            description = "set where to save generated source files (default: ${DEFAULT-VALUE})"
    )
    String genSourceDir;

    @Override
    public Integer call() throws IOException, XPathException, ParserConfigurationException, SAXException {
        var msg = new Console(ConsoleType.MESSAGES);

        // * Load and check existence of asset config file
        var assetsData = new AssetsData();
        if (CommandHelper.missingAssetConfiguration(assetsData, msg))
            return ReturnCode.USER_ERROR.code();

        // * Check that we are effectively trying to create a new project and not editing an existing one
        var projectData = new ProjectData();
        if (projectData.hasConfigFile()) {
            msg.status(Status.ERROR)
                    .printStatus()
                    .print(projectData.getConfigFilename() + " already exists. Please use ")
                    .print("project", Console.COMMAND_STYLE)
                    .println(" command to modify it.");
            return ReturnCode.USER_ERROR.code();
        }

        // * Collect data
        projectData.setName(name);
        if (description != null)
            projectData.setDescription(description);
        projectData.setDatabase(database);
        projectData.setDefaultPackage(defaultPackage);
        projectData.setGenSourceDir(genSourceDir);

        // * Check database code
        if (CommandHelper.unknownDatabaseConfigurationInProject(assetsData, msg, projectData.getDatabase()))
            return ReturnCode.USER_ERROR.code();

        projectData.writeConfigFile();
        msg.ok("Configuration file created successfully.");
        return ReturnCode.SUCCESS.code();
    }

}
