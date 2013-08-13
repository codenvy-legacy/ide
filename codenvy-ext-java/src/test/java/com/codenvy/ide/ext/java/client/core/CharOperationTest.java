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
package com.codenvy.ide.ext.java.client.core;

import com.codenvy.ide.ext.java.client.core.compiler.CharOperation;

import com.codenvy.ide.ext.java.client.BaseTest;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  10:02:58 AM 34360 2009-07-22 23:58:59Z evgen $
 */
@Ignore
public class CharOperationTest extends BaseTest {
    @Test
    public void testJavaIdentifierPart() {
        String s = "for";
        for (char c : s.toCharArray()) {
            if (!CharOperation.isJavaIdentifierPart(c))
                fail("Char '" + c + "' is valid Java identifier part");
        }
    }

    @Test
    public void testJavaIdentifierPartUnicode() {
        String s = "змінна";
        for (char c : s.toCharArray()) {
            if (!CharOperation.isJavaIdentifierPart(c))
                fail("Char '" + c + "' is valid Java identifier part");
        }
    }

    @Test
    public void testNotJavaIdentifierPart() {
        String s = "@#%*";
        for (char c : s.toCharArray()) {
            if (CharOperation.isJavaIdentifierPart(c))
                fail("Char '" + c + "' not valid Java identifier part");
        }
    }

    @Test
    public void testJavaIdentifierStart() {
        String s = "_$Ab";
        for (char c : s.toCharArray()) {
            if (!CharOperation.isJavaIdentifierStart(c))
                fail("Char '" + c + "' is valid Java identifier part");
        }
    }

    @Test
    public void testNotJavaIdentifierStart() {
        String s = "123@#&";
        for (char c : s.toCharArray()) {
            if (CharOperation.isJavaIdentifierStart(c))
                fail("Char '" + c + "' not valid Java identifier part");
        }
    }
}
