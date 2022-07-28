package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.XMLElement;

record RelationshipConfig(String table, String javaType, String javaName, String idField) {

    XMLElement getXMLElement() {
        var relationshipElement = new XMLElement("relationship");

        relationshipElement.addChild(ConfigData.createXMLElement("database-table", table));
        relationshipElement.addChild(ConfigData.createXMLElement("java-type", javaType));
        relationshipElement.addChild(ConfigData.createXMLElement("java-name", javaName));
        relationshipElement.addChild(ConfigData.createXMLElement("id-field", idField));

        return relationshipElement;
    }

}
