package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.Column;
import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.xmlbase.XMLElement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class ExtraFieldConfig {
    
    private String javaType;
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

    void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    boolean changeJavaType(String javaType) {
        if (Objects.equals(this.javaType, javaType))
            return false;

        setJavaType(javaType);
        return true;
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

    boolean changeInitialization(String initialization) {
        if (Objects.equals(this.initialization, initialization))
            return false;

        setInitialization(initialization);
        return true;
    }

    boolean isFinal() {
        return isFinal;
    }

    void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    boolean changeFinal(boolean isFinal) {
        if (this.isFinal == isFinal)
            return false;

        setFinal(isFinal);
        return true;
    }

    List<String> getImports() {
        return new ArrayList<>(imports);
    }

    void addImport(String importData) {
        imports.add(importData);
    }

    boolean updateImport(String importData) {
        if (imports.contains(importData))
            return false;

        addImport(importData);
        return true;
    }

    String getImportTextList() {
        if (imports.isEmpty())
            return "";

        return Strings.concatWithSeparator(", ", imports);
    }

    boolean hasImports() {
        return !imports.isEmpty();
    }

    void clearImports() {
        imports.clear();
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
                importsElement.addChild(ConfigData.createXMLElement("import", importRef));
            fieldElement.addChild(importsElement);
        }

        return fieldElement;
    }

}
