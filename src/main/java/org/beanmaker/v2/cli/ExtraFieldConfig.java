package org.beanmaker.v2.cli;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.xmlbase.XMLElement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class ExtraFieldConfig {
    
    private final String javaType;
    private final String javaName;
    
    private String initialization;
    private boolean isFinal;
    
    private final Set<String> imports = new LinkedHashSet<>();

    ExtraFieldConfig(String javaType, String javaName) {
        this.javaType = javaType;
        this.javaName = javaName;
    }

    String getJavaType() {
        return javaType;
    }

    String getJavaName() {
        return javaName;
    }

    String getInitialization() {
        return initialization;
    }

    void setInitialization(String initialization) {
        this.initialization = initialization;
    }

    boolean isFinal() {
        return isFinal;
    }

    void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    List<String> getImports() {
        return new ArrayList<>(imports);
    }

    void addImport(String importData) {
        imports.add(importData);
    }

    String getImportTextList() {
        if (imports.isEmpty())
            return "";

        return Strings.concatWithSeparator(", ", imports);
    }

    XMLElement getXMLElement() {
        var fieldElement = new XMLElement("field");

        fieldElement.addChild(ConfigData.createXMLElement("java-type", javaType));
        fieldElement.addChild(ConfigData.createXMLElement("java-name", javaName));

        if (initialization != null)
            fieldElement.addChild(ConfigData.createXMLElement("initialization", initialization));
        if (isFinal)
            fieldElement.addChild(ConfigData.createXMLElement("final"));

        if (!imports.isEmpty()) {
            var importsElement = new XMLElement("imports");
            for (var importRef: imports)
                importsElement.addChild(new XMLElement("import", importRef));
            fieldElement.addChild(importsElement);
        }

        return fieldElement;
    }

}
