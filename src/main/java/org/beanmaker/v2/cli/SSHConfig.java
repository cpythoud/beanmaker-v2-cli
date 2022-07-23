package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.XMLElement;
import org.w3c.dom.Node;

class SSHConfig {

    private final String code;

    private String server;
    private int port = 22;
    private String user;
    private SSHAuthMethod sshAuthMethod;

    SSHConfig(String code) {
        this.code = code;
    }

    String getCode() {
        return code;
    }

    String getServer() {
        return server;
    }

    void setServer(String server) {
        this.server = server;
    }

    int getPort() {
        return port;
    }

    void setPort(int port) {
        this.port = port;
    }

    String getUser() {
        return user;
    }

    void setUser(String user) {
        this.user = user;
    }

    SSHAuthMethod getSshAuthMethod() {
        return sshAuthMethod;
    }

    void setSshAuthMethod(SSHAuthMethod sshAuthMethod) {
        this.sshAuthMethod = sshAuthMethod;
    }

    XMLElement getXMLElement() {
        var hostElement = new XMLElement("host");
        hostElement.addChild(ConfigData.createXMLElement("code", code));
        hostElement.addChild(ConfigData.createXMLElement("server", server));
        hostElement.addChild(ConfigData.createXMLElement("port", port));
        hostElement.addChild(ConfigData.createXMLElement("user", user));
        hostElement.addChild(sshAuthMethod.getXMLElement());
        return hostElement;
    }

}
