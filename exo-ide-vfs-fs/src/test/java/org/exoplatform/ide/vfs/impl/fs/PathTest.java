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
package org.exoplatform.ide.vfs.impl.fs;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.exoplatform.ide.vfs.impl.fs.LocalFileSystemTest.Pair;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PathTest extends TestCase {
    private Map<String, Pair<String, String[]>> legal;
    private String[]                            illegal;

    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        legal = new HashMap<String, Pair<String, String[]>>();
        legal.put("/a/b/c/d", new Pair("/a/b/c/d", new String[]{"a", "b", "c", "d"}));
        legal.put("/a/b/c/../d", new Pair("/a/b/d", new String[]{"a", "b", "d"}));
        legal.put("/a/b/c/./d", new Pair("/a/b/c/d", new String[]{"a", "b", "c", "d"}));
        illegal = new String[]{"..", "/a/../..", "/a/b/../../..", "/a/b/../../../c/././.."};
    }

    public void testPath() {
        for (Map.Entry<String, Pair<String, String[]>> e : legal.entrySet()) {
            Path parsed = Path.fromString(e.getKey());
            assertEquals(e.getValue().a, parsed.toString());
            assertTrue(
                    String.format("expected: %s but was: %s", Arrays.toString(e.getValue().b), Arrays.toString(parsed.elements())),
                    Arrays.equals(e.getValue().b, parsed.elements()));
        }
    }

    public void testSubPath() {
        final String raw = "/a/b/c/d";
        Path parsed = Path.fromString(raw);
        assertEquals("/c/d", parsed.subPath(2).toString());
    }

    public void testSubPath2() {
        final String raw = "/a/b/c/d/";
        Path parsed = Path.fromString(raw);
        assertEquals("/a/b/c", parsed.subPath(0, parsed.length() - 1).toString());
    }

    public void testNewPath() {
        final String raw = "/a/b";
        Path parsed = Path.fromString(raw);
        assertEquals("/a/b/c/d", parsed.newPath("/c/d").toString());
    }

    public void testChildPath() {
        Path parent = Path.fromString("/a/b/c");
        Path child1 = Path.fromString("/a/b/c/d");
        Path child2 = Path.fromString("/a/b/c/d/e");
        assertTrue(child1.isChild(parent));
        assertTrue(child2.isChild(parent));
        assertTrue(child2.isChild(child1));
        assertFalse(child1.isChild(child2));
        assertFalse(parent.isChild(child1));
    }

    public void testParentPath() {
        Path path = Path.fromString("/a/b/c/d");
        Path expectedParent = Path.fromString("/a/b/c");
        assertEquals(expectedParent, path.getParent());
    }

    public void testIllegalPath() {
        for (String s : illegal) {
            try {
                Path.fromString(s);
                fail(String.format("IllegalArgumentException expected for path '%s' ", s));
            } catch (IllegalArgumentException ok) {
                //System.err.println(ok.getMessage());
            }
        }
    }
}
