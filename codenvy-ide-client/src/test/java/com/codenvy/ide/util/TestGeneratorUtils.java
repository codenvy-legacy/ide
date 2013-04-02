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

import java.io.IOException;
import java.util.regex.Matcher;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/** @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> */
public class TestGeneratorUtils {

    /** Should match package name */
    @Test
    public void shouldMatchPackage() {
        String packageString =
                "* along with this program; if not, see<http://www.gnu.org/licenses/>.\n" + "*/ \n"
                + "package com.codenvy.ide.util;" + "import junit.framework.Assert;";
        Matcher matcher = GeneratorUtils.PACKAGE_PATTERN.matcher(packageString);
        assertTrue(matcher.matches());
        assertEquals(1, matcher.groupCount());
        String group = matcher.group(1);

        assertEquals("com.codenvy.ide.util", group);
    }

    /**
     * Should match package name
     *
     * @throws IOException
     */
    @Test
    public void shouldExtractPackage() throws IOException {
        String packageString =
                "* along with this program; if not, see<http://www.gnu.org/licenses/>.\n" + "*/ \n"
                + "package com.codenvy.ide.util;" + "import junit.framework.Assert;";
        assertEquals("com.codenvy.ide.util", GeneratorUtils.getClassFQN("dummy", packageString));
    }
}
