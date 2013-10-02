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
package com.codenvy.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static junit.framework.Assert.assertEquals;

/** @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> */
public class TestExtensionManagerGenerator {

    @Test
    public void shouldMatchExtensions() {
        List<String> correct = new ArrayList<String>();
        correct.add("@Singleton @Extension  (title = \"Demo extension1\", id = \"ide.ext.demo\", version = \"2.0.0\")"
                    + "public class DemoExtension");
        // no whitespace after Extension keyword
        correct.add("@Singleton @Extension(title = \"Demo extension2\", id = \"ide.ext.demo\", version = \"2.0.0\")"
                    + "public class DemoExtension");
        // linebrake after Extension()
        correct.add("@Singleton "
                    + "@Extension  (title = \"Demo extension3\", id = \"ide.ext.demo\", version = \"2.0.0\") \n"
                    + "public class DemoExtension");
        matchExtensions(correct, true);
    }

    @Test
    public void shouldNotMatchExtensions() {
        List<String> incorrect = new ArrayList<String>();
        // no "@" char before extension
        incorrect.add("@Singleton Extension  (title = \"Demo extension1\", id = \"ide.ext.demo\", version = \"2.0.0\")"
                      + "public class DemoExtension");
        matchExtensions(incorrect, false);
    }

    /**
     * Checks that Extension Pattern matches or not @Extension annotations
     *
     * @param strings
     *         the collection of strings to test
     * @param expected
     *         expected result
     */
    protected void matchExtensions(List<String> strings, boolean expected) {
        for (String matchingString : strings) {
            Matcher matcher = ExtensionManagerGenerator.EXT_PATTERN.matcher(matchingString);
            assertEquals(String.format("Line '%s' should" + (expected ? "" : " not") + " match", matchingString),
                         expected, matcher.matches());
        }
    }

}
