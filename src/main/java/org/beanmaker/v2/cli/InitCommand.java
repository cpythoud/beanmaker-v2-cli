package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.ValueXMLAttribute;
import org.jcodegen.html.xmlbase.XMLElement;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.concurrent.Callable;

import static org.beanmaker.v2.cli.BeanmakerCommand.PROJECT_CONFIG_FILE;

@Command(name = "init", description = "Initialize a new project")
class InitCommand implements Callable<Integer> {

    // TODO: move to appropriate place where it can be referenced by all configuration generating classes
    private static final String XML_PRELUDE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";

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
    public Integer call() throws IOException {
        var configFile = Path.of(PROJECT_CONFIG_FILE);
        if (Files.exists(configFile)) {
            // TODO: introduce ANSI stuff
            System.err.println(PROJECT_CONFIG_FILE + " already exists. Please use 'project' command to modify it.");
            return ReturnCode.USER_ERROR.code();
        }

        Files.writeString(configFile, createIntialConfig(), StandardCharsets.UTF_8);

        return ReturnCode.SUCCESS.code();
    }

    private String createIntialConfig() { // ! I refuse to waste my time with the W3C DOM 'tools'
        // TODO: verify parameters? i.e., database code

        var root = getRootElement("project", "config");
        root.addChild(getElement("name", name));
        if (description != null)
            root.addChild(getElement("description", description));
        root.addChild(getElement("database", database));
        root.addChild(getElement("default-package", defaultPackage));
        root.addChild(getElement("gen-source-dir", genSourceDir));

        return XML_PRELUDE + root;
    }

    // TODO: move function to the appropriate place where it can be called by other configuration generating classes
    private XMLElement getRootElement(String name, String schemaReference) {
        var element = new XMLElement(name);
        element.addAttribute(new ValueXMLAttribute(
                "xmlns",
                "https://schema.beanmaker.org/beanmaker-" + schemaReference
        ));
        element.addAttribute(new ValueXMLAttribute(
                "xmlns:xsi",
                "http://www.w3.org/2001/XMLSchema-instance"
        ));
        element.addAttribute(new ValueXMLAttribute(
                "xsi:schemaLocation",
                "https://schema.beanmaker.org/beanmaker-" + schemaReference + " beanmaker-" + schemaReference + ".xsd"
        ));
        return element;
    }

    // TODO: move function to the appropriate place where it can be called by other configuration generating classes
    private XMLElement getElement(String name, String value) {
        var element = new XMLElement(name, value);
        element.setOnOneLine(true);
        return element;
    }

}
