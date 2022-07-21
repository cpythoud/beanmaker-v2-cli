package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.ValueXMLAttribute;
import org.jcodegen.html.xmlbase.XMLElement;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.stream.StreamSource;

import javax.xml.validation.SchemaFactory;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;

abstract class ConfigData {

    private static final String XML_PRELUDE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";

    static final String ASSETS_CONFIG_FILE = "beanmaker-assets.xml";
    static final String PROJECT_CONFIG_FILE = "beanmaker.xml";

    private final String configFilename;
    private final Path configFile;
    private final boolean hasConfigFile;
    private final Document document;
    private final XPath xpath;

    ConfigData(String configFilename) throws IOException, SAXException, ParserConfigurationException {
        this.configFilename = configFilename;
        configFile = Path.of(configFilename);
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

    private void validate() throws SAXException, IOException {  // TODO: augment error reporting (manage exceptions)
        var schemaFile = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("beanmaker-config.xsd"));
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

    boolean hasConfigFile() {
        return hasConfigFile;
    }

    String getConfigFilename() {
        return configFilename;
    }

    String getStringValue(String path) throws XPathExpressionException {
        return xpath.evaluate(path, document);
    }

    XMLElement getRootElement(String name, String schemaReference) {
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

    void writeConfig(String config) throws IOException {
        Files.writeString(configFile, XML_PRELUDE + config, StandardCharsets.UTF_8);
    }

}
