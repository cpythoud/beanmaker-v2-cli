package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.Columns;

import java.util.Arrays;
import java.util.List;

record SourceFileParameters(String packageName, String beanName, Columns columns) {

    List<String> getPackageElements() {
        return Arrays.asList(packageName.split("\\."));
    }

}
