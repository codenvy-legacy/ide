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
package com.codenvy.runner.docker.dockerfile;

import com.codenvy.api.core.util.Pair;

import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author andrew00x
 */
public class DockerfileParserTest {
    @Test
    public void testParse() throws Exception {
        String dockerfileContent = "# Comment 1\n" +
                                   "FROM base_image\n" +
                                   "MAINTAINER Codenvy Corp\n" +
                                   "# Comment 2\n" +
                                   "RUN echo 1 > /dev/null\n" +
                                   "RUN echo 2 > /dev/null\n" +
                                   "RUN echo 3 > /dev/null\n" +
                                   "ADD file1 /tmp/file1\n" +
                                   "ADD http://example.com/folder/some_file.txt /tmp/file.txt  \n" +
                                   "EXPOSE 6000 7000\n" +
                                   "EXPOSE 8000   9000\n" +
                                   "# Comment 3\n" +
                                   "ENV ENV_VAR1 hello world\n" +
                                   "ENV ENV_VAR2\t to be or not to be\n" +
                                   "VOLUME [\"/data1\", \t\"/data2\"]\n" +
                                   "USER andrew\n" +
                                   "WORKDIR /tmp\n" +
                                   "ENTRYPOINT echo hello > /dev/null\n" +
                                   "CMD echo hello > /tmp/test";
        Dockerfile[] dockerfiles = DockerfileParser.parse(new StringReader(dockerfileContent));
        Assert.assertEquals(1, dockerfiles.length);
        Dockerfile dockerfile = dockerfiles[0];
        Assert.assertEquals("base_image", dockerfile.getFrom());
        Assert.assertEquals(Arrays.asList("Codenvy Corp"), dockerfile.getMaintainer());
        Assert.assertEquals(Arrays.asList("echo 1 > /dev/null", "echo 2 > /dev/null", "echo 3 > /dev/null"), dockerfile.getRun());
        Assert.assertEquals("echo hello > /tmp/test", dockerfile.getCmd());
        Assert.assertEquals(Arrays.asList("6000", "7000", "8000", "9000"), dockerfile.getExpose());
        Map<String, String> env = new LinkedHashMap<>();
        env.put("ENV_VAR1", "hello world");
        env.put("ENV_VAR2", "to be or not to be");
        Assert.assertEquals(env, dockerfile.getEnv());
        Assert.assertEquals(
                Arrays.asList(Pair.of("file1", "/tmp/file1"), Pair.of("http://example.com/folder/some_file.txt", "/tmp/file.txt")),
                dockerfile.getAdd());
        Assert.assertEquals("echo hello > /dev/null", dockerfile.getEntrypoint());
        Assert.assertEquals(Arrays.asList("/data1", "/data2"), dockerfile.getVolume());
        Assert.assertEquals("andrew", dockerfile.getUser());
        Assert.assertEquals("/tmp", dockerfile.getWorkdir());
    }

    @Test
    public void testParseMultipleImages() throws Exception {
        String dockerfileContent = "# Image 1\n" +
                                   "FROM base_image_1\n" +
                                   "MAINTAINER Codenvy Corp\n" +
                                   "RUN echo 1 > /dev/null\n" +
                                   "ADD http://example.com/folder/some_file.txt /tmp/file.txt  \n" +
                                   "EXPOSE 6000 7000\n" +
                                   "ENV ENV_VAR\t to be or not to be\n" +
                                   "VOLUME [\"/data1\"]\n" +
                                   "USER andrew\n" +
                                   "WORKDIR /tmp\n" +
                                   "ENTRYPOINT echo hello > /dev/null\n" +
                                   "CMD echo hello > /tmp/test1" +
                                   "\n" +
                                   "\n" +
                                   "# Image 2\n" +
                                   "FROM base_image_2\n" +
                                   "MAINTAINER Codenvy Corp\n" +
                                   "RUN echo 2 > /dev/null\n" +
                                   "ADD file1 /tmp/file1\n" +
                                   "EXPOSE 8000 9000\n" +
                                   "ENV ENV_VAR\t to be or not to be\n" +
                                   "VOLUME [\"/data2\"]\n" +
                                   "USER andrew\n" +
                                   "WORKDIR /home/andrew\n" +
                                   "ENTRYPOINT echo test > /dev/null\n" +
                                   "CMD echo hello > /tmp/test2";
        Dockerfile[] dockerfiles = DockerfileParser.parse(new StringReader(dockerfileContent));
        Assert.assertEquals(2, dockerfiles.length);
        Dockerfile dockerfile1 = dockerfiles[0];
        Assert.assertEquals("base_image_1", dockerfile1.getFrom());
        Assert.assertEquals(Arrays.asList("Codenvy Corp"), dockerfile1.getMaintainer());
        Assert.assertEquals(Arrays.asList("echo 1 > /dev/null"), dockerfile1.getRun());
        Assert.assertEquals("echo hello > /tmp/test1", dockerfile1.getCmd());
        Assert.assertEquals(Arrays.asList("6000", "7000"), dockerfile1.getExpose());
        Map<String, String> env1 = new LinkedHashMap<>();
        env1.put("ENV_VAR", "to be or not to be");
        Assert.assertEquals(env1, dockerfile1.getEnv());
        Assert.assertEquals(Arrays.asList(Pair.of("http://example.com/folder/some_file.txt", "/tmp/file.txt")),
                            dockerfile1.getAdd());
        Assert.assertEquals("echo hello > /dev/null", dockerfile1.getEntrypoint());
        Assert.assertEquals(Arrays.asList("/data1"), dockerfile1.getVolume());
        Assert.assertEquals("andrew", dockerfile1.getUser());
        Assert.assertEquals("/tmp", dockerfile1.getWorkdir());

        Dockerfile dockerfile2 = dockerfiles[1];
        Assert.assertEquals("base_image_2", dockerfile2.getFrom());
        Assert.assertEquals(Arrays.asList("Codenvy Corp"), dockerfile2.getMaintainer());
        Assert.assertEquals(Arrays.asList("echo 2 > /dev/null"), dockerfile2.getRun());
        Assert.assertEquals("echo hello > /tmp/test2", dockerfile2.getCmd());
        Assert.assertEquals(Arrays.asList("8000", "9000"), dockerfile2.getExpose());
        Map<String, String> env2 = new LinkedHashMap<>();
        env2.put("ENV_VAR", "to be or not to be");
        Assert.assertEquals(env2, dockerfile2.getEnv());
        Assert.assertEquals(Arrays.asList(Pair.of("file1", "/tmp/file1")), dockerfile2.getAdd());
        Assert.assertEquals("echo test > /dev/null", dockerfile2.getEntrypoint());
        Assert.assertEquals(Arrays.asList("/data2"), dockerfile2.getVolume());
        Assert.assertEquals("andrew", dockerfile2.getUser());
        Assert.assertEquals("/home/andrew", dockerfile2.getWorkdir());
    }
}
