package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.Columns;
import org.beanmaker.v2.codegen.DatabaseServer;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.xmlbase.XMLElement;

import java.util.List;
import java.util.Objects;

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

    boolean changeType(DatabaseType type) {
        if (Objects.equals(this.type, type))
            return false;

        setType(type);
        return true;
    }

    String getServer() {
        return server;
    }

    void setServer(String server) {
        this.server = server;
    }

    boolean changeServer(String server) {
        if (Objects.equals(this.server, server))
            return false;

        setServer(server);
        return true;
    }

    int getPort() {
        return port;
    }

    void setPort(int port) {
        this.port = port;
    }

    boolean changePort(int port) {
        if (this.port == port)
            return false;

        setPort(port);
        return true;
    }

    String getDatabase() {
        return database;
    }

    void setDatabase(String database) {
        this.database = database;
    }

    boolean changeDatabase(String database) {
        if (Objects.equals(this.database, database))
            return false;

        setDatabase(database);
        return true;
    }

    String getUser() {
        return user;
    }

    void setUser(String user) {
        this.user = user;
    }

    boolean changeUser(String user) {
        if (Objects.equals(this.user, user))
            return false;

        setUser(user);
        return true;
    }

    PasswordConfig getPasswordConfig() {
        return passwordConfig;
    }

    void setPasswordConfig(PasswordConfig passwordConfig) {
        this.passwordConfig = passwordConfig;
    }

    boolean changePasswordConfig(PasswordConfig passwordConfig) {
        if (Objects.equals(this.passwordConfig, passwordConfig))
            return false;

        setPasswordConfig(passwordConfig);
        return true;
    }

    String getSshCode() {
        return sshCode;
    }

    void setSshCode(String sshCode) {
        this.sshCode = sshCode;
    }

    boolean changeSshCode(String sshCode) {
        if (Objects.equals(this.sshCode, sshCode))
            return false;

        setSshCode(sshCode);
        return true;
    }

    XMLElement getXMLElement() {
        var hostElement = new XMLElement("host");

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

    String getTabularRepresentation() {
        var table = new TextTable(2);

        table.addLine("CODE", code);
        table.addLine("TYPE", type.toString());
        table.addLine("SERVER", server);
        table.addLine("PORT", Integer.toString(port));
        table.addLine("DATABASE", database);
        table.addLine("USER", user);
        table.addLine("PASSWORD", Strings.repeatString("*", 16));  // TODO: reconsider at some point
        if (sshCode != null)
            table.addLine("SSH CODE", sshCode);

        return table.toString();
    }

    boolean checkConnection() {
        getTables();  // ! Exception = bad connection TODO: implement proper reporting of failures
        return true;
    }

    List<String> getTables() {
        if (!passwordConfig.hasCleartextPassword())
            throw new UnsupportedOperationException("Using a non clear text password is not implemented yet");

        return getServerConnection().getTables(database);
    }

    List<String> getTables(String filter) {
        return CommandHelper.wildcardFilter(getTables(), filter);
    }

    private DatabaseServer getServerConnection() {
        return type.getServerInstance(server, port, user, passwordConfig.getCleartextPassword());
    }

    Columns getColumns(String table) {
        return new Columns(getServerConnection(), database, table);
    }

    boolean hasTable(String table) {
        return getTables().contains(table);  // TODO: check performance/need to store table list or make a request for big projects
    }

}
