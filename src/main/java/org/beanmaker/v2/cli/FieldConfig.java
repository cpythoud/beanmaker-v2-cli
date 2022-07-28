package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.XMLElement;

class FieldConfig {

    private final String sqlName;
    
    private String javaType;
    private String javaName;
    private boolean required;
    private boolean unique;
    private String associatedBeanClass;

    FieldConfig(String sqlName) {
        this.sqlName = sqlName;
    }

    String getSqlName() {
        return sqlName;
    }

    String getJavaType() {
        return javaType;
    }

    void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    String getJavaName() {
        return javaName;
    }

    void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    boolean isRequired() {
        return required;
    }

    void setRequired(boolean required) {
        this.required = required;
    }

    boolean isUnique() {
        return unique;
    }

    void setUnique(boolean unique) {
        this.unique = unique;
    }

    String getAssociatedBeanClass() {
        return associatedBeanClass;
    }

    void setAssociatedBeanClass(String associatedBeanClass) {
        this.associatedBeanClass = associatedBeanClass;
    }

    XMLElement getXMLElement() {
        var fieldElement = new XMLElement("field");

        fieldElement.addChild(ConfigData.createXMLElement("sql-name", sqlName));
        fieldElement.addChild(ConfigData.createXMLElement("java-type", javaType));
        fieldElement.addChild(ConfigData.createXMLElement("java-name", javaName));
        fieldElement.addChild(ConfigData.createXMLElement("required", required));
        fieldElement.addChild(ConfigData.createXMLElement("unique", unique));
        if (associatedBeanClass != null)
            fieldElement.addChild(ConfigData.createXMLElement("bean-class", associatedBeanClass));

        return fieldElement;
    }

}
