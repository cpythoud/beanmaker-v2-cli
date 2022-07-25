package org.beanmaker.v2.cli;

import org.xml.sax.SAXException;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathExpressionException;

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

    @Option(names = "--default-package", required = true, paramLabel = "<package name>", description = "default package for generated beans")
    String defaultPackage;

    @Option(
            names = "--gen-source-dir",
            defaultValue = "src/main/java",
            paramLabel = "<path to source dir>",
            description = "set where to save generated source files (default: ${DEFAULT-VALUE})"
    )
    String genSourceDir;

    @Override
    public Integer call() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        var out = Console.MESSAGES;
        var projectData = new ProjectData();
        if (projectData.hasConfigFile()) {
            out.print(Status.ERROR, projectData.getConfigFilename() + " already exists. Please use ", true)
                    .print(Status.ERROR, "project", Console.COMMAND_STYLE)
                    .println(Status.ERROR, " command to modify it.");
            return ReturnCode.USER_ERROR.code();
        }

        // TODO: verify parameters? i.e., database code
        projectData.setName(name);
        if (description != null)
            projectData.setDescription(description);
        projectData.setDatabase(database);
        projectData.setDefaultPackage(defaultPackage);
        projectData.setGenSourceDir(genSourceDir);

        projectData.writeConfigFile();
        out.println(Status.OK, "Configuration file created successfully.");
        return ReturnCode.SUCCESS.code();
    }

}
