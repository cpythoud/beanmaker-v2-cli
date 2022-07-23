package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.XMLElement;

class DatabaseConfig {

    private final String code;

    private DatabaseType type = DatabaseType.MYSQL;
    private String server;
    private int port = 3306;
    private String database;
    private String user;
    private PasswordConfig passwordConfig = PasswordConfig.promptOnly();
    private String sshCode;

    DatabaseConfig(String code) {
        this.code = code;
    }

    String getCode() {
        return code;
    }

    DatabaseType getType() {
        return type;
    }

    void setType(DatabaseType type) {
        this.type = type;
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

    String getDatabase() {
        return database;
    }

    void setDatabase(String database) {
        this.database = database;
    }

    String getUser() {
        return user;
    }

    void setUser(String user) {
        this.user = user;
    }

    PasswordConfig getPasswordConfig() {
        return passwordConfig;
    }

    void setPasswordConfig(PasswordConfig passwordConfig) {
        this.passwordConfig = passwordConfig;
    }

    String getSshCode() {
        return sshCode;
    }

    void setSshCode(String sshCode) {
        this.sshCode = sshCode;
    }

    XMLElement getXMLElement() {
        var hostElement = new XMLElement("database");
        hostElement.addChild(ConfigData.createXMLElement("code", code));
        hostElement.addChild(ConfigData.createXMLElement("type", type));
        hostElement.addChild(ConfigData.createXMLElement("server", server));
        hostElement.addChild(ConfigData.createXMLElement("port", port));
        hostElement.addChild(ConfigData.createXMLElement("database", database));
        hostElement.addChild(ConfigData.createXMLElement("user", user));
        hostElement.addChild(passwordConfig.getXMLElement());
        if (sshCode != null)
            hostElement.addChild(ConfigData.createXMLElement("ssh", sshCode));
        return hostElement;
    }

}
