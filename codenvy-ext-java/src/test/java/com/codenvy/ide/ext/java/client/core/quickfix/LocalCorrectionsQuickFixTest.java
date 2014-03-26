/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla <bmuskalla@innoopract.com> - [quick fix] Shouldn't offer "Add throws declaration" quickfix for overriding
 *     signature if result would conflict with overridden signature
 *******************************************************************************/
package com.codenvy.ide.ext.java.client.core.quickfix;

import com.codenvy.ide.ext.java.client.editor.JavaCorrectionAssistant;
import com.codenvy.ide.ext.java.client.editor.JavaCorrectionProcessor;
import com.codenvy.ide.ext.java.emul.FileSystem;
import com.codenvy.ide.ext.java.jdt.CUVariables;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.formatter.DefaultCodeFormatterConstants;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.StubUtility;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.AssistContext;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.CUCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.templates.CodeTemplateContextType;
import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentImpl;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class LocalCorrectionsQuickFixTest extends QuickFixTest {
    @Mock
    private Project project;

    @Mock
    private File activeFle;

    @Mock
    private CUVariables cuVariables;

    @Before
    public void setUp() throws Exception {
        HashMap<String, String> options = TestOptions.getDefaultOptions();
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
        options.put(DefaultCodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE, String.valueOf(99));
        options.put(JavaCore.COMPILER_PB_STATIC_ACCESS_RECEIVER, JavaCore.ERROR);
        options.put(JavaCore.COMPILER_PB_UNCHECKED_TYPE_OPERATION, JavaCore.IGNORE);
        options.put(JavaCore.COMPILER_PB_MISSING_HASHCODE_METHOD, JavaCore.WARNING);

        new JavaCorrectionProcessor(new JavaCorrectionAssistant(null, null, null), null, null);
        new WorkerMessageHandler(null);
        GwtReflectionUtils.setPrivateFieldValue(WorkerMessageHandler.get(), "nameEnvironment", new FileSystem(
                new String[]{System.getProperty("java.home") + "/lib/rt.jar"}, null, "UTF-8"));
        when(activeFle.getProject()).thenReturn(project);
        when(activeFle.getName()).thenReturn("TestClass.java");
        when(activeFle.getPath()).thenReturn("/MyProject/src/main/java/my/test/TestClass.java");
        when(project.getPath()).thenReturn("/MyProject/src/main/java/my/test");

        when(cuVariables.getFileName()).thenReturn("TestClass.java");
        when(cuVariables.getPackageName()).thenReturn("my.test");
        when(cuVariables.getProjectName()).thenReturn("MyProject");

        GwtReflectionUtils.setPrivateFieldValue(WorkerMessageHandler.get(), "cuVar", cuVariables);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StubUtility.setCodeTemplate(CodeTemplateContextType.CATCHBLOCK_ID, "");
        StubUtility.setCodeTemplate(CodeTemplateContextType.CONSTRUCTORSTUB_ID, "");
        StubUtility.setCodeTemplate(CodeTemplateContextType.METHODSTUB_ID, "");

    }

    @Test
    public void testFieldAccessToStatic() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.File;\n");
        buf.append("public class E {\n");
        buf.append("    public char foo() {\n");
        buf.append("        return (new File(\"x.txt\")).separatorChar;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.File;\n");
        buf.append("public class E {\n");
        buf.append("    public char foo() {\n");
        buf.append("        return File.separatorChar;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    //   public void testInheritedAccessOnStatic() throws Exception
    //   {
    //
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package pack;\n");
    //      buf.append("public class A {\n");
    //      buf.append("    public static void foo() {\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //
    //      buf = new StringBuffer();
    //      buf.append("package pack;\n");
    //      buf.append("public class B extends A {\n");
    //      buf.append("}\n");
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import pack.B;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    public void foo(B b) {\n");
    //      buf.append("        b.foo();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //
    //      Document cu = new DocumentImpl(buf.toString());
    //      CompilationUnit astRoot = getASTRoot(cu, "E");
    //      ArrayList proposals = collectCorrections(cu, astRoot);
    //      assertNumberOfProposals(proposals, 3);
    //      assertCorrectLabels(proposals);
    //
    //      CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
    //      String preview1 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import pack.B;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    public void foo(B b) {\n");
    //      buf.append("        B.foo();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected1 = buf.toString();
    //
    //      proposal = (CUCorrectionProposal)proposals.get(1);
    //      String preview2 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import pack.A;\n");
    //      buf.append("import pack.B;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    public void foo(B b) {\n");
    //      buf.append("        A.foo();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected2 = buf.toString();
    //
    //      proposal = (CUCorrectionProposal)proposals.get(2);
    //      String preview3 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package pack;\n");
    //      buf.append("public class A {\n");
    //      buf.append("    public void foo() {\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected3 = buf.toString();
    //
    //      assertEqualStringsIgnoreOrder(new String[]{preview1, preview2, preview3}, new String[]{expected1, expected2,
    //         expected3});
    //   }

    //   public void testInheritedAccessOnStaticInGeneric() throws Exception
    //   {
    //
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package pack;\n");
    //      buf.append("public class A<T> {\n");
    //      buf.append("    public static void foo() {\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //
    //      buf = new StringBuffer();
    //      buf.append("package pack;\n");
    //      buf.append("public class B<T> extends A<String> {\n");
    //      buf.append("}\n");
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import pack.B;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    public void foo(B<Number> b) {\n");
    //      buf.append("        b.foo();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //
    //      Document cu = new DocumentImpl(buf.toString());
    //      CompilationUnit astRoot = getASTRoot(cu, "E");
    //      ArrayList proposals = collectCorrections(cu, astRoot);
    //      assertNumberOfProposals(proposals, 3);
    //      assertCorrectLabels(proposals);
    //
    //      CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
    //      String preview1 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import pack.B;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    public void foo(B<Number> b) {\n");
    //      buf.append("        B.foo();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected1 = buf.toString();
    //
    //      proposal = (CUCorrectionProposal)proposals.get(1);
    //      String preview2 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import pack.A;\n");
    //      buf.append("import pack.B;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    public void foo(B<Number> b) {\n");
    //      buf.append("        A.foo();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected2 = buf.toString();
    //
    //      proposal = (CUCorrectionProposal)proposals.get(2);
    //      String preview3 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package pack;\n");
    //      buf.append("public class A<T> {\n");
    //      buf.append("    public void foo() {\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected3 = buf.toString();
    //
    //      assertEqualStringsIgnoreOrder(new String[]{preview1, preview2, preview3}, new String[]{expected1, expected2,
    //         expected3});
    //   }
    @Test
    public void testQualifiedAccessToStatic() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Thread t) throws InterruptedException {\n");
        buf.append("        t.sleep(10);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Thread t) throws InterruptedException {\n");
        buf.append("        Thread.sleep(10);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testThisAccessToStatic() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public static void goo() {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        this.goo();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public static void goo() {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        E.goo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        this.goo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    @Ignore
    public void testThisAccessToStaticField() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public static int fCount;\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        this.fCount= 1;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public static int fCount;\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        E.fCount= 1;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public int fCount;\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        this.fCount= 1;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});

    }

    @Test
    public void testCastMissingInVarDecl() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Object o) {\n");
        buf.append("        Thread th= o;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Object o) {\n");
        buf.append("        Thread th= (Thread) o;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Object o) {\n");
        buf.append("        Object th= o;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(2);
        String preview3 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Thread o) {\n");
        buf.append("        Thread th= o;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2, preview3}, new String[]{expected1, expected2,
                                                                                               expected3});
    }

    @Test
    @Ignore
    public void testCastMissingInVarDecl2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class Container {\n");
        buf.append("    public List[] getLists() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Container c) {\n");
        buf.append("         ArrayList[] lists= c.getLists();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Container c) {\n");
        buf.append("         ArrayList[] lists= (ArrayList[]) c.getLists();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Container c) {\n");
        buf.append("         List[] lists= c.getLists();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(2);
        String preview3 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class Container {\n");
        buf.append("    public ArrayList[] getLists() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2, preview3}, new String[]{expected1, expected2,
                                                                                               expected3});

    }

    @Test
    public void testCastMissingInVarDecl3() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Thread th= foo();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public Thread foo() {\n");
        buf.append("        Thread th= foo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1}, new String[]{expected1});
    }

    @Test
    @Ignore
    public void testCastMissingInVarDecl4() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class Container {\n");
        buf.append("    public List getLists()[] {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("public class E extends Container {\n");
        buf.append("    public void foo() {\n");
        buf.append("         ArrayList[] lists= super.getLists();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("public class E extends Container {\n");
        buf.append("    public void foo() {\n");
        buf.append("         ArrayList[] lists= (ArrayList[]) super.getLists();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E extends Container {\n");
        buf.append("    public void foo() {\n");
        buf.append("         List[] lists= super.getLists();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(2);
        String preview3 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class Container {\n");
        buf.append("    public ArrayList[] getLists() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2, preview3}, new String[]{expected1, expected2,
                                                                                               expected3});

    }

    @Test
    public void testCastMissingInFieldDecl() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    int time= System.currentTimeMillis();\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    int time= (int) System.currentTimeMillis();\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    long time= System.currentTimeMillis();\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testCastMissingInAssignment() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Iterator;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Iterator iter) {\n");
        buf.append("        String str;\n");
        buf.append("        str= iter.next();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Iterator;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Iterator iter) {\n");
        buf.append("        String str;\n");
        buf.append("        str= (String) iter.next();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Iterator;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Iterator iter) {\n");
        buf.append("        Object str;\n");
        buf.append("        str= iter.next();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});

    }

    @Test
    public void testCastMissingInAssignment2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Iterator;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Iterator iter) {\n");
        buf.append("        String str, str2;\n");
        buf.append("        str= iter.next();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Iterator;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Iterator iter) {\n");
        buf.append("        String str, str2;\n");
        buf.append("        str= (String) iter.next();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Iterator;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Iterator iter) {\n");
        buf.append("        Object str;\n");
        buf.append("        String str2;\n");
        buf.append("        str= iter.next();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});

    }

    @Test
    public void testCastMissingInExpression() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public String[] foo(List list) {\n");
        buf.append("        return list.toArray(new List[list.size()]);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public String[] foo(List list) {\n");
        buf.append("        return (String[]) list.toArray(new List[list.size()]);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public Object[] foo(List list) {\n");
        buf.append("        return list.toArray(new List[list.size()]);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testCastOnCastExpression() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(List list) {\n");
        buf.append("        ArrayList a= (Cloneable) list;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(List list) {\n");
        buf.append("        ArrayList a= (ArrayList) list;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(List list) {\n");
        buf.append("        Cloneable a= (Cloneable) list;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUncaughtException() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException {\n");
        buf.append("    }\n");
        buf.append("    public void foo() throws IOException {\n");
        buf.append("        goo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUncaughtException2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public String goo() throws IOException {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * Not much to say here.\n");
        buf.append("     */\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo().substring(2);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public String goo() throws IOException {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * Not much to say here.\n");
        buf.append("     * @throws IOException \n");
        buf.append("     */\n");
        buf.append("    public void foo() throws IOException {\n");
        buf.append("        goo().substring(2);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public String goo() throws IOException {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * Not much to say here.\n");
        buf.append("     */\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo().substring(2);\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUncaughtException3() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public String goo() throws IOException, ParseException {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * Not much to say here.\n");
        buf.append("     * @throws ParseException Parsing failed\n");
        buf.append("     */\n");
        buf.append("    public void foo() throws ParseException {\n");
        buf.append("        goo().substring(2);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public String goo() throws IOException, ParseException {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * Not much to say here.\n");
        buf.append("     * @throws ParseException Parsing failed\n");
        buf.append("     * @throws IOException \n");
        buf.append("     */\n");
        buf.append("    public void foo() throws ParseException, IOException {\n");
        buf.append("        goo().substring(2);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public String goo() throws IOException, ParseException {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * Not much to say here.\n");
        buf.append("     * @throws ParseException Parsing failed\n");
        buf.append("     */\n");
        buf.append("    public void foo() throws ParseException {\n");
        buf.append("        try {\n");
        buf.append("            goo().substring(2);\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUncaughtException4() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.FileNotFoundException;\n");
        buf.append("import java.io.InterruptedIOException;\n");
        buf.append("public class E {\n");
        buf.append("    public E goo(int i) throws InterruptedIOException {\n");
        buf.append("        return new E();\n");
        buf.append("    }\n");
        buf.append("    public E bar() throws FileNotFoundException {\n");
        buf.append("        return new E();\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * Not much to say here.\n");
        buf.append("     */\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo(1).bar();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.FileNotFoundException;\n");
        buf.append("import java.io.InterruptedIOException;\n");
        buf.append("public class E {\n");
        buf.append("    public E goo(int i) throws InterruptedIOException {\n");
        buf.append("        return new E();\n");
        buf.append("    }\n");
        buf.append("    public E bar() throws FileNotFoundException {\n");
        buf.append("        return new E();\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * Not much to say here.\n");
        buf.append("     * @throws InterruptedIOException \n");
        buf.append("     * @throws FileNotFoundException \n");
        buf.append("     */\n");
        buf.append("    public void foo() throws FileNotFoundException, InterruptedIOException {\n");
        buf.append("        goo(1).bar();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.FileNotFoundException;\n");
        buf.append("import java.io.InterruptedIOException;\n");
        buf.append("public class E {\n");
        buf.append("    public E goo(int i) throws InterruptedIOException {\n");
        buf.append("        return new E();\n");
        buf.append("    }\n");
        buf.append("    public E bar() throws FileNotFoundException {\n");
        buf.append("        return new E();\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * Not much to say here.\n");
        buf.append("     */\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo(1).bar();\n");
        buf.append("        } catch (FileNotFoundException e) {\n");
        buf.append("        } catch (InterruptedIOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUncaughtException5() throws Exception {
        //https://bugs.eclipse.org/bugs/show_bug.cgi?id=31554

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    void foo() {\n");
        buf.append("        try {\n");
        buf.append("            throw new IOException();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("            throw new IOException();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    void foo() throws IOException {\n");
        buf.append("        try {\n");
        buf.append("            throw new IOException();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("            throw new IOException();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    void foo() {\n");
        buf.append("        try {\n");
        buf.append("            throw new IOException();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("            try {\n");
        buf.append("                throw new IOException();\n");
        buf.append("            } catch (IOException e1) {\n");
        buf.append("            }\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    @Ignore
    public void testUncaughtExceptionImportConflict() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test1() {\n");
        buf.append("        test2();\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public void test2() throws de.muenchen.test.Exception {\n");
        buf.append("        throw new de.muenchen.test.Exception();\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public void test3() {\n");
        buf.append("        try {\n");
        buf.append("            java.io.File.createTempFile(\"\", \".tmp\");\n");
        buf.append("        } catch (Exception ex) {\n");
        buf.append("\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package de.muenchen.test;\n");
        buf.append("\n");
        buf.append("public class Exception extends java.lang.Throwable {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test1() {\n");
        buf.append("        try {\n");
        buf.append("            test2();\n");
        buf.append("        } catch (de.muenchen.test.Exception e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public void test2() throws de.muenchen.test.Exception {\n");
        buf.append("        throw new de.muenchen.test.Exception();\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public void test3() {\n");
        buf.append("        try {\n");
        buf.append("            java.io.File.createTempFile(\"\", \".tmp\");\n");
        buf.append("        } catch (Exception ex) {\n");
        buf.append("\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test1() throws de.muenchen.test.Exception {\n");
        buf.append("        test2();\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public void test2() throws de.muenchen.test.Exception {\n");
        buf.append("        throw new de.muenchen.test.Exception();\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public void test3() {\n");
        buf.append("        try {\n");
        buf.append("            java.io.File.createTempFile(\"\", \".tmp\");\n");
        buf.append("        } catch (Exception ex) {\n");
        buf.append("\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUncaughtExceptionExtendedSelection() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public String goo(int i) throws IOException {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        System.out.println(goo(1));\n");
        buf.append("        System.out.println(goo(2));\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        String begin = "goo(1)", end = "goo(2));";

        int offset = buf.indexOf(begin);
        int length = buf.indexOf(end) + end.length() - offset;
        AssistContext context = getCorrectionContext(cu, offset, length, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2, context);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public String goo(int i) throws IOException {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() throws IOException {\n");
        buf.append("        System.out.println(goo(1));\n");
        buf.append("        System.out.println(goo(2));\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public String goo(int i) throws IOException {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            System.out.println(goo(1));\n");
        buf.append("            System.out.println(goo(2));\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUncaughtExceptionRemoveMoreSpecific() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.net.SocketException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException {\n");
        buf.append("        return;\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * @throws SocketException Sockets are dangerous\n");
        buf.append("     * @since 3.0\n");
        buf.append("     */\n");
        buf.append("    public void foo() throws SocketException {\n");
        buf.append("        this.goo();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.net.SocketException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException {\n");
        buf.append("        return;\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * @throws IOException \n");
        buf.append("     * @since 3.0\n");
        buf.append("     */\n");
        buf.append("    public void foo() throws IOException {\n");
        buf.append("        this.goo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.net.SocketException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException {\n");
        buf.append("        return;\n");
        buf.append("    }\n");
        buf.append("    /**\n");
        buf.append("     * @throws SocketException Sockets are dangerous\n");
        buf.append("     * @since 3.0\n");
        buf.append("     */\n");
        buf.append("    public void foo() throws SocketException {\n");
        buf.append("        try {\n");
        buf.append("            this.goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUncaughtExceptionToSurroundingTry() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public static void goo() throws IOException, ParseException {\n");
        buf.append("        return;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            E.goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public static void goo() throws IOException, ParseException {\n");
        buf.append("        return;\n");
        buf.append("    }\n");
        buf.append("    public void foo() throws ParseException {\n");
        buf.append("        try {\n");
        buf.append("            E.goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public static void goo() throws IOException, ParseException {\n");
        buf.append("        return;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            try {\n");
        buf.append("                E.goo();\n");
        buf.append("            } catch (ParseException e) {\n");
        buf.append("            }\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(2);
        String preview3 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public static void goo() throws IOException, ParseException {\n");
        buf.append("        return;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            E.goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        } catch (ParseException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2, preview3}, new String[]{expected1, expected2,
                                                                                               expected3});
    }

    @Test
    public void testUncaughtExceptionOnSuper1() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.FileInputStream;\n");
        buf.append("public class E extends FileInputStream {\n");
        buf.append("    public E() {\n");
        buf.append("        super(\"x\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.FileInputStream;\n");
        buf.append("import java.io.FileNotFoundException;\n");
        buf.append("public class E extends FileInputStream {\n");
        buf.append("    public E() throws FileNotFoundException {\n");
        buf.append("        super(\"x\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUncaughtExceptionOnSuper2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public A() throws Exception {\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends A {\n");
        buf.append("    /**\n");
        buf.append("     * @throws Exception sometimes...\n");
        buf.append("     */\n");
        buf.append("    public E() {\n");
        buf.append("        super();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends A {\n");
        buf.append("    /**\n");
        buf.append("     * @throws Exception sometimes...\n");
        buf.append("     */\n");
        buf.append("    public E() throws Exception {\n");
        buf.append("        super();\n");
        buf.append("    }\n");
        buf.append("}\n");

        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUncaughtExceptionOnSuper3() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A implements Runnable {\n");
        buf.append("    public void run() {\n");
        buf.append("        Class.forName(null);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A implements Runnable {\n");
        buf.append("    public void run() {\n");
        buf.append("        try {\n");
        buf.append("            Class.forName(null);\n");
        buf.append("        } catch (ClassNotFoundException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUncaughtExceptionOnSuper4() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        throw new Exception();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends A {\n");
        buf.append("    public void foo() throws Exception {\n");
        buf.append("        throw new Exception();\n");
        buf.append("    }\n");
        buf.append("}\n");

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            throw new Exception();\n");
        buf.append("        } catch (Exception e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUncaughtExceptionOnThis() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public E() {\n");
        buf.append("        this(null);\n");
        buf.append("    }\n");
        buf.append("    public E(Object x) throws IOException {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public E() throws IOException {\n");
        buf.append("        this(null);\n");
        buf.append("    }\n");
        buf.append("    public E(Object x) throws IOException {\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    boolean BUG_25417 = true;

    @Test
    public void testUncaughtExceptionDuplicate() throws Exception {
        if (BUG_25417) {
            return;
        }

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class MyException extends Exception {\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public void m1() throws IOException {\n");
        buf.append("        m2();\n");
        buf.append("    }\n");
        buf.append("    public void m2() throws IOException, ParseException, MyException {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2); // 2 uncaught exceptions
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public void m1() throws IOException, ParseException, MyException {\n");
        buf.append("        m2();\n");
        buf.append("    }\n");
        buf.append("    public void m2() throws IOException, ParseException, MyException {\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public void m1() throws IOException {\n");
        buf.append("        try {\n");
        buf.append("            m2();\n");
        buf.append("        } catch (ParseException e) {\n");
        buf.append("        } catch (MyException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("    public void m2() throws IOException, ParseException, MyException {\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testMultipleUncaughtExceptions() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException, ParseException {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2); // 2 uncaught exceptions
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException, ParseException {\n");
        buf.append("    }\n");
        buf.append("    public void foo() throws IOException, ParseException {\n");
        buf.append("        goo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException, ParseException {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        } catch (ParseException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUncaughtExceptionInInitializer() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    {\n");
        buf.append("        Class.forName(null);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    {\n");
        buf.append("        try {\n");
        buf.append("            Class.forName(null);\n");
        buf.append("        } catch (ClassNotFoundException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1}, new String[]{expected1});
    }

    @Test
    public void testUnneededCatchBlock() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        } catch (ParseException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() throws IOException {\n");
        buf.append("    }\n");
        buf.append("    public void foo() throws ParseException {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUnneededCatchBlockInInitializer() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    static {\n");
        buf.append("        try {\n");
        buf.append("            int x= 1;\n");
        buf.append("        } catch (ParseException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    static {\n");
        buf.append("        int x= 1;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1}, new String[]{expected1});
    }

    @Test
    public void testUnneededCatchBlockSingle() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() {\n");
        buf.append("    }\n");
        buf.append("    public void foo() throws IOException {\n");
        buf.append("        goo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUnneededCatchBlockBug47221() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public Object foo() {\n");
        buf.append("        try {\n");
        buf.append("            Object o= null;\n");
        buf.append("            return o;\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public Object foo() {\n");
        buf.append("        Object o= null;\n");
        buf.append("        return o;\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public Object foo() throws IOException {\n");
        buf.append("        Object o= null;\n");
        buf.append("        return o;\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUnneededCatchBlockWithFinally() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        } finally {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() {\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } finally {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void goo() {\n");
        buf.append("    }\n");
        buf.append("    public void foo() throws IOException {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } finally {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});

    }

    @Test
    @Ignore
    public void testUnimplementedMethods() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test2;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public interface Inter {\n");
        buf.append("    int getCount(Object[] o) throws IOException;\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import test2.Inter;\n");
        buf.append("public class E implements Inter{\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import test2.Inter;\n");
        buf.append("public abstract class E implements Inter{\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("\n");
        buf.append("import test2.Inter;\n");
        buf.append("public class E implements Inter{\n");
        buf.append("\n");
        buf.append("    public int getCount(Object[] o) throws IOException {\n");
        buf.append("        return 0;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});

    }

    @Test
    @Ignore
    public void testUnimplementedMethods2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test2;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public interface Inter {\n");
        buf.append("    int getCount(Object[] o) throws IOException;\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test2;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public abstract class InterImpl implements Inter {\n");
        buf.append("    protected abstract int[] getMusic() throws IOException;\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import test2.InterImpl;\n");
        buf.append("public class E extends InterImpl {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import test2.InterImpl;\n");
        buf.append("public abstract class E extends InterImpl {\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("\n");
        buf.append("import test2.InterImpl;\n");
        buf.append("public class E extends InterImpl {\n");
        buf.append("\n");
        buf.append("    public int getCount(Object[] o) throws IOException {\n");
        buf.append("        return 0;\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    @Override\n");
        buf.append("    protected int[] getMusic() throws IOException {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    @Ignore
    public void testUnimplementedMethodsExtendingGenericType1() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Collection;\n");
        buf.append("public interface Inter<T> {\n");
        buf.append("    T doT(Collection<T> in);\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E implements Inter<String> {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public abstract class E implements Inter<String> {\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.util.Collection;\n");
        buf.append("\n");
        buf.append("public class E implements Inter<String> {\n");
        buf.append("\n");
        buf.append("    public String doT(Collection<String> in) {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    @Ignore
    public void testUnimplementedMethodsExtendingGenericType2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public interface Inter<T> {\n");
        buf.append("    T doT(T in);\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E implements Inter<String> {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public abstract class E implements Inter<String> {\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E implements Inter<String> {\n");
        buf.append("\n");
        buf.append("    public String doT(String in) {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    @Ignore
    public void testUnimplementedMethodsWithTypeParameters() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Collection;\n");
        buf.append("public interface Inter {\n");
        buf.append("    <T> T doX(Collection<T> in);\n");
        buf.append("    <T extends Exception> T getException();\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E implements Inter {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public abstract class E implements Inter {\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.util.Collection;\n");
        buf.append("\n");
        buf.append("public class E implements Inter {\n");
        buf.append("\n");
        buf.append("    public <T> T doX(Collection<T> in) {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public <T extends Exception> T getException() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    @Ignore
    public void testUnimplementedMethodsWithTypeParameters2() throws Exception {
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=330241

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public interface Inter {\n");
        buf.append("    <T> List<T> findElements(Class<T> clazz, List<String> tagsToMatch);\n");
        buf.append("    <T> List<T> findPerspectiveElements(Class<T> clazz, List<String> tagsToMatch);\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E implements Inter {\n");
        buf.append("    public <T> List<T> findPerspectiveElements(Class<T> clazz, List<String> tagsToMatch) {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public abstract class E implements Inter {\n");
        buf.append("    public <T> List<T> findPerspectiveElements(Class<T> clazz, List<String> tagsToMatch) {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E implements Inter {\n");
        buf.append("    public <T> List<T> findPerspectiveElements(Class<T> clazz, List<String> tagsToMatch) {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public <T> List<T> findElements(Class<T> clazz, List<String> tagsToMatch) {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUnimplementedMethodsInEnum() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("enum TestEnum implements IA {\n");
        buf.append("    test1,test2;\n");
        buf.append("}\n");
        buf.append("interface IA {\n");
        buf.append("    void foo();\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "TestEnum");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("enum TestEnum implements IA {\n");
        buf.append("    test1,test2;\n");
        buf.append("\n");
        buf.append("    @Override\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        buf.append("interface IA {\n");
        buf.append("    void foo();\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnimplementedMethodsInEnumConstant1() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("enum TestEnum {\n");
        buf.append("    A {\n");
        buf.append("        @Override\n");
        buf.append("        public boolean foo() {\n");
        buf.append("            return false;\n");
        buf.append("        }\n");
        buf.append("    };\n");
        buf.append("    public abstract boolean foo();\n");
        buf.append("    public abstract void bar();\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("enum TestEnum {\n");
        buf.append("    A {\n");
        buf.append("        @Override\n");
        buf.append("        public boolean foo() {\n");
        buf.append("            return false;\n");
        buf.append("        }\n");
        buf.append("\n");
        buf.append("        @Override\n");
        buf.append("        public void bar() {\n");
        buf.append("        }\n");
        buf.append("    };\n");
        buf.append("    public abstract boolean foo();\n");
        buf.append("    public abstract void bar();\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testUnimplementedMethodsInEnumConstant2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("enum TestEnum {\n");
        buf.append("    A {\n");
        buf.append("    };\n");
        buf.append("    public abstract boolean foo();\n");
        buf.append("    public abstract void bar();\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("enum TestEnum {\n");
        buf.append("    A {\n");
        buf.append("\n");
        buf.append("        @Override\n");
        buf.append("        public boolean foo() {\n");
        buf.append("            return false;\n");
        buf.append("        }\n");
        buf.append("\n");
        buf.append("        @Override\n");
        buf.append("        public void bar() {\n");
        buf.append("        }\n");
        buf.append("    };\n");
        buf.append("    public abstract boolean foo();\n");
        buf.append("    public abstract void bar();\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnimplementedMethodsInEnumConstant3() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("enum TestEnum implements Runnable {\n");
        buf.append("    A;\n");
        buf.append("    public abstract boolean foo();\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("enum TestEnum implements Runnable {\n");
        buf.append("    A {\n");
        buf.append("        @Override\n");
        buf.append("        public void run() {\n");
        buf.append("        }\n");
        buf.append("        @Override\n");
        buf.append("        public boolean foo() {\n");
        buf.append("            return false;\n");
        buf.append("        }\n");
        buf.append("    };\n");
        buf.append("    public abstract boolean foo();\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnitializedVariable() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int s;\n");
        buf.append("        try {\n");
        buf.append("            s= 1;\n");
        buf.append("        } catch (Exception e) {\n");
        buf.append("            System.out.println(s);\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int s = 0;\n");
        buf.append("        try {\n");
        buf.append("            s= 1;\n");
        buf.append("        } catch (Exception e) {\n");
        buf.append("            System.out.println(s);\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUndefinedConstructorInDefaultConstructor1() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F {\n");
        buf.append("    public F(Runnable runnable) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F {\n");
        buf.append("\n");
        buf.append("    public E(Runnable runnable) {\n");
        buf.append("        super(runnable);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUndefinedConstructorInDefaultConstructor2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class F {\n");
        buf.append("    public F(Runnable runnable) throws IOException {\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public F(int i, Runnable runnable) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F {\n");
        buf.append("\n");
        buf.append("    public E(int i, Runnable runnable) {\n");
        buf.append("        super(i, runnable);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.io.IOException;\n");
        buf.append("\n");
        buf.append("public class E extends F {\n");
        buf.append("\n");
        buf.append("    public E(Runnable runnable) throws IOException {\n");
        buf.append("        super(runnable);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    @Ignore
    public void testUndefinedConstructorWithGenericSuperClass() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F<T extends Runnable> {\n");
        buf.append("    public F(Runnable runnable) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F<Runnable> {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F<Runnable> {\n");
        buf.append("\n");
        buf.append("    public E(Runnable runnable) {\n");
        buf.append("        super(runnable);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUndefinedConstructorWithEnclosing1() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F {\n");
        buf.append("    public class SubF {\n");
        buf.append("        public SubF(int i) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public class SubE extends F.SubF {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public class SubE extends F.SubF {\n");
        buf.append("\n");
        buf.append("        public SubE(F f, int i) {\n");
        buf.append("            f.super(i);\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUndefinedConstructorWithEnclosing2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F {\n");
        buf.append("    public static class SubF {\n");
        buf.append("        public SubF(int i) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public class SubE extends F.SubF {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public class SubE extends F.SubF {\n");
        buf.append("\n");
        buf.append("        public SubE(int i) {\n");
        buf.append("            super(i);\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUndefinedConstructorWithEnclosingInGeneric() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F<S> {\n");
        buf.append("    public class SubF <T>{\n");
        buf.append("        public SubF(S s, T t) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F<String>.SubF<String> {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F<String>.SubF<String> {\n");
        buf.append("\n");
        buf.append("    public E(F<String> f, String s, String t) {\n");
        buf.append("        f.super(s, t);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUndefinedConstructorWithEnclosing3() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F {\n");
        buf.append("    public static class SubF {\n");
        buf.append("        public SubF(int i) {\n");
        buf.append("        }\n");
        buf.append("        public class SubF2 extends SubF {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "F");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F {\n");
        buf.append("    public static class SubF {\n");
        buf.append("        public SubF(int i) {\n");
        buf.append("        }\n");
        buf.append("        public class SubF2 extends SubF {\n");
        buf.append("\n");
        buf.append("            public SubF2(int i) {\n");
        buf.append("                super(i);\n");
        buf.append("            }\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testNotVisibleConstructorInDefaultConstructor() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F {\n");
        buf.append("    private F() {\n");
        buf.append("    }\n");
        buf.append("    public F(Runnable runnable) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F {\n");
        buf.append("\n");
        buf.append("    public E(Runnable runnable) {\n");
        buf.append("        super(runnable);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUnhandledExceptionInDefaultConstructor() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class F {\n");
        buf.append("    public F() throws IOException{\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E extends F {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.io.IOException;\n");
        buf.append("\n");
        buf.append("public class E extends F {\n");
        buf.append("\n");
        buf.append("    public E() throws IOException {\n");
        buf.append("        super();\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUnusedPrivateField() throws Exception {
        HashMap<String, String> hashtable = JavaCore.getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("\n");
        buf.append("    public int getCount() {\n");
        buf.append("        return count;\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public void setCount(int count) {\n");
        buf.append("        this.count = count;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testUnusedPrivateField1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count, color= count;\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count, color= count;\n");
        buf.append("\n");
        buf.append("    public int getColor() {\n");
        buf.append("        return color;\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public void setColor(int color) {\n");
        buf.append("        this.color = color;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testUnusedPrivateField2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count= 0;\n");
        buf.append("    public void foo() {\n");
        buf.append("        count= 1 + 2;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count= 0;\n");
        buf.append("    public void foo() {\n");
        buf.append("        setCount(1 + 2);\n");
        buf.append("    }\n");
        buf.append("    public int getCount() {\n");
        buf.append("        return count;\n");
        buf.append("    }\n");
        buf.append("    public void setCount(int count) {\n");
        buf.append("        this.count = count;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testUnusedPrivateField3() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private E e= new E();\n");
        buf.append("    private int value;\n");
        buf.append("    public void foo() {\n");
        buf.append("        value= 0;\n");
        buf.append("        this.value= 0;\n");
        buf.append("        e.value= 0;\n");
        buf.append("        this.e.value= 0;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private E e= new E();\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private E e= new E();\n");
        buf.append("    private int value;\n");
        buf.append("    public void foo() {\n");
        buf.append("        setValue(0);\n");
        buf.append("        this.setValue(0);\n");
        buf.append("        e.setValue(0);\n");
        buf.append("        this.e.setValue(0);\n");
        buf.append("    }\n");
        buf.append("    public int getValue() {\n");
        buf.append("        return value;\n");
        buf.append("    }\n");
        buf.append("    public void setValue(int value) {\n");
        buf.append("        this.value = value;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testUnusedPrivateFieldBug328481() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("    void foo(){;\n");
        buf.append("        count++;\n");
        buf.append("        count--;\n");
        buf.append("        --count;\n");
        buf.append("        ++count;\n");
        buf.append("        for ( ; ; count++) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    void foo(){;\n");
        buf.append("        for ( ; ;) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("    void foo(){;\n");
        buf.append("        setCount(getCount() + 1);\n");
        buf.append("        setCount(getCount() - 1);\n");
        buf.append("        setCount(getCount() - 1);\n");
        buf.append("        setCount(getCount() + 1);\n");
        buf.append("        for ( ; ; count++) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("    public int getCount() {\n");
        buf.append("        return count;\n");
        buf.append("    }\n");
        buf.append("    public void setCount(int count) {\n");
        buf.append("        this.count = count;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnusedVariable() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        boolean res= process();\n");
        buf.append("        res= (super.hashCode() == 1);\n");
        buf.append("    }\n");
        buf.append("    public boolean process() {\n");
        buf.append("        return true;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String expected[] = new String[2];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        process();\n");
        buf.append("        (super.hashCode() == 1);\n");
        buf.append("    }\n");
        buf.append("    public boolean process() {\n");
        buf.append("        return true;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("    public boolean process() {\n");
        buf.append("        return true;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnusedVariable1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private Object[] foo() {\n");
        buf.append("        Object[] i, j= new Object[0];\n");
        buf.append("        i= j = null;\n");
        buf.append("        i= (new Object[] { null, null });\n");
        buf.append("        return j;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private Object[] foo() {\n");
        buf.append("        Object[] j= new Object[0];\n");
        buf.append("        j = null;\n");
        buf.append("        return j;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private Object[] foo() {\n");
        buf.append("        Object[] j= new Object[0];\n");
        buf.append("        j = null;\n");
        buf.append("        return j;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();
        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnusedVariable2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private void foo() {\n");
        buf.append("        for (int j= 0, i= 0; i < 3; i++) {\n");
        buf.append("             j= i;\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private void foo() {\n");
        buf.append("        for (int i= 0; i < 3; i++) {\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private void foo() {\n");
        buf.append("        for (int i= 0; i < 3; i++) {\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnusedVariable4() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE, JavaCore.DISABLED);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @param i\n");
        buf.append("     */\n");
        buf.append("    private void foo(int i) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     */\n");
        buf.append("    private void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUnusedVariable5() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    private final String c=\"Test\";\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "B");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[2];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    private final String c=\"Test\";\n");
        buf.append("\n");
        buf.append("    public String getC() {\n");
        buf.append("        return c;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);

    }

    @Test
    @Ignore
    public void testUnusedVariable6() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    private String c=String.valueOf(true),d=\"test\";\n");
        buf.append("    String f=d;\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "B");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[2];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    private String d=\"test\";\n");
        buf.append("    String f=d;\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    private String c=String.valueOf(true),d=\"test\";\n");
        buf.append("    String f=d;\n");
        buf.append("    public String getC() {\n");
        buf.append("        return c;\n");
        buf.append("    }\n");
        buf.append("    public void setC(String c) {\n");
        buf.append("        this.c = c;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnusedVariable7() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    void test(){\n");
        buf.append("        String c=\"Test\",d=String.valueOf(true),e=c;\n");
        buf.append("        e+=\"\";\n");
        buf.append("        d=\"blubb\";\n");
        buf.append("        d=String.valueOf(12);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "B");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[2];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    void test(){\n");
        buf.append("        String c=\"Test\";\n");
        buf.append("        String.valueOf(true);\n");
        buf.append("        String e=c;\n");
        buf.append("        e+=\"\";\n");
        buf.append("        String.valueOf(12);\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    void test(){\n");
        buf.append("        String c=\"Test\",e=c;\n");
        buf.append("        e+=\"\";\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnusedVariable8() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        E x = (E) bar();\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    private Object bar() {\n");
        buf.append("        throw null;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[2];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        bar();\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    private Object bar() {\n");
        buf.append("        throw null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    private Object bar() {\n");
        buf.append("        throw null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnusedVariableAsSwitchStatement() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    void test(int i){\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 3:\n");
        buf.append("                String c=\"Test\",d=String.valueOf(true),e=c;\n");
        buf.append("                e+=\"\";\n");
        buf.append("                d=\"blubb\";\n");
        buf.append("                d=String.valueOf(12);\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "B");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[2];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    void test(int i){\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 3:\n");
        buf.append("                String c=\"Test\";\n");
        buf.append("                String.valueOf(true);\n");
        buf.append("                String e=c;\n");
        buf.append("                e+=\"\";\n");
        buf.append("                String.valueOf(12);\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B {\n");
        buf.append("    void test(int i){\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 3:\n");
        buf.append("                String c=\"Test\",e=c;\n");
        buf.append("                e+=\"\";\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnusedParam() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private void foo(Object str) {\n");
        buf.append("        {\n");
        buf.append("            str= toString();\n");
        buf.append("            str= new String[] { toString(), toString() };\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private void foo() {\n");
        buf.append("        {\n");
        buf.append("            toString();\n");
        buf.append("            new String[] { toString(), toString() };\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @param str  \n");
        buf.append("     */\n");
        buf.append("    private void foo(Object str) {\n");
        buf.append("        {\n");
        buf.append("            str= toString();\n");
        buf.append("            str= new String[] { toString(), toString() };\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnusedParam2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @see E\n");
        buf.append("     */\n");
        buf.append("    private void foo(Object str) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @see E\n");
        buf.append("     */\n");
        buf.append("    private void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @param str \n");
        buf.append("     * @see E\n");
        buf.append("     */\n");
        buf.append("    private void foo(Object str) {\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnusedPrivateMethod() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public int fCount;\n");
        buf.append(" \n");
        buf.append("    private void foo() {\n");
        buf.append("        fCount= 1;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public int fCount;\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnusedPrivateConstructor() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public E() {\n");
        buf.append("    }\n");
        buf.append(" \n");
        buf.append("    private E(int i) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public E() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnusedPrivateType() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private class F {\n");
        buf.append("    }\n");
        buf.append(" \n");
        buf.append("    public E() {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public E() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnnecessaryCast1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_TYPE_CHECK, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        int s = (int) i;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        int s = i;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnnecessaryCast2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_TYPE_CHECK, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(String s) {\n");
        buf.append("        String r = ((String) s);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(String s) {\n");
        buf.append("        String r = s;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnnecessaryCast3() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_TYPE_CHECK, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        int s = ((int) 1 + 2) * 3;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        int s = (1 + 2) * 3;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnnecessaryCastBug335173_1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_TYPE_CHECK, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Integer n) {\n");
        buf.append("        int i = (((Integer) n)).intValue();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Integer n) {\n");
        buf.append("        int i = (n).intValue();\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnnecessaryCastBug335173_2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_TYPE_CHECK, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Integer n) {\n");
        buf.append("        int i = ((Integer) (n)).intValue();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Integer n) {\n");
        buf.append("        int i = (n).intValue();\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testSuperfluousSemicolon() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_EMPTY_STATEMENT, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        int s= 1;;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        int s= 1;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testIndirectStaticAccess1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_INDIRECT_STATIC_ACCESS, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package other;\n");
        buf.append("public class A {\n");
        buf.append("    public static final int CONST=1;\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B extends other.A {\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import pack.B;\n");
        buf.append("public class E {\n");
        buf.append("    public int foo(B b) {\n");
        buf.append("        return B.CONST;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import other.A;\n");
        buf.append("import pack.B;\n");
        buf.append("public class E {\n");
        buf.append("    public int foo(B b) {\n");
        buf.append("        return A.CONST;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testIndirectStaticAccess2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_INDIRECT_STATIC_ACCESS, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package other;\n");
        buf.append("public class A {\n");
        buf.append("    public static int foo() {\n");
        buf.append("        return 1;\n");
        buf.append("    }\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class B extends other.A {\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public int foo() {\n");
        buf.append("        return pack.B.foo();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import other.A;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public int foo() {\n");
        buf.append("        return A.foo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testIndirectStaticAccess_bug307407() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_INDIRECT_STATIC_ACCESS, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private final String localString = new MyClass().getMyString();\n");
        buf.append("    public static class MyClass {\n");
        buf.append("        public static String getMyString() {\n");
        buf.append("            return \"a\";\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private final String localString = MyClass.getMyString();\n");
        buf.append("    public static class MyClass {\n");
        buf.append("        public static String getMyString() {\n");
        buf.append("            return \"a\";\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private final String localString = new MyClass().getMyString();\n");
        buf.append("    public static class MyClass {\n");
        buf.append("        public String getMyString() {\n");
        buf.append("            return \"a\";\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUnnecessaryInstanceof1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_TYPE_CHECK, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(String b) {\n");
        buf.append("        return (b instanceof String);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(String b) {\n");
        buf.append("        return (b != null);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnnecessaryInstanceof2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_TYPE_CHECK, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(String b) {\n");
        buf.append("        if  (b instanceof String && b.getClass() != null) {\n");
        buf.append("            System.out.println();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(String b) {\n");
        buf.append("        if  (b != null && b.getClass() != null) {\n");
        buf.append("            System.out.println();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnnecessaryThrownException1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(String b) throws IOException {\n");
        buf.append("        if  (b != null) {\n");
        buf.append("            System.out.println();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(String b) {\n");
        buf.append("        if  (b != null) {\n");
        buf.append("            System.out.println();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @throws IOException  \n");
        buf.append("     */\n");
        buf.append("    public void foo(String b) throws IOException {\n");
        buf.append("        if  (b != null) {\n");
        buf.append("            System.out.println();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnnecessaryThrownException2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @throws IOException\n");
        buf.append("     */\n");
        buf.append("    public E(int i) throws IOException, ParseException {\n");
        buf.append("        if  (i == 0) {\n");
        buf.append("            throw new IOException();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @throws IOException\n");
        buf.append("     */\n");
        buf.append("    public E(int i) throws IOException {\n");
        buf.append("        if  (i == 0) {\n");
        buf.append("            throw new IOException();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @throws IOException\n");
        buf.append("     * @throws ParseException \n");
        buf.append("     */\n");
        buf.append("    public E(int i) throws IOException, ParseException {\n");
        buf.append("        if  (i == 0) {\n");
        buf.append("            throw new IOException();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnnecessaryThrownException3() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_INCLUDE_DOC_COMMENT_REFERENCE,
                      JavaCore.DISABLED);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @param i\n");
        buf.append("     * @throws IOException\n");
        buf.append("     * @throws ParseException\n");
        buf.append("     */\n");
        buf.append("    public void foo(int i) throws IOException, ParseException {\n");
        buf.append("        if  (i == 0) {\n");
        buf.append("            throw new IOException();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("import java.text.ParseException;\n");
        buf.append("public class E {\n");
        buf.append("    /**\n");
        buf.append("     * @param i\n");
        buf.append("     * @throws IOException\n");
        buf.append("     */\n");
        buf.append("    public void foo(int i) throws IOException {\n");
        buf.append("        if  (i == 0) {\n");
        buf.append("            throw new IOException();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnqualifiedFieldAccess1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNQUALIFIED_FIELD_ACCESS, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("    public E(int i) {\n");
        buf.append("        count= i;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        String[] expecteds = new String[1];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("    public E(int i) {\n");
        buf.append("        this.count= i;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expecteds[0] = buf.toString();
        assertExpectedExistInProposals(proposals, expecteds);
    }

    @Test
    @Ignore
    public void testUnqualifiedFieldAccess2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNQUALIFIED_FIELD_ACCESS, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F {\n");
        buf.append("    public int count;\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private F f= new F();\n");
        buf.append("    public E(int i) {\n");
        buf.append("        f.count= i;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expecteds = new String[1];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private F f= new F();\n");
        buf.append("    public E(int i) {\n");
        buf.append("        this.f.count= i;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expecteds[0] = buf.toString();
        assertExpectedExistInProposals(proposals, expecteds);
    }

    @Test
    @Ignore
    public void testUnqualifiedFieldAccess3() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNQUALIFIED_FIELD_ACCESS, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F {\n");
        buf.append("    public void setCount(int i) {}\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private F f= new F();\n");
        buf.append("    public E(int i) {\n");
        buf.append("        f.setCount(i);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expecteds = new String[1];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private F f= new F();\n");
        buf.append("    public E(int i) {\n");
        buf.append("        this.f.setCount(i);\n");
        buf.append("    }\n");
        buf.append("}\n");
        expecteds[0] = buf.toString();
        assertExpectedExistInProposals(proposals, expecteds);
    }

    @Test
    public void testUnqualifiedFieldAccess4() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNQUALIFIED_FIELD_ACCESS, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("    public E(int i) {\n");
        buf.append("        class Inner {\n");
        buf.append("            public void foo() {\n");
        buf.append("               count= 1;\n");
        buf.append("            }\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        String[] expecteds = new String[1];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("    public E(int i) {\n");
        buf.append("        class Inner {\n");
        buf.append("            public void foo() {\n");
        buf.append("               E.this.count= 1;\n");
        buf.append("            }\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expecteds[0] = buf.toString();
        assertExpectedExistInProposals(proposals, expecteds);
    }

    @Test
    @Ignore
    public void testUnqualifiedFieldAccessWithGenerics() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNQUALIFIED_FIELD_ACCESS, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class F<T> {\n");
        buf.append("    protected T someObject;\n");
        buf.append("}\n");

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E<T> extends F<String> {\n");
        buf.append("    public void foo() {\n");
        buf.append("        class X {\n");
        buf.append("            public String toString() {\n");
        buf.append("                return someObject.getClass().getName();\n");
        buf.append("            }\n");
        buf.append("         };\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expecteds = new String[1];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E<T> extends F<String> {\n");
        buf.append("    public void foo() {\n");
        buf.append("        class X {\n");
        buf.append("            public String toString() {\n");
        buf.append("                return E.this.someObject.getClass().getName();\n");
        buf.append("            }\n");
        buf.append("         };\n");
        buf.append("    }\n");
        buf.append("}\n");
        expecteds[0] = buf.toString();
        assertExpectedExistInProposals(proposals, expecteds);
    }

    @Test
    @Ignore
    public void testHidingVariable1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("    public void foo() {\n");
        buf.append("       int count= 1;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);
        //      assertTrue(proposals.get(0) instanceof LinkedNamesAssistProposal);
    }

    @Test
    @Ignore
    public void testHidingVariable2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("    public void foo(int count) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);
        //      assertTrue(proposals.get(0) instanceof LinkedNamesAssistProposal);
    }

    @Test
    @Ignore
    public void testHidingVariable3() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int count) {\n");
        buf.append("        class Inner {\n");
        buf.append("            private int count;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);
        //      assertTrue(proposals.get(0) instanceof LinkedNamesAssistProposal);
    }

    @Test
    @Ignore
    public void testHidingVariable4() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int count;\n");
        buf.append("    public void foo() {\n");
        buf.append("        class Inner {\n");
        buf.append("            private int count;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);
        //      assertTrue(proposals.get(0) instanceof LinkedNamesAssistProposal);
    }

    @Test
    @Ignore
    public void testHidingVariable5() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int count) {\n");
        buf.append("        class Inner {\n");
        buf.append("            public void foo() {\n");
        buf.append("                 int count;\n");
        buf.append("            }\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);
        //      assertTrue(proposals.get(0) instanceof LinkedNamesAssistProposal);
    }

    @Test
    @Ignore
    public void testHidingVariable6() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int count) {\n");
        buf.append("        class Inner {\n");
        buf.append("            public void foo(int count) {\n");
        buf.append("            }\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);
        //      assertTrue(proposals.get(0) instanceof LinkedNamesAssistProposal);
    }

    @Test
    public void testSetParenteses1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Object x) {\n");
        buf.append("        if (!x instanceof Runnable) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Object x) {\n");
        buf.append("        if (!(x instanceof Runnable)) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    @Test
    public void testSetParenteses2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JavaCore.ERROR);
        hashtable.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(int x) {\n");
        buf.append("        return !x instanceof Runnable || true;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(int x) {\n");
        buf.append("        return !(x instanceof Runnable) || true;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    @Test
    public void testUnnecessaryElse1() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_ELSE, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(int x) {\n");
        buf.append("        if (x == 9) {\n");
        buf.append("            return true;\n");
        buf.append("        } else {\n");
        buf.append("            return false;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(int x) {\n");
        buf.append("        if (x == 9) {\n");
        buf.append("            return true;\n");
        buf.append("        }\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    @Test
    public void testUnnecessaryElse2() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_ELSE, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(int x) {\n");
        buf.append("        if (x == 9) {\n");
        buf.append("            return true;\n");
        buf.append("        } else {\n");
        buf.append("            x= 9;\n");
        buf.append("            return false;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(int x) {\n");
        buf.append("        if (x == 9) {\n");
        buf.append("            return true;\n");
        buf.append("        }\n");
        buf.append("        x= 9;\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    @Test
    public void testUnnecessaryElse3() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_ELSE, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(int x) {\n");
        buf.append("        if (x == 9) {\n");
        buf.append("            return true;\n");
        buf.append("        } else\n");
        buf.append("            return false;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(int x) {\n");
        buf.append("        if (x == 9) {\n");
        buf.append("            return true;\n");
        buf.append("        }\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    @Test
    public void testUnnecessaryElse4() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_ELSE, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    boolean foo(int i) {\n");
        buf.append("        if (i < 100)\n");
        buf.append("            if (i == 42)\n");
        buf.append("                return true;\n");
        buf.append("            else\n");
        buf.append("                i = i + 3;\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    boolean foo(int i) {\n");
        buf.append("        if (i < 100) {\n");
        buf.append("            if (i == 42)\n");
        buf.append("                return true;\n");
        buf.append("            i = i + 3;\n");
        buf.append("        }\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testUnnecessaryElse5() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_ELSE, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    boolean foo(int i) {\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 42:\n");
        buf.append("                if (foo(i+1))\n");
        buf.append("                    return true;\n");
        buf.append("                else\n");
        buf.append("                    i = i + 3;\n");
        buf.append("        }\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    boolean foo(int i) {\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 42:\n");
        buf.append("                if (foo(i+1))\n");
        buf.append("                    return true;\n");
        buf.append("                i = i + 3;\n");
        buf.append("        }\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testInterfaceExtendsClass() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_ELSE, JavaCore.ERROR);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E extends List {\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E implements List {\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.List;\n");
        buf.append("public interface E extends List {\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveUnreachableCodeStmt() throws Exception {
        HashMap<String, String> hashtable = WorkerMessageHandler.get().getOptions();
        hashtable.put(JavaCore.COMPILER_PB_UNNECESSARY_ELSE, JavaCore.IGNORE);
        WorkerMessageHandler.get().getOptions().putAll(hashtable);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(int x) {\n");
        buf.append("        if (x == 9) {\n");
        buf.append("            return true;\n");
        buf.append("        } else\n");
        buf.append("            return false;\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(int x) {\n");
        buf.append("        if (x == 9) {\n");
        buf.append("            return true;\n");
        buf.append("        } else\n");
        buf.append("            return false;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    @Test
    public void testRemoveUnreachableCodeStmt2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public String getName() {\n");
        buf.append("        try{\n");
        buf.append("            return \"fred\";\n");
        buf.append("        }\n");
        buf.append("        catch (Exception e){\n");
        buf.append("            return e.getLocalizedMessage();\n");
        buf.append("        }\n");
        buf.append("        System.err.print(\"wow\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public String getName() {\n");
        buf.append("        try{\n");
        buf.append("            return \"fred\";\n");
        buf.append("        }\n");
        buf.append("        catch (Exception e){\n");
        buf.append("            return e.getLocalizedMessage();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testRemoveUnreachableCodeWhile() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo() {\n");
        buf.append("        while (false) {\n");
        buf.append("            return true;\n");
        buf.append("        }\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo() {\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testRemoveDeadCodeIfThen() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false) {\n");
        buf.append("            System.out.println(\"a\");\n");
        buf.append("        } else {\n");
        buf.append("            System.out.println(\"b\");\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        System.out.println(\"b\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false) {\n");
        buf.append("            System.out.println(\"a\");\n");
        buf.append("        } else {\n");
        buf.append("            System.out.println(\"b\");\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveDeadCodeIfThen2() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) {\n");
        buf.append("            if (o == null) {\n");
        buf.append("            	System.out.println(\"hello\");\n");
        buf.append("        	} else {\n");
        buf.append("            	System.out.println(\"bye\");\n");
        buf.append("        	}\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) {\n");
        buf.append("            System.out.println(\"bye\");\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) {\n");
        buf.append("            if (o == null) {\n");
        buf.append("            	System.out.println(\"hello\");\n");
        buf.append("        	} else {\n");
        buf.append("            	System.out.println(\"bye\");\n");
        buf.append("        	}\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveDeadCodeIfThen3() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) \n");
        buf.append("            if (o == null) {\n");
        buf.append("            	System.out.println(\"hello\");\n");
        buf.append("        	} else {\n");
        buf.append("            	System.out.println(\"bye\");\n");
        buf.append("            	System.out.println(\"bye-bye\");\n");
        buf.append("        	}\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) {\n");
        buf.append("        	System.out.println(\"bye\");\n");
        buf.append("        	System.out.println(\"bye-bye\");\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) \n");
        buf.append("            if (o == null) {\n");
        buf.append("            	System.out.println(\"hello\");\n");
        buf.append("        	} else {\n");
        buf.append("            	System.out.println(\"bye\");\n");
        buf.append("            	System.out.println(\"bye-bye\");\n");
        buf.append("        	}\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveDeadCodeIfThen4() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) \n");
        buf.append("            if (true) \n");
        buf.append("            	if (o == null) \n");
        buf.append("            		System.out.println(\"hello\");\n");
        buf.append("		System.out.println(\"bye\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) \n");
        buf.append("            if (true) {\n");
        buf.append("            }\n");
        buf.append("		System.out.println(\"bye\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) \n");
        buf.append("            if (true) \n");
        buf.append("            	if (o == null) \n");
        buf.append("            		System.out.println(\"hello\");\n");
        buf.append("		System.out.println(\"bye\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveDeadCodeIfThen5() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) \n");
        buf.append("            if (false) \n");
        buf.append("            	if (o == null) \n");
        buf.append("            		System.out.println(\"hello\");\n");
        buf.append("		System.out.println(\"bye\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) {\n");
        buf.append("        }\n");
        buf.append("		System.out.println(\"bye\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = new Object();\n");
        buf.append("        if (o != null) \n");
        buf.append("            if (false) \n");
        buf.append("            	if (o == null) \n");
        buf.append("            		System.out.println(\"hello\");\n");
        buf.append("		System.out.println(\"bye\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveDeadCodeIfThenSwitch() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        switch (1) {\n");
        buf.append("            case 1:\n");
        buf.append("                if (false) {\n");
        buf.append("                	foo();\n");
        buf.append("					System.out.println(\"hi\");\n");
        buf.append("				} else {\n");
        buf.append("                	System.out.println(\"bye\");\n");
        buf.append("				}\n");
        buf.append("                break;\n");
        buf.append("            case 2:\n");
        buf.append("                foo();\n");
        buf.append("                break;\n");
        buf.append("            default:\n");
        buf.append("                break;\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        switch (1) {\n");
        buf.append("            case 1:\n");
        buf.append("                System.out.println(\"bye\");\n");
        buf.append("                break;\n");
        buf.append("            case 2:\n");
        buf.append("                foo();\n");
        buf.append("                break;\n");
        buf.append("            default:\n");
        buf.append("                break;\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public void foo() {\n");
        buf.append("        switch (1) {\n");
        buf.append("            case 1:\n");
        buf.append("                if (false) {\n");
        buf.append("                	foo();\n");
        buf.append("					System.out.println(\"hi\");\n");
        buf.append("				} else {\n");
        buf.append("                	System.out.println(\"bye\");\n");
        buf.append("				}\n");
        buf.append("                break;\n");
        buf.append("            case 2:\n");
        buf.append("                foo();\n");
        buf.append("                break;\n");
        buf.append("            default:\n");
        buf.append("                break;\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveDeadCodeIfElse() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (Math.random() == -1 || true) {\n");
        buf.append("            System.out.println(\"a\");\n");
        buf.append("        } else {\n");
        buf.append("            System.out.println(\"b\");\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        System.out.println(\"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (Math.random() == -1 || true) {\n");
        buf.append("            System.out.println(\"a\");\n");
        buf.append("        } else {\n");
        buf.append("            System.out.println(\"b\");\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveDeadCodeAfterIf() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo() {\n");
        buf.append("        if (true) return false;\n");
        buf.append("        return true;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo() {\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public boolean foo() {\n");
        buf.append("        if (true) return false;\n");
        buf.append("        return true;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveDeadCodeConditional() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public int foo() {\n");
        buf.append("        return true ? 1 : 0;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public int foo() {\n");
        buf.append("        return 1;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public int foo() {\n");
        buf.append("        return true ? 1 : 0;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveDeadCodeConditional2() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = true ? new Integer(1) + 2 : new Double(0.0) + 3;\n");
        buf.append("        System.out.println(o);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = (double) (new Integer(1) + 2);\n");
        buf.append("        System.out.println(o);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String[] expected = new String[]{buf.toString()};

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testRemoveDeadCodeConditional3() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = true ? new Integer(1) : new Double(0.0);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object o = (double) new Integer(1);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String[] expected = new String[]{buf.toString()};

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testRemoveDeadCodeMultiStatements() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            return;\n");
        buf.append("        foo();\n");
        buf.append("        foo();\n");
        buf.append("        foo();\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        return;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            return;\n");
        buf.append("        foo();\n");
        buf.append("        foo();\n");
        buf.append("        foo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveUnreachableCodeMultiStatementsSwitch() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_DEAD_CODE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        switch (1) {\n");
        buf.append("            case 1:\n");
        buf.append("                foo();\n");
        buf.append("                break;\n");
        buf.append("                foo();\n");
        buf.append("                new Object();\n");
        buf.append("            case 2:\n");
        buf.append("                foo();\n");
        buf.append("                break;\n");
        buf.append("            default:\n");
        buf.append("                break;\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);
        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        switch (1) {\n");
        buf.append("            case 1:\n");
        buf.append("                foo();\n");
        buf.append("                break;\n");
        buf.append("            case 2:\n");
        buf.append("                foo();\n");
        buf.append("                break;\n");
        buf.append("            default:\n");
        buf.append("                break;\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testUnusedObjectAllocation1() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_UNUSED_OBJECT_ALLOCATION, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public Object foo() {\n");
        buf.append("        if (Boolean.TRUE) {\n");
        buf.append("            /*a*/new Object()/*b*/;/*c*/\n");
        buf.append("        }\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 5);

        String[] expected = new String[5];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public Object foo() {\n");
        buf.append("        if (Boolean.TRUE) {\n");
        buf.append("            /*a*/return new Object()/*b*/;/*c*/\n");
        buf.append("        }\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public Object foo() {\n");
        buf.append("        if (Boolean.TRUE) {\n");
        buf.append("        }\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public Object foo() {\n");
        buf.append("        if (Boolean.TRUE) {\n");
        buf.append("            /*a*/new Object()/*b*/;/*c*/\n");
        buf.append("        }\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[2] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public Object foo() {\n");
        buf.append("        if (Boolean.TRUE) {\n");
        buf.append("            /*a*/Object object = new Object()/*b*/;/*c*/\n");
        buf.append("        }\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[3] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    private Object object;\n");
        buf.append("\n");
        buf.append("    public Object foo() {\n");
        buf.append("        if (Boolean.TRUE) {\n");
        buf.append("            /*a*/object = new Object()/*b*/;/*c*/\n");
        buf.append("        }\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[4] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testUnusedObjectAllocation2() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_UNUSED_OBJECT_ALLOCATION, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        /*a*/new Exception()/*b*/;/*c*/\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 6);

        String[] expected = new String[6];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        /*a*/throw new Exception()/*b*/;/*c*/\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"unused\")\n");
        buf.append("    public void foo() {\n");
        buf.append("        /*a*/new Exception()/*b*/;/*c*/\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[2] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        /*a*/return new Exception()/*b*/;/*c*/\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[3] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        /*a*/Exception exception = new Exception()/*b*/;/*c*/\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[4] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    private Exception exception;\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        /*a*/exception = new Exception()/*b*/;/*c*/\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[5] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testAssignmentWithoutSideEffect1() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    int count;\n");
        buf.append("    public void foo(int count) {\n");
        buf.append("        count= count;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[2];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    int count;\n");
        buf.append("    public void foo(int count) {\n");
        buf.append("        this.count= count;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    int count;\n");
        buf.append("    public void foo(int count) {\n");
        buf.append("        count= this.count;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testAssignmentWithoutSideEffect2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    static int count;\n");
        buf.append("    public void foo(int count) {\n");
        buf.append("        count= count;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[2];
        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    static int count;\n");
        buf.append("    public void foo(int count) {\n");
        buf.append("        E.count= count;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    static int count;\n");
        buf.append("    public void foo(int count) {\n");
        buf.append("        count= E.count;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testAssignmentWithoutSideEffect3() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    int bar;\n");
        buf.append("    public void foo() {\n");
        buf.append("        this.bar= bar;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    int bar;\n");
        buf.append("    public void foo(int bar) {\n");
        buf.append("        this.bar= bar;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testAddTypeParametersToClassInstanceCreationTest01() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        List<E> l= new ArrayList(); \n");
        buf.append("    }\n");
        buf.append("}\n");

        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_UNCHECKED_TYPE_OPERATION, JavaCore.WARNING);
        WorkerMessageHandler.get().getOptions().putAll(options);

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 4);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        List<E> l= new ArrayList<E>(); \n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testAddTypeParametersToClassInstanceCreationTest02() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.HashSet;\n");
        buf.append("import java.util.Hashtable;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        List<List<Hashtable<Integer, HashSet<E>>>> l= new ArrayList(); \n");
        buf.append("    }\n");
        buf.append("}\n");

        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_UNCHECKED_TYPE_OPERATION, JavaCore.WARNING);
        WorkerMessageHandler.get().getOptions().putAll(options);

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 4);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.HashSet;\n");
        buf.append("import java.util.Hashtable;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        List<List<Hashtable<Integer, HashSet<E>>>> l= new ArrayList<List<Hashtable<Integer, HashSet<E>>>>(); \n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testMissingAnnotationAttributes1() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public @interface Annot {\n");
        buf.append("        public int foo();\n");
        buf.append("    }\n");
        buf.append("    @Annot()\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public @interface Annot {\n");
        buf.append("        public int foo();\n");
        buf.append("    }\n");
        buf.append("    @Annot(foo = 0)\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testMissingAnnotationAttributes2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public @interface Other {\n");
        buf.append("    }\n");
        buf.append("    public @interface Annot {\n");
        buf.append("        public Other[] foo();\n");
        buf.append("        public String hoo();\n");
        buf.append("    }\n");
        buf.append("    @Annot()\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public @interface Other {\n");
        buf.append("    }\n");
        buf.append("    public @interface Annot {\n");
        buf.append("        public Other[] foo();\n");
        buf.append("        public String hoo();\n");
        buf.append("    }\n");
        buf.append("    @Annot(foo = {@Other}, hoo = \"\")\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testMissingAnnotationAttributes3() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public @interface Annot {\n");
        buf.append("        public int foo();\n");
        buf.append("        public String hoo() default \"hello\";\n");
        buf.append("    }\n");
        buf.append("    @Annot()\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 1);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public @interface Annot {\n");
        buf.append("        public int foo();\n");
        buf.append("        public String hoo() default \"hello\";\n");
        buf.append("    }\n");
        buf.append("    @Annot(foo = 0)\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testMissingAnnotationAttributes_bug179316() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package e;\n");
        buf.append("@Requires1\n");
        buf.append("@interface Requires1 {\n");
        buf.append("        String value();\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package e;\n");
        buf.append("@Requires1(value = \"\")\n");
        buf.append("@interface Requires1 {\n");
        buf.append("        String value();\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testTypeParametersToRawTypeReference01() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_RAW_TYPE_REFERENCE, JavaCore.WARNING);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void test() {\n");
        buf.append("        List l;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 1);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 4);

        String[] expected = new String[3];

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void test() {\n");
        buf.append("        @SuppressWarnings(\"rawtypes\")\n");
        buf.append("        List l;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"rawtypes\")\n");
        buf.append("    public void test() {\n");
        buf.append("        List l;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    public void test() {\n");
        buf.append("        List<?> l;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[2] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testTypeParametersToRawTypeReference02() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_RAW_TYPE_REFERENCE, JavaCore.WARNING);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    private class E1<P1, P2> {}\n");
        buf.append("    public void test() {\n");
        buf.append("        E1 e1;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 1);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 4);

        String[] expected = new String[3];

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    private class E1<P1, P2> {}\n");
        buf.append("    public void test() {\n");
        buf.append("        @SuppressWarnings(\"rawtypes\")\n");
        buf.append("        E1 e1;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    private class E1<P1, P2> {}\n");
        buf.append("    @SuppressWarnings(\"rawtypes\")\n");
        buf.append("    public void test() {\n");
        buf.append("        E1 e1;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    private class E1<P1, P2> {}\n");
        buf.append("    public void test() {\n");
        buf.append("        E1<?, ?> e1;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[2] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testTypeParametersToRawTypeReference06() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_RAW_TYPE_REFERENCE, JavaCore.WARNING);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    private List l= new ArrayList<String>();\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 1);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 4);

        String[] expected = new String[3];

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"rawtypes\")\n");
        buf.append("    private List l= new ArrayList<String>();\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    private List<String> l= new ArrayList<String>();\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    private ArrayList<String> l= new ArrayList<String>();\n");
        buf.append("}\n");
        expected[2] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testTypeParametersToRawTypeReference07() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_RAW_TYPE_REFERENCE, JavaCore.WARNING);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    private List l;\n");
        buf.append("    private void foo() {\n");
        buf.append("        l.add(\"String\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 1);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 3);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"rawtypes\")\n");
        buf.append("    private List l;\n");
        buf.append("    private void foo() {\n");
        buf.append("        l.add(\"String\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    private List<String> l;\n");
        buf.append("    private void foo() {\n");
        buf.append("        l.add(\"String\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testTypeParametersToRawTypeReference08() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_UNCHECKED_TYPE_OPERATION, JavaCore.WARNING);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    private class E1<T> {\n");
        buf.append("        public void foo(T t) {\n");
        buf.append("            return;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("    private void foo(E1 e1) {\n");
        buf.append("        e1.foo(\"\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 1);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 3);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    private class E1<T> {\n");
        buf.append("        public void foo(T t) {\n");
        buf.append("            return;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("    @SuppressWarnings(\"unchecked\")\n");
        buf.append("    private void foo(E1 e1) {\n");
        buf.append("        e1.foo(\"\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    private class E1<T> {\n");
        buf.append("        public void foo(T t) {\n");
        buf.append("            return;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("    private void foo(E1<String> e1) {\n");
        buf.append("        e1.foo(\"\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testTypeParametersToRawTypeReference09() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_RAW_TYPE_REFERENCE, JavaCore.WARNING);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    private List<String> l= new ArrayList();\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 1);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 3);

        String[] expected = new String[3];

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"rawtypes\")\n");
        buf.append("    private List<String> l= new ArrayList();\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("import java.util.ArrayList;\n");
        buf.append("import java.util.List;\n");
        buf.append("public class E {\n");
        buf.append("    private List<String> l= new ArrayList<String>();\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testSwitchCaseFallThrough1() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_FALLTHROUGH_CASE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("            case 2:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 3);

        String[] expected = new String[3];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("                break;\n");
        buf.append("            case 2:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"fallthrough\")\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("            case 2:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("                //$FALL-THROUGH$\n");
        buf.append("            case 2:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[2] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testSwitchCaseFallThrough2() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_FALLTHROUGH_CASE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("            default:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 3);

        String[] expected = new String[3];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("                break;\n");
        buf.append("            default:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"fallthrough\")\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("            default:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("                //$FALL-THROUGH$\n");
        buf.append("            default:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[2] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testSwitchCaseFallThrough3() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_FALLTHROUGH_CASE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("                // fall through is OK\n");
        buf.append("            default:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 3);

        String[] expected = new String[3];
        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("                break;\n");
        buf.append("            // fall through is OK\n");
        buf.append("            default:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    @SuppressWarnings(\"fallthrough\")\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("                // fall through is OK\n");
        buf.append("            default:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public long foo(int i) {\n");
        buf.append("        long time= 0;\n");
        buf.append("        switch (i) {\n");
        buf.append("            case 1:\n");
        buf.append("                time= System.currentTimeMillis();\n");
        buf.append("                // fall through is OK\n");
        buf.append("                //$FALL-THROUGH$\n");
        buf.append("            default:\n");
        buf.append("                time= 3;\n");
        buf.append("        }\n");
        buf.append("        return time;\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[2] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testCollectionsFieldMethodReplacement() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_UNCHECKED_TYPE_OPERATION, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_RAW_TYPE_REFERENCE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_TYPE_PARAMETER_HIDING, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);

        StringBuffer buf = new StringBuffer();
        buf.append("package b112441;\n");
        buf.append("\n");
        buf.append("import java.util.Collections;\n");
        buf.append("import java.util.Map;\n");
        buf.append("\n");
        buf.append("public class CollectionsTest {\n");
        buf.append("    Map<String,String> m=Collections.EMPTY_MAP;\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "CollectionsTest");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package b112441;\n");
        buf.append("\n");
        buf.append("import java.util.Collections;\n");
        buf.append("import java.util.Map;\n");
        buf.append("\n");
        buf.append("public class CollectionsTest {\n");
        buf.append("    Map<String,String> m=Collections.emptyMap();\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testCollectionsFieldMethodReplacement2() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_UNCHECKED_TYPE_OPERATION, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_RAW_TYPE_REFERENCE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_TYPE_PARAMETER_HIDING, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("import java.util.Collections;\n");
        buf.append("import java.util.Map;\n");
        buf.append("\n");
        buf.append("public class CollectionsTest {\n");
        buf.append("    public void foo(Map<Object, Integer> map) { };\n");
        buf.append("    {\n");
        buf.append("        foo(Collections.EMPTY_MAP);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "CollectionsTest");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("import java.util.Collections;\n");
        buf.append("import java.util.Map;\n");
        buf.append("\n");
        buf.append("public class CollectionsTest {\n");
        buf.append("    public void foo(Map<Object, Integer> map) { };\n");
        buf.append("    {\n");
        buf.append("        foo(Collections.<Object, Integer> emptyMap());\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    @Ignore
    public void testCollectionsFieldMethodReplacement3() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_UNCHECKED_TYPE_OPERATION, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_RAW_TYPE_REFERENCE, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_TYPE_PARAMETER_HIDING, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.ENABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("import java.util.*;\n");
        buf.append("\n");
        buf.append("public class CollectionsTest {\n");
        buf.append("    public void foo(Map<Date, Integer> map) { };\n");
        buf.append("    {\n");
        buf.append("        foo(Collections.EMPTY_MAP);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "CollectionsTest");
        ArrayList<?> proposals = collectCorrections(cu, astRoot);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("import java.util.*;\n");
        buf.append("\n");
        buf.append("public class CollectionsTest {\n");
        buf.append("    public void foo(Map<Date, Integer> map) { };\n");
        buf.append("    {\n");
        buf.append("        foo(Collections.<Date, Integer> emptyMap());\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testMissingEnumConstantsInCase1() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_INCOMPLETE_ENUM_SWITCH, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.DISABLED);

        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    enum MyEnum {\n");
        buf.append("        X1, X2, X3\n");
        buf.append("    }\n");
        buf.append("    \n");
        buf.append("    public void foo(MyEnum x) {\n");
        buf.append("        switch (x) {\n");
        buf.append("        \n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 3);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    enum MyEnum {\n");
        buf.append("        X1, X2, X3\n");
        buf.append("    }\n");
        buf.append("    \n");
        buf.append("    public void foo(MyEnum x) {\n");
        buf.append("        switch (x) {\n");
        buf.append("            case X1 :\n");
        buf.append("                break;\n");
        buf.append("            case X2 :\n");
        buf.append("                break;\n");
        buf.append("            case X3 :\n");
        buf.append("                break;\n");
        buf.append("        \n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testMissingEnumConstantsInCase2() throws Exception {
        HashMap<String, String> options = WorkerMessageHandler.get().getOptions();
        options.put(JavaCore.COMPILER_PB_INCOMPLETE_ENUM_SWITCH, JavaCore.WARNING);
        options.put(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS, JavaCore.DISABLED);
        WorkerMessageHandler.get().getOptions().putAll(options);

        StringBuffer buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    enum MyEnum {\n");
        buf.append("        X1, X2, X3\n");
        buf.append("    }\n");
        buf.append("    \n");
        buf.append("    public void foo(MyEnum x) {\n");
        buf.append("        switch (x) {\n");
        buf.append("            case X1 :\n");
        buf.append("                break;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 2);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 1);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    enum MyEnum {\n");
        buf.append("        X1, X2, X3\n");
        buf.append("    }\n");
        buf.append("    \n");
        buf.append("    public void foo(MyEnum x) {\n");
        buf.append("        switch (x) {\n");
        buf.append("            case X1 :\n");
        buf.append("                break;\n");
        buf.append("            case X2 :\n");
        buf.append("                break;\n");
        buf.append("            case X3 :\n");
        buf.append("                break;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testMissingHashCode1() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    private int fField;\n");
        buf.append("\n");
        buf.append("    public boolean equals(Object o) {\n");
        buf.append("        return true;\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        ArrayList<?> proposals = collectCorrections(cu, astRoot, 1);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

        String[] expected = new String[1];
        buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    private int fField;\n");
        buf.append("\n");
        buf.append("    public boolean equals(Object o) {\n");
        buf.append("        return true;\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    @Override\n");
        buf.append("    public int hashCode() {\n");
        buf.append("        return super.hashCode();\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testMissingHashCode2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package p;\n");
        buf.append("\n");
        buf.append("public class E extends java.io.File{\n");
        buf.append("    private static final long serialVersionUID= 1L;\n");
        buf.append("    public E() { super(\"x\"); }\n");
        buf.append("    public boolean equals(Object o) {\n");
        buf.append("        return o instanceof E && super.equals(o);\n");
        buf.append("    }\n");
        buf.append("}\n");

        Document cu = new DocumentImpl(buf.toString());
        CompilationUnit astRoot = getASTRoot(cu, "E");
        Assert.assertEquals(0, astRoot.getProblems().length); // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=38751#c7
    }

}
