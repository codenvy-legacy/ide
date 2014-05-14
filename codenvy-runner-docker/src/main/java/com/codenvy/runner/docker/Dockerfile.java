/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
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

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author andrew00x
 */
public class Dockerfile {
    private List<String>        lines;
    private Map<String, Object> parameters;
    private List<DockerImage>   images;

    public List<String> getLines() {
        if (lines == null) {
            lines = new LinkedList<>();
        }
        return lines;
    }

    public List<DockerImage> getImages() {
        if (images == null) {
            images = new LinkedList<>();
        }
        return images;
    }

    public Map<String, Object> getParameters() {
        if (parameters == null) {
            parameters = new LinkedHashMap<>();
        }
        return parameters;
    }

    public void writeDockerfile(java.io.File path) throws IOException {
        try (FileWriter output = new FileWriter(path)) {
            writeDockerfile(output);
        }
    }

    public void writeDockerfile(Appendable output) throws IOException {
        StringBuilder buf = null;
        for (String line : getLines()) {
            final Matcher matcher = DockerfileParser.TEMPLATE_PATTERN.matcher(line);
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
