package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.Columns;
import org.beanmaker.v2.codegen.ProjectParameters;

import java.util.Arrays;
import java.util.List;

record SourceFileParameters(
        String packageName,
        String beanName,
        Columns columns,
        ProjectParameters projectParameters)
{
    List<String> getPackageElements() {
        return Arrays.asList(packageName.split("\\."));
    }
}
