/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.client.core;

import com.codenvy.ide.ext.java.client.BaseTest;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateBuffer;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateException;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateTranslator;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateVariable;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/** @since 3.3 */
public class TemplateTranslatorTest extends BaseTest {

    private TemplateTranslator fTranslator;

    @Before
    public void gwtSetUp() throws Exception {
        fTranslator = new TemplateTranslator();
    }

    public void testNullTemplate() throws Exception {
        try {
            fTranslator.translate((String)null);
            fail();
        } catch (NullPointerException x) {
        }
    }

    @Test
    public void testEmptyTemplate() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("");
        assertNull(fTranslator.getErrorMessage());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(0, vars.length);
        assertEquals("", buffer.getString());
    }

    @Test
    public void testNoVarTemplate() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("foo bar");
        assertNull(fTranslator.getErrorMessage());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(0, vars.length);
        assertEquals("foo bar", buffer.getString());
    }

    @Test
    public void testSimpleTemplate() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("foo ${var} bar");
        assertNull(fTranslator.getErrorMessage());
        assertEquals("foo var bar", buffer.getString());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(1, vars.length);
        assertEquals("var", vars[0].getName());
        assertEquals(1, vars[0].getOffsets().length);
        assertEquals(4, vars[0].getOffsets()[0]);
        assertEquals(3, vars[0].getLength());
        assertEquals(false, vars[0].isUnambiguous());
        assertEquals("var", vars[0].getDefaultValue());
        assertEquals(1, vars[0].getValues().length);
        assertEquals(vars[0].getDefaultValue(), vars[0].getValues()[0]);
        assertEquals("var", vars[0].getType());
    }

    @Test
    public void testMultiTemplate() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("foo ${var} bar ${var} end");
        assertNull(fTranslator.getErrorMessage());
        assertEquals("foo var bar var end", buffer.getString());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(1, vars.length);
        assertEquals("var", vars[0].getName());
        assertEquals(2, vars[0].getOffsets().length);
        assertEquals(4, vars[0].getOffsets()[0]);
        assertEquals(12, vars[0].getOffsets()[1]);
        assertEquals(3, vars[0].getLength());
        assertEquals(false, vars[0].isUnambiguous());
        assertEquals("var", vars[0].getDefaultValue());
        assertEquals(1, vars[0].getValues().length);
        assertEquals(vars[0].getDefaultValue(), vars[0].getValues()[0]);
        assertEquals("var", vars[0].getType());
    }

    @Test
    public void testIllegalSyntax1() throws Exception {
        ensureFailure("foo ${var");
    }

    private void ensureFailure(String template) {
        try {
            fTranslator.translate(template);
            fail();
        } catch (TemplateException e) {
        }
    }

    @Test
    public void testIllegalSyntax2() throws Exception {
        ensureFailure("foo $");
    }

    @Test
    public void testIllegalSyntax3() throws Exception {
        ensureFailure("foo ${] } bar");
    }

    @Test
    public void testDollar() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("foo $$ bar");
        assertNull(fTranslator.getErrorMessage());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(0, vars.length);
        assertEquals("foo $ bar", buffer.getString());
    }

    @Test
    public void testEmptyVariable() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("foo ${} bar");
        assertNull(fTranslator.getErrorMessage());
        assertEquals("foo  bar", buffer.getString());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(1, vars.length);
        assertEquals("", vars[0].getName());
        assertEquals(1, vars[0].getOffsets().length);
        assertEquals(4, vars[0].getOffsets()[0]);
        assertEquals(0, vars[0].getLength());
        assertEquals(false, vars[0].isUnambiguous());
        assertEquals("", vars[0].getDefaultValue());
        assertEquals(1, vars[0].getValues().length);
        assertEquals(vars[0].getDefaultValue(), vars[0].getValues()[0]);
        assertEquals("", vars[0].getType());
    }

    /* 3.3 typed template variables */
    @Test
    public void testTypedTemplate() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("foo ${var:type} bar");
        assertNull(fTranslator.getErrorMessage());
        assertEquals("foo var bar", buffer.getString());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(1, vars.length);
        assertEquals("var", vars[0].getName());
        assertEquals(1, vars[0].getOffsets().length);
        assertEquals(4, vars[0].getOffsets()[0]);
        assertEquals(3, vars[0].getLength());
        assertEquals(false, vars[0].isUnambiguous());
        assertEquals("var", vars[0].getDefaultValue());
        assertEquals(1, vars[0].getValues().length);
        assertEquals("type", vars[0].getType());
        assertEquals(vars[0].getDefaultValue(), vars[0].getValues()[0]);
    }

    @Test
    public void testParameterizedTypeTemplate() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("foo ${var:type(param)} bar");
        assertNull(fTranslator.getErrorMessage());
        assertEquals("foo var bar", buffer.getString());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(1, vars.length);
        assertEquals("var", vars[0].getName());
        assertEquals(1, vars[0].getOffsets().length);
        assertEquals(4, vars[0].getOffsets()[0]);
        assertEquals(3, vars[0].getLength());
        assertEquals(false, vars[0].isUnambiguous());
        assertEquals("var", vars[0].getDefaultValue());
        assertEquals(1, vars[0].getValues().length);
        assertEquals(vars[0].getDefaultValue(), vars[0].getValues()[0]);
        assertEquals("type", vars[0].getType());
        assertEquals(Collections.singletonList("param"), vars[0].getVariableType().getParams());
    }

    @Test
    public void testMultiParameterizedTypeTemplate1() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("foo ${var:type(param)} bar ${var:type(param)} end");
        assertNull(fTranslator.getErrorMessage());
        assertEquals("foo var bar var end", buffer.getString());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(1, vars.length);
        assertEquals("var", vars[0].getName());
        assertEquals(2, vars[0].getOffsets().length);
        assertEquals(4, vars[0].getOffsets()[0]);
        assertEquals(12, vars[0].getOffsets()[1]);
        assertEquals(3, vars[0].getLength());
        assertEquals(false, vars[0].isUnambiguous());
        assertEquals("var", vars[0].getDefaultValue());
        assertEquals(1, vars[0].getValues().length);
        assertEquals(vars[0].getDefaultValue(), vars[0].getValues()[0]);
        assertEquals("type", vars[0].getType());
        assertEquals(Collections.singletonList("param"), vars[0].getVariableType().getParams());
    }

    @Test
    public void testMultiParameterizedTypeTemplate2() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("foo ${var:type(param)} bar ${var} end");
        assertNull(fTranslator.getErrorMessage());
        assertEquals("foo var bar var end", buffer.getString());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(1, vars.length);
        assertEquals("var", vars[0].getName());
        assertEquals(2, vars[0].getOffsets().length);
        assertEquals(4, vars[0].getOffsets()[0]);
        assertEquals(12, vars[0].getOffsets()[1]);
        assertEquals(3, vars[0].getLength());
        assertEquals(false, vars[0].isUnambiguous());
        assertEquals("var", vars[0].getDefaultValue());
        assertEquals(1, vars[0].getValues().length);
        assertEquals(vars[0].getDefaultValue(), vars[0].getValues()[0]);
        assertEquals("type", vars[0].getType());
        assertEquals(Collections.singletonList("param"), vars[0].getVariableType().getParams());
    }

    @Test
    public void testIllegallyParameterizedTypeTemplate() throws Exception {
        ensureFailure("foo ${var:type(param)} bar ${var:type(other)} end");
        ensureFailure("foo ${var:type(param)} bar ${var:type} end");
    }

    @Test
    public void testParameterizedTypeTemplateWithWhitespace() throws Exception {
        TemplateBuffer buffer = fTranslator.translate("foo ${ var : type ( param1 , param2 , param3 ) } bar");
        assertNull(fTranslator.getErrorMessage());
        assertEquals("foo var bar", buffer.getString());
        TemplateVariable[] vars = buffer.getVariables();
        assertEquals(1, vars.length);
        assertEquals("var", vars[0].getName());
        assertEquals(1, vars[0].getOffsets().length);
        assertEquals(4, vars[0].getOffsets()[0]);
        assertEquals(3, vars[0].getLength());
        assertEquals(false, vars[0].isUnambiguous());
        assertEquals("var", vars[0].getDefaultValue());
        assertEquals(1, vars[0].getValues().length);
        assertEquals(vars[0].getDefaultValue(), vars[0].getValues()[0]);
        assertEquals("type", vars[0].getType());
        List params = new ArrayList(2);
        params.add("param1");
        params.add("param2");
        params.add("param3");
        assertEquals(params, vars[0].getVariableType().getParams());
    }

    //TODO this test ignored. HtmlUnit has a bug with RegExp emulation
    //   public void testQualifiedTypeTemplate() throws Exception
    //   {
    //      TemplateBuffer buffer = fTranslator.translate("foo ${ var : qual.type ( qual.param1, qual.param2 ) } bar");
    //      assertNull(fTranslator.getErrorMessage());
    //      assertEquals("foo var bar", buffer.getString());
    //      TemplateVariable[] vars = buffer.getVariables();
    //      assertEquals(1, vars.length);
    //      assertEquals("var", vars[0].getName());
    //      assertEquals(1, vars[0].getOffsets().length);
    //      assertEquals(4, vars[0].getOffsets()[0]);
    //      assertEquals(3, vars[0].getLength());
    //      assertEquals(false, vars[0].isUnambiguous());
    //      assertEquals("var", vars[0].getDefaultValue());
    //      assertEquals(1, vars[0].getValues().length);
    //      assertEquals(vars[0].getDefaultValue(), vars[0].getValues()[0]);
    //      assertEquals("qual.type", vars[0].getType());
    //      List params = new ArrayList(2);
    //      params.add("qual.param1");
    //      params.add("qual.param2");
    //      assertEquals(params, vars[0].getVariableType().getParams());
    //   }

    //   public void testTextParameterTemplate() throws Exception
    //   {
    //      TemplateBuffer buffer =
    //         fTranslator.translate("foo ${ var : qual.type ( 'a parameter 1', qual.param2, 'a parameter ''3' ) } bar");
    //      assertNull(fTranslator.getErrorMessage());
    //      assertEquals("foo var bar", buffer.getString());
    //      TemplateVariable[] vars = buffer.getVariables();
    //      assertEquals(1, vars.length);
    //      assertEquals("var", vars[0].getName());
    //      assertEquals(1, vars[0].getOffsets().length);
    //      assertEquals(4, vars[0].getOffsets()[0]);
    //      assertEquals(3, vars[0].getLength());
    //      assertEquals(false, vars[0].isUnambiguous());
    //      assertEquals("var", vars[0].getDefaultValue());
    //      assertEquals(1, vars[0].getValues().length);
    //      assertEquals(vars[0].getDefaultValue(), vars[0].getValues()[0]);
    //      assertEquals("qual.type", vars[0].getType());
    //      List params = new ArrayList(3);
    //      params.add("a parameter 1");
    //      params.add("qual.param2");
    //      params.add("a parameter '3");
    //      assertEquals(params, vars[0].getVariableType().getParams());
    //   }
    @Test
    public void testIllegalSyntax4() throws Exception {
        ensureFailure("foo ${var:} bar");
    }

    @Test
    public void testIllegalSyntax5() throws Exception {
        ensureFailure("foo ${var:type(} bar");
    }

    @Test
    public void testIllegalSyntax6() throws Exception {
        ensureFailure("foo ${var:type(] )} bar");
    }

    @Test
    public void testIllegalSyntax7() throws Exception {
        ensureFailure("foo ${var:type((} bar");
    }

}
