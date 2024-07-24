package org.beanmaker.v2.cli;

enum ProjectParameter {
    EDITOR_FIELDS_CONSTRUCTOR("editor-fields-constructor"),
    SEALED_CLASSES("sealed-classes"),
    DATABASE_PROVIDER_REFERENCE("database-provider-reference");

    private final String xmlName;

    ProjectParameter(String xmlName) {
        this.xmlName = xmlName;
    }

    public String xmlName() {
        return xmlName;
    }

}
