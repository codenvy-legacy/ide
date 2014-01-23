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

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author andrew00x
 */
public class DockerfileTemplateTest {
    @Test
    public void testTemplate() throws Exception {
        String templateContent = "FROM $from$\n" +
                                 "MAINTAINER Codenvy Corp\n" +
                                 "ADD $app$ /tmp/$app$\n" +
                                 "CMD /bin/bash -cl \"java $jvm_args$ -classpath /tmp/$app$ $main_class$ $prg_args$\"\n";
        String expected = "FROM base\n" +
                          "MAINTAINER Codenvy Corp\n" +
                          "ADD hello.jar /tmp/hello.jar\n" +
                          "CMD /bin/bash -cl \"java -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n -classpath /tmp/hello.jar test.Main name=andrew\"\n";
        DockerfileTemplate template = DockerfileTemplate.of(templateContent);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", "base");
        parameters.put("app", "hello.jar");
        parameters.put("jvm_args", "-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n");
        parameters.put("main_class", "test.Main");
        parameters.put("prg_args", "name=andrew");
        StringBuilder buf = new StringBuilder();
        template.setParameters(parameters).writeDockerfile(buf);
        Assert.assertEquals(expected, buf.toString());
    }
}
