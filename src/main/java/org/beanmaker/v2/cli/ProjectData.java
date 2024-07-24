package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.ProjectParameters;

import org.beanmaker.v2.util.Strings;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathExpressionException;

import java.io.IOException;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

class ProjectData extends ConfigData {

    private String name;
    private String description;
    private String database;
    private String defaultPackage;
    private String genSourceDir;
    private boolean editorFieldsConstructor;
    private boolean sealedClasses;
    private boolean databaseProviderReference;

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
            editorFieldsConstructor = nodeExists("/project/parameters/editor-fields-constructor");
            sealedClasses = nodeExists("/project/parameters/sealed-classes");
            databaseProviderReference = nodeExists("/project/parameters/database-provider-reference");
        }
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

    boolean updateParameters(Set<ProjectParameter> activatedParameters, Set<ProjectParameter> deactivatedParameters) {
        boolean changed = false;
        if (activatedParameters != null) {
            if (activatedParameters.contains(ProjectParameter.EDITOR_FIELDS_CONSTRUCTOR)) {
                if (!editorFieldsConstructor) {
                    changed = true;
                    editorFieldsConstructor = true;
                }
            }
            if (activatedParameters.contains(ProjectParameter.SEALED_CLASSES)) {
                if (!sealedClasses) {
                    changed = true;
                    sealedClasses = true;
                }
            }
            if (activatedParameters.contains(ProjectParameter.DATABASE_PROVIDER_REFERENCE)) {
                if (!databaseProviderReference) {
                    changed = true;
                    databaseProviderReference = true;
                }
            }
        }
        if (deactivatedParameters != null) {
            if (deactivatedParameters.contains(ProjectParameter.EDITOR_FIELDS_CONSTRUCTOR)) {
                if (editorFieldsConstructor) {
                    changed = true;
                    editorFieldsConstructor = false;
                }
            }
            if (deactivatedParameters.contains(ProjectParameter.SEALED_CLASSES)) {
                if (sealedClasses) {
                    changed = true;
                    sealedClasses = false;
                }
            }
            if (deactivatedParameters.contains(ProjectParameter.DATABASE_PROVIDER_REFERENCE)) {
                if (databaseProviderReference) {
                    changed = true;
                    databaseProviderReference = false;
                }
            }
        }
        return changed;
    }

    private Set<ProjectParameter> getParameterSet() {
        var set = new LinkedHashSet<ProjectParameter>();
        if (editorFieldsConstructor)
            set.add(ProjectParameter.EDITOR_FIELDS_CONSTRUCTOR);
        if (sealedClasses)
            set.add(ProjectParameter.SEALED_CLASSES);
        if (databaseProviderReference)
            set.add(ProjectParameter.DATABASE_PROVIDER_REFERENCE);
        return set;
    }

    ProjectParameters getProjectParameters() {
        return new CodeGenerationParameters(getParameterSet());
    }

    void writeConfigFile() throws IOException {
        var root = createRootElement("project", "config");
        root.addChild(createXMLElement("name", name));
        if (description != null)
            root.addChild(createXMLElement("description", description));
        root.addChild(createXMLElement("database", database));
        root.addChild(createXMLElement("default-package", defaultPackage));
        root.addChild(createXMLElement("gen-source-dir", genSourceDir));

        var parameterSet  = getParameterSet();
        if (!parameterSet.isEmpty()) {
            var parameterElements = createXMLElement("parameters");
            for (var parameter : parameterSet)
                parameterElements.addChild(createXMLElement(parameter.xmlName(), "true"));
            root.addChild(parameterElements);
        }

        writeConfig(root.toString());
    }

    String getTabularRepresentation() throws IOException {
        var table = new TextTable(2);

        table.addLine("NAME", name);
        if (description != null)
            table.addLine("DESCRIPTION", description);
        table.addLine("DATABASE CODE", database);
        table.addLine("DEFAULT PACKAGE", defaultPackage);
        table.addLine("SOURCE DIR", genSourceDir);

        var parameterSet  = getParameterSet();
        if (!parameterSet.isEmpty())
            table.addLine("CODEGEN PARAMETERS", format(parameterSet));

        TableData.getCurrentTableName().ifPresent(currentTable -> table.addLine("CURRENT TABLE", currentTable));

        return table.toString();
    }

    private String format(Set<ProjectParameter> parameterSet) {
        var parameters = new StringBuilder();
        for (ProjectParameter parameter : parameterSet) {
            parameters.append(parameter.xmlName()).append(", ");
        }
        parameters.delete(parameters.length() - 2, parameters.length());
        return parameters.toString();
    }

}
