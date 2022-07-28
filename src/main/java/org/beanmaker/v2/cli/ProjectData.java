package org.beanmaker.v2.cli;

import org.beanmaker.v2.util.Strings;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathExpressionException;

import java.io.IOException;

import java.util.Objects;

class ProjectData extends ConfigData {

    private String name;
    private String description;
    private String database;
    private String defaultPackage;
    private String genSourceDir;

    ProjectData() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        super(PROJECT_CONFIG_FILE, PROJECT_SCHEMA_FILE, false, false);

        if (hasConfigFile()) {
            name = getStringValue("/project/name/text()");
            String description = getStringValue("/project/description/text()");
            if (!Strings.isEmpty(description))
                this.description = description;
            database = getStringValue("/project/database/text()");
            defaultPackage = getStringValue("/project/default-package/text()");
            genSourceDir = getStringValue("/project/gen-source-dir/text()");
        }
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    boolean changeName(String name) {
        if (Objects.equals(this.name, name))
            return false;

        setName(name);
        return true;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    boolean changeDescription(String description) {
        if (Objects.equals(this.description, description))
            return false;

        setDescription(description);
        return true;
    }

    String getDatabase() {
        return database;
    }

    void setDatabase(String database) {
        this.database = database;
    }

    boolean changeDatabase(String database) {
        if (Objects.equals(this.database, database))
            return false;

        setDatabase(database);
        return true;
    }

    String getDefaultPackage() {
        return defaultPackage;
    }

    void setDefaultPackage(String defaultPackage) {
        this.defaultPackage = defaultPackage;
    }

    boolean changeDefaultPackage(String defaultPackage) {
        if (Objects.equals(this.defaultPackage, defaultPackage))
            return false;

        setDefaultPackage(defaultPackage);
        return true;
    }

    String getGenSourceDir() {
        return genSourceDir;
    }

    void setGenSourceDir(String genSourceDir) {
        this.genSourceDir = genSourceDir;
    }

    boolean changeGenSourceDir(String genSourceDir) {
        if (Objects.equals(this.genSourceDir, genSourceDir))
            return false;

        setGenSourceDir(genSourceDir);
        return true;
    }

    void writeConfigFile() throws IOException {
        var root = createRootElement("project", "config");
        root.addChild(createXMLElement("name", name));
        if (description != null)
            root.addChild(createXMLElement("description", description));
        root.addChild(createXMLElement("database", database));
        root.addChild(createXMLElement("default-package", defaultPackage));
        root.addChild(createXMLElement("gen-source-dir", genSourceDir));

        writeConfig(root.toString());
    }

    String getTabularRepresentation() {
        var table = new TextTable(2);

        table.addLine("NAME", name);
        if (description != null)
            table.addLine("DESCRIPTION", description);
        table.addLine("DATABASE CODE", database);
        table.addLine("DEFAULT PACKAGE", defaultPackage);
        table.addLine("SOURCE DIR", genSourceDir);

        return table.toString();
    }

}
