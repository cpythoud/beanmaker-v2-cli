package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.XMLElement;

class SSHAuthMethod {

    private final PasswordConfig passwordConfig;
    private final PrivateKeyConfig privateKeyConfig;

    private SSHAuthMethod(PasswordConfig passwordConfig, PrivateKeyConfig privateKeyConfig) {
        this.passwordConfig = passwordConfig;
        this.privateKeyConfig = privateKeyConfig;
    }

    static SSHAuthMethod password(PasswordConfig passwordConfig) {
        return new SSHAuthMethod(passwordConfig, null);
    }

    static SSHAuthMethod privateKey(PrivateKeyConfig privateKeyConfig) {
        return new SSHAuthMethod(null, privateKeyConfig);
    }

    PasswordConfig getPasswordConfig() {
        return passwordConfig;
    }

    PrivateKeyConfig getPrivateKeyConfig() {
        return privateKeyConfig;
    }

    boolean usesPassword() {
        return passwordConfig != null;
    }

    boolean usePrivateKey() {
        return privateKeyConfig != null;
    }

    XMLElement getXMLElement() {
        var authElement = new XMLElement("auth");
        if (passwordConfig == null)
            authElement.addChild(privateKeyConfig.getXMLElement());
        else
            authElement.addChild(passwordConfig.getXMLElement());
        return authElement;
    }

}
