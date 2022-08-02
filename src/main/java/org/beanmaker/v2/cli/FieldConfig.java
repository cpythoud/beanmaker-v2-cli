package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.Column;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.xmlbase.XMLElement;

import java.util.Objects;

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
        if (!Column.JAVA_TYPES.contains(javaType))
            throw new IllegalArgumentException("Unsupported type: " + javaType);

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

    void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    boolean changeJavaName(String javaName) {
        if (Objects.equals(this.javaName, javaName))
            return false;

        setJavaName(javaName);
        return true;
    }

    boolean isRequired() {
        return required;
    }

    void setRequired(boolean required) {
        this.required = required;
    }

    boolean changeRequired(boolean required) {
        if (this.required == required)
            return false;

        setRequired(required);
        return true;
    }

    boolean isUnique() {
        return unique;
    }

    void setUnique(boolean unique) {
        this.unique = unique;
    }

    boolean changeUnique(boolean unique) {
        if (this.unique == unique)
            return false;

        setUnique(unique);
        return true;
    }

    String getAssociatedBeanClass() {
        return associatedBeanClass;
    }

    void setAssociatedBeanClass(String associatedBeanClass) {
        this.associatedBeanClass = associatedBeanClass;
    }

    boolean changeAssociatedBeanClass(String associatedBeanClass) {
        if (Objects.equals(this.associatedBeanClass, associatedBeanClass))
            return false;

        setAssociatedBeanClass(associatedBeanClass);
        return true;
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

    public boolean matches(Column column) {
        return Objects.equals(sqlName, column.getSqlName())
                && Objects.equals(javaType, column.getJavaType())
                && Objects.equals(javaName, column.getJavaName())
                && required == column.isRequired()
                && unique == column.isUnique()
                && Objects.equals(associatedBeanClass, nullifyAssociatedBean(column));
    }

    private String nullifyAssociatedBean(Column column) {
        String value = column.getAssociatedBeanClass();
        if (Strings.isEmpty(value))
            return null;
        return value;
    }

}
