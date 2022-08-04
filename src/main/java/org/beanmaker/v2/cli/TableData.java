package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.Column;
import org.beanmaker.v2.codegen.Columns;

import org.jcodegen.html.xmlbase.XMLElement;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathExpressionException;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

class TableData extends ConfigData {

    private static final String CURRENT_TABLE_FILENAME = "CURRENT_TABLE";

    private String name;
    private String packageName;
    private String beanName;
    private String itemOrderAssociatedField;

    private final Map<String, FieldConfig> fields = new LinkedHashMap<>();
    private final Map<String, RelationshipConfig> relationships = new LinkedHashMap<>();
    private final Map<String, ExtraFieldConfig> extraFields = new LinkedHashMap<>();

    TableData(String table) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        super(table + ".xml", TABLE_SCHEAMA_FILE, false, true);

        if (hasConfigFile()) {
            name = getStringValue("table/name");
            packageName = getStringValue("table/package");
            beanName = getStringValue("table/bean");
            itemOrderAssociatedField = getStringValue("table/item-order-associated-field");
            for (var fieldConfigNode: getNodeList("table/fields/field")) {
                String sqlName = getStringValue("sql-name", fieldConfigNode);
                var fieldConfig = new FieldConfig(sqlName);
                fieldConfig.setJavaType(getStringValue("java-type", fieldConfigNode));
                fieldConfig.setJavaName(getStringValue("java-name", fieldConfigNode));
                fieldConfig.setRequired(getBooleanValue("required", fieldConfigNode));
                fieldConfig.setUnique(getBooleanValue("unique", fieldConfigNode));
                fieldConfig.setAssociatedBeanClass(getStringValue("bean-class", fieldConfigNode));
                fields.put(sqlName, fieldConfig);
            }
            for (var relationshipConfigNode: getNodeList("table/relationships/relationship")) {
                String databaseTable = getStringValue("database-table", relationshipConfigNode);
                String javaType = getStringValue("java-type", relationshipConfigNode);
                String javaName = getStringValue("java-name", relationshipConfigNode);
                String idField = getStringValue("id-field", relationshipConfigNode);
                relationships.put(javaName, new RelationshipConfig(databaseTable, javaType, javaName, idField));
            }
            for (var extraFieldConfigNode: getNodeList("table/extra-fields/field")) {
                String javaName = getStringValue("java-name", extraFieldConfigNode);
                var extraFieldConfig = new ExtraFieldConfig(
                        getStringValue("java-type", extraFieldConfigNode),
                        javaName
                );
                extraFieldConfig.setInitialization(getStringValue("initialization"));
                var isFinal = getBooleanValue("final", extraFieldConfigNode);
                if (isFinal != null)
                    extraFieldConfig.setFinal(isFinal);
                for (var importNode: getNodeList("imports/import", extraFieldConfigNode))
                    extraFieldConfig.addImport(importNode.getTextContent());
                extraFields.put(javaName, extraFieldConfig);
            }
        } else
            name = table;  // ! in case table is made current and/or shown before a config file is created
    }

    static Optional<TableData> getCurrent() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        String table = getCurrentTableName().orElse(null);
        if (table == null)
            return Optional.empty();

        return Optional.of(new TableData(table));
    }

    static Optional<String> getCurrentTableName() throws IOException {
        Path subDir = getOrCreateConfigSubdir(Path.of("."));
        Path currentTableFile = subDir.resolve(CURRENT_TABLE_FILENAME);
        if (!Files.exists(currentTableFile) || !Files.isRegularFile(currentTableFile))
            return Optional.empty();

        return Optional.of(Files.readString(currentTableFile, StandardCharsets.UTF_8));
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getPackageName() {
        return packageName;
    }

    void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    boolean changePackageName(String packageName) {
        if (Objects.equals(this.packageName, packageName))
            return false;

        setPackageName(packageName);
        return true;
    }

    String getBeanName() {
        return beanName;
    }

    void setBeanName(String beanName) {
        this.beanName = beanName;
    }
    
    boolean changeBeanName(String beanName) {
        if (Objects.equals(this.beanName, beanName))
            return false;
        
        setBeanName(beanName);
        return true;
    }

    boolean hasItemOrderAssociatedField() {
        return itemOrderAssociatedField != null;
    }

    String getItemOrderAssociatedField() {
        return itemOrderAssociatedField;
    }

    void setItemOrderAssociatedField(String itemOrderAssociatedField) {
        this.itemOrderAssociatedField = itemOrderAssociatedField;
    }

    boolean changeItemOrderAssociatedField(String itemOrderAssociatedField) {
        if (Objects.equals(this.itemOrderAssociatedField, itemOrderAssociatedField))
            return false;

        setItemOrderAssociatedField(itemOrderAssociatedField);
        return true;
    }

    void updateField(FieldConfig fieldConfig, Column column) {
        fields.remove(fieldConfig.getSqlName());
        if (fieldConfig.matches(column))
            return;
        fields.put(fieldConfig.getSqlName(), fieldConfig);
    }

    void makeCurrent() throws IOException {
        Path subDir = getOrCreateConfigSubdir(Path.of("."));
        Files.writeString(subDir.resolve(CURRENT_TABLE_FILENAME), name, StandardCharsets.UTF_8);
    }

    void writeConfigFile() throws IOException {
        var root = createRootElement("table", "table");
        root.addChild(createXMLElement("name", name));
        root.addChild(createXMLElement("package", packageName));
        root.addChild(createXMLElement("bean", beanName));
        if (!fields.isEmpty()) {
            var fieldsElement = new XMLElement("fields");
            for (var field: fields.values())
                fieldsElement.addChild(field.getXMLElement());
            root.addChild(fieldsElement);
        }
        if (itemOrderAssociatedField != null)
            root.addChild(createXMLElement("item-order-associated-field", itemOrderAssociatedField));
        if (!relationships.isEmpty()) {
            var relationshipsElement = new XMLElement("relationships");
            for (var relationship: relationships.values())
                relationshipsElement.addChild(relationship.getXMLElement());
            root.addChild(relationshipsElement);
        }
        if (!extraFields.isEmpty()) {
            var extraFieldsElement = new XMLElement("extra-fields");
            for (var extraField: extraFields.values())
                extraFieldsElement.addChild(extraField.getXMLElement());
            root.addChild(extraFieldsElement);
        }

        writeConfig(root.toString());
    }

    String getTabularRepresentation(Columns columns) {
        var tables = new StringBuilder();

        tables.append(getTitle("TABLE & BEAN"));
        tables.append(getMainInfoTable());
        tables.append(getTitle("FIELDS"));
        tables.append(getFieldsTable(columns));
        if (!relationships.isEmpty()) {
            tables.append(getTitle("RELATIONSHIPS"));
            tables.append(getRelationshipsTable());
        }
        if (!extraFields.isEmpty()) {
            tables.append(getTitle("EXTRA FIELDS"));
            tables.append(getExtraFieldsTable());
        }

        return tables.toString();
    }

    private String getMainInfoTable() {
        var table = new TextTable(2);

        table.addLine("NAME", name);
        table.addLine("PACKAGE", hasConfigFile() ? packageName : "");
        table.addLine("BEAN", hasConfigFile() ? beanName : "");
        if (itemOrderAssociatedField != null)
            table.addLine("ITEM_ORDER ASSOCIATED FIELD", itemOrderAssociatedField);

        return table.toString();
    }

    private String getTitle(String title) {  // TOOO: make accessible to other classes if/when needed
        return "\n" + title + "\n" + "-".repeat(title.length()) + "\n\n";
    }

    private String getFieldsTable(Columns columns) {
        var table = new TextTable(8);

        table.addLine("DB TYPE", "DB NAME", "DISPLAY", "JAVA TYPE", "JAVA_NAME", "REQUIRED", "UNIQUE", "COMPLEMENT");

        for (var column: columns.getList()) {
            String sqlType = column.getSqlTypeName();
            String sqlName = column.getSqlName();
            String display = column.getDisplaySize() + " / " + column.getPrecision() + "," + column.getScale();
            String javaType = column.getJavaType();
            String javaName = column.getJavaName();
            String required = getYesNo(column.isRequired());
            String unique = getYesNo(column.isUnique());
            String complement = getComplementaryInfo(column);

            if (fields.containsKey(sqlName)) {
                var field = fields.get(sqlName);
                javaType = field.getJavaType();
                javaName = field.getJavaName();
                required = getYesNo(field.isRequired());
                unique = getYesNo(field.isUnique());
                complement = getComplementaryInfo(column, field);
            }

            if (column.isItemOrder() && itemOrderAssociatedField != null)
                complement = "ASSOCIATED FIELD: " + itemOrderAssociatedField;

            table.addLine(sqlType, sqlName, display, javaType, javaName, required, unique, complement);
        }

        return table.toString();
    }

    private String getYesNo(boolean flag) {
        return flag ? "YES" : "NO";
    }

    private String getComplementaryInfo(Column column) {
        if (column.isBad())
            return "***INVALID FIELD***";

        if (column.couldHaveAssociatedBean())
            return "ASSOCIATED BEAN CLASS: " + column.getAssociatedBeanClass();

        if (column.isItemOrder() && !column.isUnique())
            return "ASSOCIATED FIELD: " + column.getItemOrderAssociatedField();

        return "";
    }

    private String getComplementaryInfo(Column column, FieldConfig field) {
        if (column.couldHaveAssociatedBean())
            return "ASSOCIATED BEAN CLASS: " + field.getAssociatedBeanClass();

        if (column.isItemOrder() && !column.isUnique())
            return "ASSOCIATED FIELD: " + getItemOrderAssociatedField();

        return "";
    }

    private String getRelationshipsTable() {
        var table = new TextTable(4);

        table.addLine("JAVA TYPE (LIST OF)", "JAVA NAME", "ASSOCIATED TABLE", "ID FIELD");

        for (var relationship: relationships.values())
            table.addLine(relationship.javaType(), relationship.javaName(), relationship.table(), relationship.idField());

        return table.toString();
    }

    private String getExtraFieldsTable() {
        var table = new TextTable(5);

        table.addLine("TYPE", "NAME", "INITIALIZATION", "FINAL", "IMPORTS");

        for (var field: extraFields.values()) {
            String type = field.getJavaType();
            String name = field.getJavaName();
            String initialization = field.getInitialization();
            if (initialization == null)
                initialization = "";
            String isFinal = getYesNo(field.isFinal());
            String imports = field.getImportTextList();

            table.addLine(type, name, initialization, isFinal, imports);
        }

        return table.toString();
    }

    FieldConfig getFieldConfig(String field, Column column) {
        var config = fields.get(field);
        if (config != null)
            return config;

        config = new FieldConfig(field);
        config.setJavaType(column.getJavaType());
        config.setJavaName(column.getJavaName());
        config.setRequired(column.isRequired());
        config.setUnique(column.isUnique());
        config.setAssociatedBeanClass(column.getAssociatedBeanClass());
        return config;
    }

    Optional<FieldConfig> getCustomizedFieldConfiguration(String field) {
        return Optional.ofNullable(fields.get(field));
    }

    boolean relationshipExists(String javaName) {
        return relationships.containsKey(javaName);
    }

    void addRelationship(RelationshipConfig relationship) {
        if (relationshipExists(relationship.javaName()))
            throw new IllegalArgumentException("Relationship " + relationship.javaName() + " already exists");

        relationships.put(relationship.javaName(), relationship);
    }

    RelationshipConfig getRelationship(String javaName) {
        if (!relationshipExists(javaName))
            throw new IllegalArgumentException("No relationship anchored to java field " + javaName + " exists");

        return relationships.get(javaName);
    }

    void changeRelationship(RelationshipConfig relationship) {
        if (!relationshipExists(relationship.javaName()))
            throw new IllegalArgumentException("No relationship anchored to java field " + relationship.javaName() + " exists");

        relationships.put(relationship.javaName(), relationship);
    }

    void deleteRelationship(String javaName) {
        if (!relationshipExists(javaName))
            throw new IllegalArgumentException("No relationship anchored to java field " + javaName + " exists");

        relationships.remove(javaName);
    }

    List<RelationshipConfig> getRelationships() {
        return new ArrayList<>(relationships.values());
    }

}
