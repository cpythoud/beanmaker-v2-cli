package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.XMLElement;

record PrivateKeyConfig(String filename, PasswordConfig passwordConfig) {

    XMLElement getXMLElement() {
        var keyElement = new XMLElement("key");
        keyElement.addChild(ConfigData.createXMLElement("file", filename));
        keyElement.addChild(passwordConfig.getXMLElement());
        return keyElement;
    }

}
