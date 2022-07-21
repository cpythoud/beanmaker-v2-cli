package org.beanmaker.v2.cli;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.xmlbase.XMLElement;

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
        super(PROJECT_CONFIG_FILE);

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
        var root = getRootElement("project", "config");
        root.addChild(getElement("name", name));
        if (description != null)
            root.addChild(getElement("description", description));
        root.addChild(getElement("database", database));
        root.addChild(getElement("default-package", defaultPackage));
        root.addChild(getElement("gen-source-dir", genSourceDir));

        writeConfig(root.toString());
    }

    private XMLElement getElement(String name, String value) {
        var element = new XMLElement(name, value);
        element.setOnOneLine(true);
        return element;
    }

}
