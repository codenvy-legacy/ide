/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.runner.docker;

import com.google.common.io.CharStreams;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helps create Dockerfile from template. Dockerfile is a text file to automate the steps to create an image. Templates typically contain
 * set of instructions for building an image but templates support parameter substitution to enable replacement of key parameters. Template
 * parameters are declared in the format $parameter$. For example:
 * <table border="1" cellpadding="1" cellspacing="0">
 * <tr align="left">
 * <th>Example</th>
 * <th>Description</th>
 * </tr>
 * <tr><td>CMD $cmd$</td><td>The instruction adds command which will run when Docker container starts</td></tr>
 * <tr><td>FROM $base_image$</td><td>The instruction sets the <i>Base Image</i> for subsequent instructions</td></tr>
 * </table>
 * <p/>
 * Usage example. In example bellow there is template for create image to run console java application. There are next set of parameters:
 * <ul>
 * <li>$app$ - name of jar file of application</li>
 * <li>$main_class$ - name of class than contains main method</li>
 * </ul>
 * <pre>
 *     ...
 *     String dockerFileTemplate = "FROM java_image\n" +
 *                                 "MAINTAINER Codenvy Corp\n" +
 *                                 "ADD $app$ /tmp/$app$\n" +
 *                                 "CMD /bin/bash -cl \"java -classpath /tmp/$app$ $main_class$\"";
 *     DockerfileTemplate template = DockerfileTemplate.of(dockerFileTemplate);
 *     template.setParameter("app", "hello.jar");
 *     template.setParameter("main_class", "test.helloworld.Main");
 *     template.writeDockerfile(System.out);
 * </pre>
 * In example above output is:
 * <pre>
 * FROM java_image
 * MAINTAINER Codenvy Corp
 * ADD hello.jar /tmp/hello.jar
 * CMD /bin/bash -cl "java -classpath /tmp/hello.jar test.helloworld.Main"
 * </pre>
 *
 * @author andrew00x
 */
public class DockerfileTemplate {
    private final static Pattern TEMPLATE_PATTERN = Pattern.compile("\\$[^\\$^\\$]+\\$");

    public static DockerfileTemplate of(java.io.File pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("null value is not allowed");
        }
        return new DockerfileTemplate(pattern);
    }

    public static DockerfileTemplate of(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("null value is not allowed");
        }
        return new DockerfileTemplate(pattern);
    }

    private final java.io.File        patternFile;
    private final String              patternString;
    private final Map<String, Object> parameters;

    public DockerfileTemplate setParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    public DockerfileTemplate setParameters(Map<String, ?> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    public Object getParameter(String name) {
        return parameters.get(name);
    }

    public DockerfileTemplate clearParameters() {
        parameters.clear();
        return this;
    }

    public void writeDockerfile(java.io.File path) throws IOException {
        try (FileWriter output = new FileWriter(path)) {
            writeDockerfile(output);
        }
    }

    public void writeDockerfile(Appendable output) throws IOException {
        try (Reader input = patternString == null ? new FileReader(patternFile) : new StringReader(patternString)) {
            StringBuilder buf = null;
            for (String line : CharStreams.readLines(input)) {
                final Matcher matcher = TEMPLATE_PATTERN.matcher(line);
                if (matcher.find()) {
                    int start = 0;
                    if (buf == null) {
                        buf = new StringBuilder();
                    } else {
                        buf.setLength(0);
                    }
                    do {
                        buf.append(line.substring(start, matcher.start()));
                        final String name = line.substring(matcher.start() + 1, matcher.end() - 1);
                        final Object value = parameters.get(name);
                        buf.append(value == null ? "" : String.valueOf(value));
                        start = matcher.end();
                    } while (matcher.find());
                    buf.append(line.substring(start));
                    output.append(buf);
                } else {
                    output.append(line);
                }
                output.append('\n');
            }
        }
    }

    private DockerfileTemplate(java.io.File pattern) {
        this.patternFile = pattern;
        this.patternString = null;
        this.parameters = new LinkedHashMap<>();
    }

    private DockerfileTemplate(String pattern) {
        this.patternFile = null;
        this.patternString = pattern;
        this.parameters = new LinkedHashMap<>();
    }
}
