/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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