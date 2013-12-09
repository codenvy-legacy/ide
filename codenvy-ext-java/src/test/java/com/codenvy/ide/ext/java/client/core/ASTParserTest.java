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

import com.codenvy.ide.ext.java.jdt.core.dom.TypeDeclaration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 34360 2009-07-22 23:58:59Z evgen $
 */
public class ASTParserTest extends ParserBaseTest {

    @Test
    public void testParseUnit() throws Exception {
        assertFalse(unit.types().size() == 0);
        assertEquals(1, unit.types().size());
    }

    @Test
    public void testPareseClass() throws Exception {
        TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
        assertEquals("CreateJavaClassPresenter", td.getName().getFullyQualifiedName());
    }

    @Test
    public void testParseInnerType() throws Exception {
        TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
        assertEquals(1, td.getTypes().length);
        TypeDeclaration innerType = td.getTypes()[0];
        assertEquals("Display", innerType.getName().getFullyQualifiedName());
    }

    @Test
    public void testInnerTypeMethods() throws Exception {
        TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
        TypeDeclaration innerType = td.getTypes()[0];
        assertEquals(19, innerType.getMethods().length);
    }

    @Test
    public void testInnerTypeFields() throws Exception {
        TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
        TypeDeclaration innerType = td.getTypes()[0];
        assertEquals(1, innerType.getFields().length);
    }

}