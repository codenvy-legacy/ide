/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.jseditor.client.changeintercept;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.codenvy.ide.jseditor.client.document.ReadOnlyDocument;
import com.codenvy.ide.jseditor.client.text.TextPosition;


/**
 * Test of the c-style bloc comment close interceptor.
 */
@RunWith(MockitoJUnitRunner.class)
public class CloseCStyleCommentChangeInterceptorTest {

    @Mock
    private ReadOnlyDocument document;

    /**
     * The input is a normal /* &#42;&#47; comment without leading spaces.
     */
    @Test
    public void testNotFirstLineNoLeadingSpaces() {
        final CloseCStyleCommentChangeInterceptor interceptor = new CloseCStyleCommentChangeInterceptor();
        doReturn("").when(document).getLineContent(0);
        doReturn("/*").when(document).getLineContent(1);
        doReturn(" *").when(document).getLineContent(2);
        final  TextChange input = new TextChange.Builder().from(new TextPosition(1, 2))
                                                          .to(new TextPosition(2, 2))
                                                          .insert("\n *")
                                                          .build();
        final TextChange output = interceptor.processChange(input, document);
        assertNotNull(output);
        final TextChange expected = new TextChange.Builder().from(new TextPosition(1, 2))
                                                            .to(new TextPosition(3, 3))
                                                            .insert("\n * \n */")
                                                            .build();
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testFirstLineNoLeadingSpaces() {
        final CloseCStyleCommentChangeInterceptor interceptor = new CloseCStyleCommentChangeInterceptor();
        doReturn("/*").when(document).getLineContent(0);
        doReturn(" *").when(document).getLineContent(1);
        final  TextChange input = new TextChange.Builder().from(new TextPosition(0, 2))
                                                          .to(new TextPosition(1, 2))
                                                          .insert("\n *")
                                                          .build();
        final TextChange output = interceptor.processChange(input, document);
        assertNotNull(output);
        final TextChange expected = new TextChange.Builder().from(new TextPosition(0, 2))
                                                            .to(new TextPosition(2, 3))
                                                            .insert("\n * \n */")
                                                            .build();
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testStartNotEmptyLine() {
        final CloseCStyleCommentChangeInterceptor interceptor = new CloseCStyleCommentChangeInterceptor();
        doReturn("whatever").when(document).getLineContent(0);
        doReturn("s/*").when(document).getLineContent(1);
        doReturn(" *").when(document).getLineContent(2);
        final  TextChange input = new TextChange.Builder().from(new TextPosition(1, 3))
                                                          .to(new TextPosition(2, 2))
                                                          .insert("\n *")
                                                          .build();
        final TextChange output = interceptor.processChange(input, document);
        assertNull(output);
    }

    @Test
    public void test3LeadingSpaces() {
        testWithLeading("   ");
    }

    @Test
    public void testLeadingTab() {
        testWithLeading("\t");
    }

    @Test
    public void testLeadingMixed() {
        testWithLeading(" \t");
    }

    private void testWithLeading(final String lead) {
        final CloseCStyleCommentChangeInterceptor interceptor = new CloseCStyleCommentChangeInterceptor();
        doReturn(lead + "/*").when(document).getLineContent(1);
        doReturn(lead + " *").when(document).getLineContent(2);
        final  TextChange input = new TextChange.Builder().from(new TextPosition(1, 2 + lead.length()))
                                                          .to(new TextPosition(2, 2 + lead.length()))
                                                          .insert("\n" + lead + " *")
                                                          .build();
        final TextChange output = interceptor.processChange(input, document);
        assertNotNull(output);
        final TextChange expected = new TextChange.Builder().from(new TextPosition(1, 2 + lead.length()))
                                                            .to(new TextPosition(3, 3  + lead.length()))
                                                            .insert("\n" + lead + " * "+"\n" + lead + " */")
                                                            .build();
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testAddWithComment() {
        final CloseCStyleCommentChangeInterceptor interceptor = new CloseCStyleCommentChangeInterceptor();
        doReturn("/*").when(document).getLineContent(0);
        doReturn("/*").when(document).getLineContent(1);
        doReturn(" *").when(document).getLineContent(2);
        final  TextChange input = new TextChange.Builder().from(new TextPosition(1, 2))
                                                          .to(new TextPosition(2, 2))
                                                          .insert("\n *")
                                                          .build();
        final TextChange output = interceptor.processChange(input, document);
        assertNull(output);
    }

    @Test
    public void testJavadocStyleComment() {
        final CloseCStyleCommentChangeInterceptor interceptor = new CloseCStyleCommentChangeInterceptor();
        doReturn("/**").when(document).getLineContent(0);
        doReturn(" *").when(document).getLineContent(1);
        final  TextChange input = new TextChange.Builder().from(new TextPosition(0, 3))
                                                          .to(new TextPosition(1, 2))
                                                          .insert("\n *")
                                                          .build();
        final TextChange output = interceptor.processChange(input, document);
        assertNotNull(output);
        final TextChange expected = new TextChange.Builder().from(new TextPosition(0, 3))
                                                            .to(new TextPosition(2, 3))
                                                            .insert("\n * \n */")
                                                            .build();
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testPasteWholeCommentStart() {
        final CloseCStyleCommentChangeInterceptor interceptor = new CloseCStyleCommentChangeInterceptor();
        doReturn("/**").when(document).getLineContent(0);
        doReturn(" *").when(document).getLineContent(1);
        final  TextChange input = new TextChange.Builder().from(new TextPosition(0, 0))
                                                          .to(new TextPosition(1, 2))
                                                          .insert("/**\n *")
                                                          .build();
        final TextChange output = interceptor.processChange(input, document);
        assertNull(output);
    }

    @Test
    public void testCloseComment() {
        final CloseCStyleCommentChangeInterceptor interceptor = new CloseCStyleCommentChangeInterceptor();
        doReturn("/**").when(document).getLineContent(0);
        doReturn(" *").when(document).getLineContent(1);
        final  TextChange input = new TextChange.Builder().from(new TextPosition(0, 0))
                                                          .to(new TextPosition(1, 2))
                                                          .insert("/**\n *")
                                                          .build();
        final TextChange output = interceptor.processChange(input, document);
        assertNull(output);
    }

}
