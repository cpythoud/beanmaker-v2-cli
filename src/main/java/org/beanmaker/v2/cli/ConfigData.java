package org.beanmaker.v2.cli;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.html.xmlbase.ValueXMLAttribute;
import org.jcodegen.html.xmlbase.XMLElement;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.stream.StreamSource;

import javax.xml.validation.SchemaFactory;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;

abstract class ConfigData {

    private static final String XML_PRELUDE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";

    static final String ASSETS_CONFIG_FILE = "beanmaker-assets.xml";
    static final String PROJECT_CONFIG_FILE = "beanmaker.xml";
    static final String ASSETS_SCHEMA_FILE = "beanmaker-assets.xsd";
    static final String PROJECT_SCHEMA_FILE = "beanmaker-config.xsd";
    static final String TABLE_SCHEAMA_FILE = "beanmaker-table.xsd";

    static final String BEANMAKER_SUBDIR = ".beanmaker";

    private final String configFilename;
    private final String schemaFilename;
    private final boolean useUserHomeDir;
    private final boolean useBeanmakerDir;
    private final Path configDir;
    private final Path configFile;
    private boolean hasConfigFile;
    private final Document document;
    private final XPath xpath;

    ConfigData(String configFilename, String schemaFilename, boolean useUserHomeDir, boolean useBeanmakerDir)
            throws IOException, SAXException, ParserConfigurationException
    {
        this.configFilename = configFilename;
        this.schemaFilename = schemaFilename;
        this.useUserHomeDir = useUserHomeDir;
        this.useBeanmakerDir = useBeanmakerDir;

        configDir = getOrCreateConfigDir();
        configFile = configDir.resolve(configFilename);
        hasConfigFile = Files.exists(configFile);

        if (hasConfigFile) {
            validate();
            document = getDocument();
            xpath = getXPath();
        } else {
            document = null;
            xpath = null;
        }
    }

    private Path getConfigFile() throws IOException {  // ! will create .beanmaker directory if it doesn't exist
        Path startDir = useUserHomeDir ? Path.of(System.getProperty("user.home")) : Path.of(".");
        if (!useBeanmakerDir)
            return startDir.resolve(configFilename);

        Path subDir = getOrCreateConfigSubdir(startDir);
        return subDir.resolve(configFilename);
    }

    static Path getOrCreateConfigSubdir(Path startDir) throws IOException {
        Path subDir = startDir.resolve(BEANMAKER_SUBDIR);
        if (Files.exists(subDir)) {
            if (!Files.isDirectory(subDir))
                throw new IOException(BEANMAKER_SUBDIR + " exists but is not a directory.");
        } else
            Files.createDirectory(subDir);

        return subDir;
    }

    private Path getOrCreateConfigDir() throws IOException {  // ! will create .beanmaker directory if it doesn't exist
        Path startDir = useUserHomeDir ? Path.of(System.getProperty("user.home")) : Path.of(".");
        if (!useBeanmakerDir)
            return startDir;

        Path subDir = getOrCreateConfigSubdir(startDir);
        return subDir;
    }

    private void validate() throws SAXException, IOException {  // TODO: augment error reporting (manage exceptions)
        var schemaFile = new StreamSource(this.getClass().getClassLoader().getResourceAsStream(schemaFilename));
        var schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        var schema = schemaFactory.newSchema(schemaFile);
        var validator = schema.newValidator();
        validator.validate(new StreamSource(configFile.toFile()));
    }

    private Document getDocument() throws ParserConfigurationException, IOException, SAXException {
        var documentFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentFactory.newDocumentBuilder();
        return documentBuilder.parse(configFile.toFile());
    }

    private XPath getXPath() {
        var xPathFactory = XPathFactory.newInstance();
        return xPathFactory.newXPath();
    }

    Path getConfigDir() {
        return configDir;
    }

    boolean hasConfigFile() {
        return hasConfigFile;
    }

    String getConfigFilename() {
        return configFilename;
    }

    String getConfigFileFullPath() {
        return configFile.toAbsolutePath().toString();
    }

    String getStringValue(String path) throws XPathExpressionException {
        String value = xpath.evaluate(path, document);
        if (Strings.isEmpty(value))
            return null;

        return value;
    }

    String getStringValue(String path, Node node) throws XPathExpressionException {
        String value = xpath.evaluate(path, node);
        if (Strings.isEmpty(value))
            return null;

        return value;
    }

    Integer getIntValue(String path, Node node) throws XPathExpressionException {
        String value = getStringValue(path, node);
        if (value == null)
            return null;

        return Integer.parseInt(value);
    }

    Boolean getBooleanValue(String path, Node node) throws XPathExpressionException {
        String value = getStringValue(path, node);
        if (value == null)
            return null;

        return Boolean.parseBoolean(value);
    }

    XPathNodes getNodeList(String path) throws XPathExpressionException {
        return xpath.evaluateExpression(path, document, XPathNodes.class);
    }

    XPathNodes getNodeList(String path, Node node) throws XPathExpressionException {
        return xpath.evaluateExpression(path, node, XPathNodes.class);
    }

    boolean nodeExists(String path, Node node) throws XPathExpressionException {
        var nodes = getNodeList(path, node);
        return nodes != null && nodes.size() > 0;
    }

    PasswordConfig extractPasswordConfig(String path, Node node) throws XPathExpressionException {
        if (nodeExists(path + "/interactive", node))
            return PasswordConfig.promptOnly();
        if (nodeExists(path + "/cleartext", node))
            return PasswordConfig.clearText(getStringValue(path + "/cleartext", node).toCharArray());  // TODO: find how not to use a String
        if (nodeExists(path + "/encrypted", node))
            return PasswordConfig.encrypted(getStringValue(path + "/encrypted", node).toCharArray());  // TODO: find how not to use a String

        throw new AssertionError("Unknown encoding of password data");
    }

    XMLElement createRootElement(String name, String schemaReference) {
        var element = new XMLElement(name);
        element.addAttribute(new ValueXMLAttribute(
                "xmlns",
                "https://schema.beanmaker.org/beanmaker-" + schemaReference
        ));
        element.addAttribute(new ValueXMLAttribute(
                "xmlns:xsi",
                "http://www.w3.org/2001/XMLSchema-instance"
        ));
        element.addAttribute(new ValueXMLAttribute(
                "xsi:schemaLocation",
                "https://schema.beanmaker.org/beanmaker-" + schemaReference + " beanmaker-" + schemaReference + ".xsd"
        ));
        return element;
    }

    static XMLElement createXMLElement(String name, String value) {
        var element = new XMLElement(name, value);
        element.setOnOneLine(true);
        return element;
    }

    static XMLElement createXMLElement(String name, char[] password) {
        return createXMLElement(name, String.valueOf(password));
        // ! kind of annoying to have to store the password in a String at this point, fortunately the config file
        // ! being overwritten usually means that the current command is about to exit
    }

    static XMLElement createXMLElement(String name, int value) {
        return createXMLElement(name, Integer.toString(value));
    }

    static XMLElement createXMLElement(String name, Object value) {
        return createXMLElement(name, value.toString());
    }

    static XMLElement createXMLElement(String name) {
        return new XMLElement(name, true);
    }

    void writeConfig(String config) throws IOException {
        Files.writeString(configFile, XML_PRELUDE + config, StandardCharsets.UTF_8);
        hasConfigFile = true;
    }

}
