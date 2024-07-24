package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.ProjectParameters;

import java.util.Set;

class CodeGenerationParameters implements ProjectParameters {

    private final Set<ProjectParameter> parameters;

    CodeGenerationParameters(Set<ProjectParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean createEditorFieldsConstructor() {
        return parameters.contains(ProjectParameter.EDITOR_FIELDS_CONSTRUCTOR);
    }

    @Override
    public boolean createSealedClasses() {
        return parameters.contains(ProjectParameter.SEALED_CLASSES);
    }

    @Override
    public boolean createDatabaseProviderReference() {
        return parameters.contains(ProjectParameter.DATABASE_PROVIDER_REFERENCE);
    }

}
