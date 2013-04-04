/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.util;

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
        // no "@" char befor extension
        incorrect.add("@Singleton Extension  (title = \"Demo extension1\", id = \"ide.ext.demo\", version = \"2.0.0\")"
                      + "public class DemoExtension");
        matchExtensions(incorrect, false);
    }

    /**
     * Checks that Extension Pattern matches or not @Extension annotations
     *
     * @param strings
     *         the collection of strings to test
     * @param extected
     *         expected result
     */
    protected void matchExtensions(List<String> strings, boolean extected) {
        for (String matchingString : strings) {
            Matcher matcher = ExtensionManagerGenerator.EXT_PATTERN.matcher(matchingString);
            assertEquals(String.format("Line '%s' should" + (extected ? "" : " not") + " match", matchingString),
                         extected, matcher.matches());
        }
    }

}
