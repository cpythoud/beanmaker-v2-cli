package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.XMLElement;

class PasswordConfig {

    private final boolean promptOnly;
    private final char[] password;
    private final boolean encrypted;

    private PasswordConfig(boolean promptOnly, char[] password, boolean encrypted) {
        this.promptOnly = promptOnly;
        this.password = password;
        this.encrypted = encrypted;
    }

    static PasswordConfig promptOnly() {
        return new PasswordConfig(true, null, false);
    }

    static PasswordConfig clearText(char[] password) {
        return new PasswordConfig(false, password, false);
    }

    static PasswordConfig encrypted(char[] password) {
        return new PasswordConfig(false, password, true);
    }

    static PasswordConfig encrypted(char[] password, char[] passphrase) {
        // TODO: encrypt password with passphrase
        throw new UnsupportedOperationException();
    }

    XMLElement getXMLElement() {
        var passwordElement = new XMLElement("password");
        if (promptOnly)
            passwordElement.addChild(ConfigData.createXMLElement("interactive"));
        else {
            if (encrypted)
                passwordElement.addChild(ConfigData.createXMLElement("encrypted", password));
            else
                passwordElement.addChild(ConfigData.createXMLElement("cleartext", password));
        }
        return passwordElement;
    }

}
