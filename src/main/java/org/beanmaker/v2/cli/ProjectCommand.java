package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathExpressionException;

import java.io.IOException;

import java.net.URISyntaxException;

import java.util.concurrent.Callable;

@Command(name = "project", description = "Manage project parameters")
class ProjectCommand implements Callable<Integer> {

    @Option(names = { "-n", "--name" }, paramLabel = "<name>", description = "change project name")
    String name;

    @Option(names = "--description", paramLabel = "<description>", description = "set short description of the project")
    String description;

    @Option(names = { "--db", "--database"}, paramLabel = "<database code>", description = "specify database to connect to")
    String database;

    @Option(names = "--default-package", paramLabel = "<package name>", description = "set default package for generated beans")
    String defaultPackage;

    @Option(names = "--gen-source-dir", paramLabel = "<path to source dir>", description = "set where to save generated source files")
    String genSourceDir;


    @Override
    public Integer call() throws ParserConfigurationException, IOException, SAXException, URISyntaxException, XPathExpressionException {
        var msg = Console.MESSAGES;
        var projectData = new ProjectData();

        if (!projectData.hasConfigFile()) {
            msg.print(Status.ERROR, projectData.getConfigFilename() + " does not exist. Please use ", true)
                    .print(Status.ERROR, "init", Console.COMMAND_STYLE)
                    .println(Status.ERROR, " command to create it. Or you might be in the wrong directory.");
            return ReturnCode.USER_ERROR.code();
        }

        // * No option passed, notify and exit
        if (name == null && description == null && database == null && defaultPackage == null && genSourceDir == null) {
            msg.print(Status.NOTICE, "no option has been provided. Configuration unchanged. To see the configuration use the ", true)
                    .print(Status.NOTICE, "show", Console.COMMAND_STYLE)
                    .println(Status.NOTICE, " command.");
            return ReturnCode.SUCCESS.code();
        }

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

        // * If options do not change configuration, notify and exit
        if (!configChanged) {
            msg.print(Status.NOTICE, "no difference with previous config. Configuration file was not modified. To see the configuration use the ", true)
                    .print(Status.NOTICE, "show", Console.COMMAND_STYLE)
                    .println(Status.NOTICE, " command.");
            return ReturnCode.SUCCESS.code();
        }

        // * Write new configuration to file
        projectData.writeConfigFile();
        msg.println(Status.OK, "Configuration file changed successfully.");
        return ReturnCode.SUCCESS.code();
    }

}
