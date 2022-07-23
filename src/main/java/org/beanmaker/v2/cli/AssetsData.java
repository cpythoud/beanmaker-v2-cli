package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.XMLElement;

import org.w3c.dom.Node;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;

import java.io.IOException;

import java.util.LinkedHashMap;
import java.util.Map;

class AssetsData extends ConfigData {

    private final Map<String, SSHConfig> sshConfigs = new LinkedHashMap<>();
    private final Map<String, DatabaseConfig> databaseConfigs = new LinkedHashMap<>();

    AssetsData() throws IOException, SAXException, ParserConfigurationException, XPathException {
        super(ASSETS_CONFIG_FILE, ASSETS_SCHEMA_FILE, true, false);

        if (hasConfigFile()) {
            for (var sshConfigNode: getNodeList("/assets/ssh/host")) {
                String code = getStringValue("//code", sshConfigNode);
                var sshConfig = new SSHConfig(code);
                sshConfig.setServer(getStringValue("//server", sshConfigNode));
                sshConfig.setPort(getIntValue("//port", sshConfigNode));
                sshConfig.setUser(getStringValue("//user", sshConfigNode));

                if (nodeExists("//auth/passwd", sshConfigNode))
                    sshConfig.setSshAuthMethod(SSHAuthMethod.password(extractPasswordConfig("//auth/password", sshConfigNode)));
                else if (nodeExists("//auth/key", sshConfigNode))
                    sshConfig.setSshAuthMethod(SSHAuthMethod.privateKey(extractKeyConfig("//auth/key", sshConfigNode)));
                else
                    throw new AssertionError("Unknown SSH authentication method");

                sshConfigs.put(code, sshConfig);
            }
            for (var databaseConfigNode: getNodeList("/assets/databases/database")) {
                String code = getStringValue("//code", databaseConfigNode);
                var databaseConfig = new DatabaseConfig(code);
                databaseConfig.setType(DatabaseType.valueOf(getStringValue("//type", databaseConfigNode).toUpperCase()));
                databaseConfig.setServer(getStringValue("//server", databaseConfigNode));
                databaseConfig.setPort(getIntValue("//port", databaseConfigNode));
                databaseConfig.setDatabase(getStringValue("//database", databaseConfigNode));
                databaseConfig.setUser(getStringValue("//user", databaseConfigNode));
                databaseConfig.setPasswordConfig(extractPasswordConfig("//password", databaseConfigNode));

                databaseConfigs.put(code, databaseConfig);
            }
        }
    }

    private PrivateKeyConfig extractKeyConfig(String path, Node node) throws XPathExpressionException {
        return new PrivateKeyConfig(
                getStringValue(path + "/file", node),
                extractPasswordConfig(path + "/password", node)
        );
    }

    boolean hasDatabaseWithCode(String code) {
        return databaseConfigs.containsKey(code);
    }

    void addDatabaseConfig(DatabaseConfig databaseConfig) {
        String code = databaseConfig.getCode();
        if (hasDatabaseWithCode(code))
            throw new AssertionError("DB with code " + code + " already exists.");

        databaseConfigs.put(code, databaseConfig);
    }

    boolean writeConfigFile() throws IOException {
        boolean newConfig = !hasConfigFile();
        var root = createRootElement("assets", "assets");
        if (!sshConfigs.isEmpty()) {
            var sshElement = new XMLElement("ssh");
            for (var sshConfig: sshConfigs.values())
                sshElement.addChild(sshConfig.getXMLElement());
            root.addChild(sshElement);
        }
        if (!databaseConfigs.isEmpty()) {
            var databaseElement = new XMLElement("databases");
            for (var databaseConfig: databaseConfigs.values())
                databaseElement.addChild(databaseConfig.getXMLElement());
            root.addChild(databaseElement);
        }

        writeConfig(root.toString());

        return newConfig;
    }

}
