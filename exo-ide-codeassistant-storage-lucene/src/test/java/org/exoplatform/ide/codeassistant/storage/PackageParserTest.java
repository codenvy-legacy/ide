/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package org.exoplatform.ide.codeassistant.storage;

import org.junit.Test;

import java.io.FileInputStream;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 2:59:49 PM Mar 5, 2012 evgen $
 */
public class PackageParserTest {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final String PATH_TO_RT_JAR = System.getProperty("java.home") + FILE_SEPARATOR + "lib"
                                                 + FILE_SEPARATOR + "rt.jar";

    @Test
    public void parseJar() throws Exception {
        Set<String> set = PackageParser.parse(new FileInputStream(PATH_TO_RT_JAR));
        assertThat(set).contains("java", "java.lang", "java.util", "java.io");
    }

    @Test
    public void noClass() throws Exception {
        Set<String> set = PackageParser.parse(new FileInputStream(PATH_TO_RT_JAR));
        assertThat(set).excludes("java.lang.Object.java", "java.io.File");
    }
}
