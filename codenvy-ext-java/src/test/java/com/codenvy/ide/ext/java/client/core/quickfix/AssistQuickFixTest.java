/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids <sdavids@gmx.de> - testInvertEquals1-23
 *******************************************************************************/
package com.codenvy.ide.ext.java.client.core.quickfix;

import com.codenvy.ide.ext.java.client.JavaExtension;
import com.codenvy.ide.ext.java.client.editor.JavaCorrectionAssistant;
import com.codenvy.ide.ext.java.client.editor.JavaCorrectionProcessor;
import com.codenvy.ide.ext.java.emul.FileSystem;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.formatter.DefaultCodeFormatterConstants;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.StubUtility;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.AssistContext;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.QuickAssistProcessorImpl;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.AssignToVariableAssistProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.CUCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.templates.CodeTemplateContextType;
import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.codenvy.ide.text.DocumentImpl;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssistQuickFixTest extends QuickFixTest {

    private static final String CHANGE_MODIFIER_TO_FINAL = "Change modifier to final";

    @Before
    public void setUp() throws Exception {
        HashMap<String, String> options = TestOptions.getDefaultOptions();
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
        new JavaCorrectionProcessor(new JavaCorrectionAssistant(null, null, null), null, null);
        new JavaExtension();
        new WorkerMessageHandler(null);
        GwtReflectionUtils.setPrivateFieldValue(WorkerMessageHandler.get(), "nameEnvironment", new FileSystem(
                new String[]{System.getProperty("java.home") + "/lib/rt.jar"}, null, "UTF-8"));
        WorkerMessageHandler.get().getOptions().putAll(options);

        //		IPreferenceStore store= JavaPlugin.getDefault().getPreferenceStore();
        //		store.setValue(PreferenceConstants.CODEGEN_ADD_COMMENTS, false);
        //		store.setValue(PreferenceConstants.CODEGEN_KEYWORD_THIS, false);

        StubUtility.setCodeTemplate(CodeTemplateContextType.METHODSTUB_ID, "//TODO\n${body_statement}");

        //		Preferences corePrefs= JavaPlugin.getJavaCorePluginPreferences();
        //		corePrefs.setValue(JavaCore.CODEASSIST_FIELD_PREFIXES, "");
        //		corePrefs.setValue(JavaCore.CODEASSIST_STATIC_FIELD_PREFIXES, "");
        //		corePrefs.setValue(JavaCore.CODEASSIST_FIELD_SUFFIXES, "");
        //		corePrefs.setValue(JavaCore.CODEASSIST_STATIC_FIELD_SUFFIXES, "");
        //
        //		fJProject1= ProjectTestSetup.getProject();

        //		fSourceFolder= JavaProjectHelper.addSourceContainer(fJProject1, "src");
    }

    //
    //	protected void tearDown() throws Exception {
    //		JavaProjectHelper.clear(fJProject1, ProjectTestSetup.getDefaultClasspath());
    //	}
    @Test
    public void testAssignToLocal() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        getClass();\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("getClass()");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private Class<? extends E> class1;\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        class1 = getClass();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Class<? extends E> class1 = getClass();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});

    }

    @Test
    public void testAssignToLocal2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Vector;\n");
        buf.append("public class E {\n");
        buf.append("    public Vector goo() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo().iterator();\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("goo().iterator()");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Iterator;\n");
        buf.append("import java.util.Vector;\n");
        buf.append("public class E {\n");
        buf.append("    private Iterator iterator;\n");
        buf.append("    public Vector goo() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        iterator = goo().iterator();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Iterator;\n");
        buf.append("import java.util.Vector;\n");
        buf.append("public class E {\n");
        buf.append("    public Vector goo() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        Iterator iterator = goo().iterator();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    //TODO
    //   public void testAssignToLocal3() throws Exception
    //   {
    //      // test prefixes and this qualification
    //
    //      IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();
    //      store.setValue(PreferenceConstants.CODEGEN_KEYWORD_THIS, true);
    //      Preferences corePrefs = JavaPlugin.getJavaCorePluginPreferences();
    //      corePrefs.setValue(JavaCore.CODEASSIST_FIELD_PREFIXES, "f");
    //      corePrefs.setValue(JavaCore.CODEASSIST_LOCAL_PREFIXES, "_");
    //
    //      IPackageFragment pack1 = fSourceFolder.createPackageFragment("test1", false, null);
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("\n");
    //      buf.append("    private int fCount;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo() {\n");
    //      buf.append("        System.getSecurityManager();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      ICompilationUnit cu = pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //      int offset = buf.toString().indexOf("System");
    //      AssistContext context = getCorrectionContext(cu, offset, 0);
    //      List proposals = collectAssists(context, false);
    //
    //      assertNumberOfProposals(proposals, 2);
    //      assertCorrectLabels(proposals);
    //
    //      CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
    //      String preview1 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("\n");
    //      buf.append("    private int fCount;\n");
    //      buf.append("    private SecurityManager fSecurityManager;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo() {\n");
    //      buf.append("        this.fSecurityManager = System.getSecurityManager();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected1 = buf.toString();
    //
    //      proposal = (CUCorrectionProposal)proposals.get(1);
    //      String preview2 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("\n");
    //      buf.append("    private int fCount;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo() {\n");
    //      buf.append("        SecurityManager _securityManager = System.getSecurityManager();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected2 = buf.toString();
    //
    //      assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    //   }

    @Test
    public void testAssignToLocal4() throws Exception {
        // test name conflict

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("\n");
        buf.append("    private int f;\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        Math.min(1.0f, 2.0f);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("Math");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("\n");
        buf.append("    private int f;\n");
        buf.append("    private float min;\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        min = Math.min(1.0f, 2.0f);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("\n");
        buf.append("    private int f;\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        float min = Math.min(1.0f, 2.0f);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    //TODO
    //   public void testAssignToLocal5() throws Exception
    //   {
    //      // test prefixes and this qualification on static method
    //
    //      IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();
    //      store.setValue(PreferenceConstants.CODEGEN_KEYWORD_THIS, true);
    //      Preferences corePrefs = JavaPlugin.getJavaCorePluginPreferences();
    //      corePrefs.setValue(JavaCore.CODEASSIST_FIELD_PREFIXES, "f");
    //      corePrefs.setValue(JavaCore.CODEASSIST_STATIC_FIELD_PREFIXES, "fg");
    //      corePrefs.setValue(JavaCore.CODEASSIST_LOCAL_PREFIXES, "_");
    //
    //      IPackageFragment pack1 = fSourceFolder.createPackageFragment("test1", false, null);
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("\n");
    //      buf.append("    private int fCount;\n");
    //      buf.append("\n");
    //      buf.append("    public static void foo() {\n");
    //      buf.append("        System.getSecurityManager();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      ICompilationUnit cu = pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //      int offset = buf.toString().indexOf("System");
    //      AssistContext context = getCorrectionContext(cu, offset, 0);
    //      List proposals = collectAssists(context, false);
    //
    //      assertNumberOfProposals(proposals, 2);
    //      assertCorrectLabels(proposals);
    //
    //      CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
    //      String preview1 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("\n");
    //      buf.append("    private int fCount;\n");
    //      buf.append("    private static SecurityManager fgSecurityManager;\n");
    //      buf.append("\n");
    //      buf.append("    public static void foo() {\n");
    //      buf.append("        E.fgSecurityManager = System.getSecurityManager();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected1 = buf.toString();
    //
    //      proposal = (CUCorrectionProposal)proposals.get(1);
    //      String preview2 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("\n");
    //      buf.append("    private int fCount;\n");
    //      buf.append("\n");
    //      buf.append("    public static void foo() {\n");
    //      buf.append("        SecurityManager _securityManager = System.getSecurityManager();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected2 = buf.toString();
    //
    //      assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    //   }

    @Test
    public void testAssignToLocal6() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    static {\n");
        buf.append("        getClass(); // comment\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("getClass()");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private static Class<? extends E> class1;\n");
        buf.append("\n");
        buf.append("    static {\n");
        buf.append("        class1 = getClass(); // comment\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    static {\n");
        buf.append("        Class<? extends E> class1 = getClass(); // comment\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testAssignToLocal7() throws Exception {
        // test name conflict: name used later

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Vector;\n");
        buf.append("public class E {\n");
        buf.append("    public Vector goo() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo().iterator();\n");
        buf.append("        Object iterator= null;\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("goo().iterator()");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Iterator;\n");
        buf.append("import java.util.Vector;\n");
        buf.append("public class E {\n");
        buf.append("    private Iterator iterator2;\n");
        buf.append("    public Vector goo() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        iterator2 = goo().iterator();\n");
        buf.append("        Object iterator= null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Iterator;\n");
        buf.append("import java.util.Vector;\n");
        buf.append("public class E {\n");
        buf.append("    public Vector goo() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        Iterator iterator2 = goo().iterator();\n");
        buf.append("        Object iterator= null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    @Ignore
    public void testAssignToLocal8() throws Exception {
        // assign to local of field access

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public class MyLayout {\n");
        buf.append("        int indent;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        new MyLayout().indent;\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("new MyLayout().indent;");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 0, "E");
        List proposals = collectAssists(context, false);

        int numberOfProposals = 5;
        assertNumberOfProposals(proposals, numberOfProposals);
        assertCorrectLabels(proposals);

        ArrayList previews = new ArrayList();
        ArrayList expecteds = new ArrayList();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public class MyLayout {\n");
        buf.append("        int indent;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        int indent = new MyLayout().indent;\n");
        buf.append("    }\n");
        buf.append("}\n");
        addPreviewAndExpected(proposals, buf, expecteds, previews);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int indent;\n");
        buf.append("    public class MyLayout {\n");
        buf.append("        int indent;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        indent = new MyLayout().indent;\n");
        buf.append("    }\n");
        buf.append("}\n");
        addPreviewAndExpected(proposals, buf, expecteds, previews);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public class MyLayout {\n");
        buf.append("        int indent;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        MyLayout myLayout = new MyLayout();\n");
        buf.append("        myLayout.indent;\n");
        buf.append("    }\n");
        buf.append("}\n");
        addPreviewAndExpected(proposals, buf, expecteds, previews);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public class MyLayout {\n");
        buf.append("        int indent;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        MyLayout myLayout = new MyLayout();\n");
        buf.append("        myLayout.indent;\n");
        buf.append("    }\n");
        buf.append("}\n");
        addPreviewAndExpected(proposals, buf, expecteds, previews);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private static final MyLayout MY_LAYOUT = new MyLayout();\n");
        buf.append("    public class MyLayout {\n");
        buf.append("        int indent;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        MY_LAYOUT.indent;\n");
        buf.append("    }\n");
        buf.append("}\n");
        addPreviewAndExpected(proposals, buf, expecteds, previews);

        assertEqualStringsIgnoreOrder(previews, expecteds);
    }

    @Test
    public void testAssignToLocal9() throws Exception {
        // assign to local of field access

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int[] fField;\n");
        buf.append("    public void foo() {\n");
        buf.append("        fField[0];\n");
        buf.append("    }\n");
        buf.append("}\n");

        String string = "fField[0];";
        int offset = buf.toString().indexOf(string);
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, string.length(), "E");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        String[] expected = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int[] fField;\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i = fField[0];\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private int[] fField;\n");
        buf.append("    private int i;\n");
        buf.append("    public void foo() {\n");
        buf.append("        i = fField[0];\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }

    @Test
    public void testAssignToLocal10() throws Exception {
        // assign to local with recovered statement

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        System.getProperties()\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("System.getProperties()");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.util.Properties;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Properties properties = System.getProperties();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.util.Properties;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    private Properties properties;\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        properties = System.getProperties();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testAssignToLocal11() throws Exception {
        // assign to statement in if body with no brackets

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        if (i == 0)\n");
        buf.append("            System.getProperties();\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("System.getProperties()");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.util.Properties;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        if (i == 0) {\n");
        buf.append("            Properties properties = System.getProperties();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.util.Properties;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    private Properties properties;\n");
        buf.append("\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        if (i == 0)\n");
        buf.append("            properties = System.getProperties();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testAssignToLocal12() throws Exception {
        // assign to recovered statement in if body with no brackets

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        if (i == 0)\n");
        buf.append("           i++\n");
        buf.append("        else\n");
        buf.append("            System.getProperties()\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("System.getProperties()");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.util.Properties;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        if (i == 0)\n");
        buf.append("           i++\n");
        buf.append("        else {\n");
        buf.append("            Properties properties = System.getProperties();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.util.Properties;\n");
        buf.append("\n");
        buf.append("public class E {\n");
        buf.append("    private Properties properties;\n");
        buf.append("\n");
        buf.append("    public void foo(int i) {\n");
        buf.append("        if (i == 0)\n");
        buf.append("           i++\n");
        buf.append("        else\n");
        buf.append("            properties = System.getProperties();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testAssignToLocal13() throws Exception {
        // assign to local in context that requires fully qualified type, https://bugs.eclipse.org/bugs/show_bug.cgi?id=239735

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class Timer {\n");
        buf.append("    public static void main(String[] args) {\n");
        buf.append("        new java.util.Timer();\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("new java.util.Timer()");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        String[] expecteds = new String[2];

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class Timer {\n");
        buf.append("    public static void main(String[] args) {\n");
        buf.append("        java.util.Timer timer = new java.util.Timer();\n");
        buf.append("    }\n");
        buf.append("}\n");
        expecteds[0] = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class Timer {\n");
        buf.append("    private static java.util.Timer timer;\n");
        buf.append("\n");
        buf.append("    public static void main(String[] args) {\n");
        buf.append("        timer = new java.util.Timer();\n");
        buf.append("    }\n");
        buf.append("}\n");
        expecteds[1] = buf.toString();

        assertExpectedExistInProposals(proposals, expecteds);
    }

    @Test
    public void testAssignParamToField() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public  E(int count) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        String selection = "count";
        int offset = buf.toString().indexOf(selection);
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, selection.length(), "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private final int count;\n");
        buf.append("\n");
        buf.append("    public  E(int count) {\n");
        buf.append("        this.count = count;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String ex1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{ex1});
    }

    //TODO
    //   public void testAssignParamToField2() throws Exception
    //   {
    //      Preferences corePrefs = JavaPlugin.getJavaCorePluginPreferences();
    //      corePrefs.setValue(JavaCore.CODEASSIST_FIELD_PREFIXES, "f");
    //
    //      IPackageFragment pack1 = fSourceFolder.createPackageFragment("test1", false, null);
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import java.util.Vector;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    public  E(int count, Vector vec[]) {\n");
    //      buf.append("        super();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      ICompilationUnit cu = pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //      String selection = "vec";
    //      int offset = buf.toString().indexOf(selection);
    //      AssistContext context = getCorrectionContext(cu, offset, selection.length());
    //      List proposals = collectAssists(context, false);
    //
    //      assertNumberOfProposals(proposals, 1);
    //      assertCorrectLabels(proposals);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import java.util.Vector;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private final Vector[] fVec;\n");
    //      buf.append("\n");
    //      buf.append("    public  E(int count, Vector vec[]) {\n");
    //      buf.append("        super();\n");
    //      buf.append("        fVec = vec;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String ex1 = buf.toString();
    //
    //      assertExpectedExistInProposals(proposals, new String[]{ex1});
    //   }
    //
    //   public void testAssignParamToField3() throws Exception
    //   {
    //      Preferences corePrefs = JavaPlugin.getJavaCorePluginPreferences();
    //      corePrefs.setValue(JavaCore.CODEASSIST_STATIC_FIELD_PREFIXES, "fg");
    //
    //      IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();
    //      store.setValue(PreferenceConstants.CODEGEN_KEYWORD_THIS, true);
    //
    //      IPackageFragment pack1 = fSourceFolder.createPackageFragment("test1", false, null);
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import java.util.Vector;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private int fgVec;\n");
    //      buf.append("\n");
    //      buf.append("    public static void foo(int count, Vector vec[]) {\n");
    //      buf.append("        count++;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      ICompilationUnit cu = pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //      String selection = "vec";
    //      int offset = buf.toString().indexOf(selection);
    //      AssistContext context = getCorrectionContext(cu, offset, selection.length());
    //      List proposals = collectAssists(context, false);
    //
    //      assertNumberOfProposals(proposals, 1);
    //      assertCorrectLabels(proposals);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import java.util.Vector;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private int fgVec;\n");
    //      buf.append("    private static Vector[] fgVec2;\n");
    //      buf.append("\n");
    //      buf.append("    public static void foo(int count, Vector vec[]) {\n");
    //      buf.append("        E.fgVec2 = vec;\n");
    //      buf.append("        count++;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String ex1 = buf.toString();
    //
    //      assertExpectedExistInProposals(proposals, new String[]{ex1});
    //   }

    //   public void testAssignParamToField4() throws Exception
    //   {
    //      IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();
    //      store.setValue(PreferenceConstants.CODEGEN_KEYWORD_THIS, true);
    //
    //      IPackageFragment pack1 = fSourceFolder.createPackageFragment("test1", false, null);
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private long count;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo(int count) {\n");
    //      buf.append("        count++;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      ICompilationUnit cu = pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //      int offset = buf.toString().indexOf("int count");
    //      AssistContext context = getCorrectionContext(cu, offset, 0);
    //      List proposals = collectAssists(context, false);
    //
    //      assertNumberOfProposals(proposals, 2);
    //      assertCorrectLabels(proposals);
    //
    //      CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
    //      String preview1 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private long count;\n");
    //      buf.append("    private int count2;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo(int count) {\n");
    //      buf.append("        this.count2 = count;\n");
    //      buf.append("        count++;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected1 = buf.toString();
    //
    //      proposal = (CUCorrectionProposal)proposals.get(1);
    //      String preview2 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private long count;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo(int count) {\n");
    //      buf.append("        this.count = count;\n");
    //      buf.append("        count++;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected2 = buf.toString();
    //
    //      assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    //   }
    //
    //   public void testAssignParamToField5() throws Exception
    //   {
    //      IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();
    //      store.setValue(PreferenceConstants.CODEGEN_KEYWORD_THIS, true);
    //
    //      IPackageFragment pack1 = fSourceFolder.createPackageFragment("test1", false, null);
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private int p1;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo(int p1, int p2) {\n");
    //      buf.append("        this.p1 = p1;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      ICompilationUnit cu = pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //      int offset = buf.toString().indexOf("int p2");
    //      AssistContext context = getCorrectionContext(cu, offset, 0);
    //      List proposals = collectAssists(context, false);
    //
    //      assertNumberOfProposals(proposals, 2);
    //      assertCorrectLabels(proposals);
    //
    //      CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
    //      String preview1 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private int p1;\n");
    //      buf.append("    private int p2;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo(int p1, int p2) {\n");
    //      buf.append("        this.p1 = p1;\n");
    //      buf.append("        this.p2 = p2;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected1 = buf.toString();
    //
    //      proposal = (CUCorrectionProposal)proposals.get(1);
    //      String preview2 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private int p1;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo(int p1, int p2) {\n");
    //      buf.append("        this.p1 = p1;\n");
    //      buf.append("        this.p1 = p2;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected2 = buf.toString();
    //
    //      assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    //   }
    //
    //   public void testAssignParamToField6() throws Exception
    //   {
    //      IPreferenceStore store = JavaPlugin.getDefault().getPreferenceStore();
    //      store.setValue(PreferenceConstants.CODEGEN_KEYWORD_THIS, true);
    //
    //      IPackageFragment pack1 = fSourceFolder.createPackageFragment("test1", false, null);
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private Float p1;\n");
    //      buf.append("    private Number p2;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo(Float p1, Integer p2) {\n");
    //      buf.append("        this.p1 = p1;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      ICompilationUnit cu = pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //      int offset = buf.toString().indexOf("Integer p2");
    //      AssistContext context = getCorrectionContext(cu, offset, 0);
    //      List proposals = collectAssists(context, false);
    //
    //      assertNumberOfProposals(proposals, 2);
    //      assertCorrectLabels(proposals);
    //
    //      CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
    //      String preview1 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private Float p1;\n");
    //      buf.append("    private Number p2;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo(Float p1, Integer p2) {\n");
    //      buf.append("        this.p1 = p1;\n");
    //      buf.append("        this.p2 = p2;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected1 = buf.toString();
    //
    //      proposal = (CUCorrectionProposal)proposals.get(1);
    //      String preview2 = getPreviewContent(proposal);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private Float p1;\n");
    //      buf.append("    private Number p2;\n");
    //      buf.append("    private Integer p22;\n");
    //      buf.append("\n");
    //      buf.append("    public void foo(Float p1, Integer p2) {\n");
    //      buf.append("        this.p1 = p1;\n");
    //      buf.append("        this.p22 = p2;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String expected2 = buf.toString();
    //
    //      assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    //   }
    //
    //   public void testAssignParamToFieldInGeneric() throws Exception
    //   {
    //      Preferences corePrefs = JavaPlugin.getJavaCorePluginPreferences();
    //      corePrefs.setValue(JavaCore.CODEASSIST_FIELD_PREFIXES, "f");
    //
    //      IPackageFragment pack1 = fSourceFolder.createPackageFragment("test1", false, null);
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import java.util.Vector;\n");
    //      buf.append("public class E<T> {\n");
    //      buf.append("    public  E(int count, Vector<String>[] vec) {\n");
    //      buf.append("        super();\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      ICompilationUnit cu = pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //      String selection = "vec";
    //      int offset = buf.toString().indexOf(selection);
    //      AssistContext context = getCorrectionContext(cu, offset, selection.length());
    //      List proposals = collectAssists(context, false);
    //
    //      assertNumberOfProposals(proposals, 1);
    //      assertCorrectLabels(proposals);
    //
    //      buf = new StringBuffer();
    //      buf.append("package test1;\n");
    //      buf.append("import java.util.Vector;\n");
    //      buf.append("public class E<T> {\n");
    //      buf.append("    private final Vector<String>[] fVec;\n");
    //      buf.append("\n");
    //      buf.append("    public  E(int count, Vector<String>[] vec) {\n");
    //      buf.append("        super();\n");
    //      buf.append("        fVec = vec;\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      String ex1 = buf.toString();
    //
    //      assertExpectedExistInProposals(proposals, new String[]{ex1});
    //   }

    @Test
    public void testAssignToLocal2CursorAtEnd() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Vector;\n");
        buf.append("public class E {\n");
        buf.append("    public Vector goo() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo().toArray();\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "goo().toArray();";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Vector;\n");
        buf.append("public class E {\n");
        buf.append("    private Object[] array;\n");
        buf.append("    public Vector goo() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        array = goo().toArray();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Vector;\n");
        buf.append("public class E {\n");
        buf.append("    public Vector goo() {\n");
        buf.append("        return null;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        Object[] array = goo().toArray();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testReplaceCatchClauseWithThrowsWithFinally() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        } finally {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "(IOException e)";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() throws IOException {\n");
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
        buf.append("    public void foo() {\n");
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
    public void testReplaceSingleCatchClauseWithThrows() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "(IOException e)";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
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
        buf.append("    public void foo() {\n");
        buf.append("        goo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(2);
        String preview3 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.io.IOException;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            goo();\n");
        buf.append("        } catch (IOException e) {\n");
        buf.append("        } finally {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2, preview3}, new String[]{expected1, expected2,
                                                                                               expected3});

    }

    @Test
    public void testUnwrapForLoop() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        for (int i= 0; i < 3; i++) {\n");
        buf.append("            goo();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "for";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnwrapDoStatement() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        do {\n");
        buf.append("            goo();\n");
        buf.append("            goo();\n");
        buf.append("            goo();\n");
        buf.append("        } while (true);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "do";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo();\n");
        buf.append("        goo();\n");
        buf.append("        goo();\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnwrapWhileLoop2Statements() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        while (true) {\n");
        buf.append("            goo();\n");
        buf.append("            System.out.println();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "while";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        goo();\n");
        buf.append("        System.out.println();\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnwrapIfStatement() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (1+ 3 == 6) {\n");
        buf.append("            StringBuffer buf= new StringBuffer();\n");
        buf.append("            buf.append(1);\n");
        buf.append("            buf.append(2);\n");
        buf.append("            buf.append(3);\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "if";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview1 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        StringBuffer buf= new StringBuffer();\n");
        buf.append("        buf.append(1);\n");
        buf.append("        buf.append(2);\n");
        buf.append("        buf.append(3);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        proposal = (CUCorrectionProposal)proposals.get(1);
        String preview2 = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (1+ 3 == 6) {\n");
        buf.append("            StringBuffer buf= new StringBuffer();\n");
        buf.append("            buf.append(1);\n");
        buf.append("            buf.append(2);\n");
        buf.append("            buf.append(3);\n");
        buf.append("        } else {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertEqualStringsIgnoreOrder(new String[]{preview1, preview2}, new String[]{expected1, expected2});
    }

    @Test
    public void testUnwrapTryStatement() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        try {\n");
        buf.append("            StringBuffer buf= new StringBuffer();\n");
        buf.append("            buf.append(1);\n");
        buf.append("            buf.append(2);\n");
        buf.append("            buf.append(3);\n");
        buf.append("        } finally {\n");
        buf.append("            return;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "try";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        StringBuffer buf= new StringBuffer();\n");
        buf.append("        buf.append(1);\n");
        buf.append("        buf.append(2);\n");
        buf.append("        buf.append(3);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnwrapAnonymous() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Runnable run= new Runnable() {\n");
        buf.append("            public void run() { \n");
        buf.append("                throw new NullPointerException();\n");
        buf.append("            }\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "};";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        throw new NullPointerException();\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnwrapBlock() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        {\n");
        buf.append("            { \n");
        buf.append("                throw new NullPointerException();\n");
        buf.append("            }//comment\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "}//comment";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        {\n");
        buf.append("            throw new NullPointerException();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testUnwrapMethodInvocation() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public int foo() {\n");
        buf.append("        return Math.abs(9+ 8);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "Math.abs(9+ 8)";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, false);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public int foo() {\n");
        buf.append("        return 9+ 8;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testSplitDeclaration1() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i = 9;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "=";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i;\n");
        buf.append("        i = 9;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testSplitDeclaration2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        for (int i = 0; i < 9; i++) {\n");
        buf.append("       }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "=";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i;\n");
        buf.append("        for (i = 0; i < 9; i++) {\n");
        buf.append("       }\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testSplitDeclaration3() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        final int i[] = null;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "i[]";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        final int i[];\n");
        buf.append("        i = null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    public void testSplitDeclaration4() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package e;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test() {\n");
        buf.append("        String[] test = new String[0];\n");
        buf.append("    }\n");
        buf.append("}");

        String str = "=";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package e;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test() {\n");
        buf.append("        String[] test;\n");
        buf.append("        test = new String[0];\n");
        buf.append("    }\n");
        buf.append("}");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testSplitDeclaration5() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package e;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test() {\n");
        buf.append("        String[] test = { null };\n");
        buf.append("    }\n");
        buf.append("}");

        String str = "=";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package e;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test() {\n");
        buf.append("        String[] test;\n");
        buf.append("        test = new String[]{ null };\n");
        buf.append("    }\n");
        buf.append("}");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testSplitDeclaration6() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package e;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test() {\n");
        buf.append("        String[] test = { \"a\" };\n");
        buf.append("    }\n");
        buf.append("}");

        String str = "=";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package e;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test() {\n");
        buf.append("        String[] test;\n");
        buf.append("        test = new String[]{ \"a\" };\n");
        buf.append("    }\n");
        buf.append("}");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testSplitDeclaration7() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package e;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test() {\n");
        buf.append("        String[] test = x;\n");
        buf.append("    }\n");
        buf.append("}");

        String str = "=";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package e;\n");
        buf.append("public class Test {\n");
        buf.append("    public void test() {\n");
        buf.append("        String[] test;\n");
        buf.append("        test = x;\n");
        buf.append("    }\n");
        buf.append("}");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testJoinDeclaration1() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int var[];\n");
        buf.append("        foo();\n");
        buf.append("        var = null;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "var[]";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int var[] = null;\n");
        buf.append("        foo();\n");
        buf.append("    }\n");
        buf.append("}\n");

        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    public void testJoinDeclaration2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int var[];\n");
        buf.append("        foo();\n");
        buf.append("        var = null;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "var = ";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        foo();\n");
        buf.append("        int var[] = null;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testJoinDeclaration3() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int var[] = null;\n");
        buf.append("        foo();\n");
        buf.append("        var = new int[10];\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "var[]";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int var[] = new int[10];\n");
        buf.append("        foo();\n");
        buf.append("    }\n");
        buf.append("}\n");

        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    public void testJoinDeclaration4() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        // 1;\n");
        buf.append("        \n");
        buf.append("        String message;\n");
        buf.append("        \n");
        buf.append("        // 2;\n");
        buf.append("        \n");
        buf.append("        message = \"\";\n");
        buf.append("        \n");
        buf.append("        // 3;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "message;";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        // 1;\n");
        buf.append("        \n");
        buf.append("        String message = \"\";\n");
        buf.append("        \n");
        buf.append("        // 2;\n");
        buf.append("\n");
        buf.append("        \n");
        buf.append("        // 3;\n");
        buf.append("    }\n");
        buf.append("}\n");

        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    public void testJoinDeclaration5() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        // 1;\n");
        buf.append("        \n");
        buf.append("        String message;\n");
        buf.append("        \n");
        buf.append("        // 2;\n");
        buf.append("        \n");
        buf.append("        message = \"\";\n");
        buf.append("        \n");
        buf.append("        // 3;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "message =";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        // 1;\n");
        buf.append("        \n");
        buf.append("        \n");
        buf.append("        \n");
        buf.append("        // 2;\n");
        buf.append("        \n");
        buf.append("        String message = \"\";\n");
        buf.append("        \n");
        buf.append("        // 3;\n");
        buf.append("    }\n");
        buf.append("}\n");

        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    private static final Class[] FILTER_EQ =
            { /*LinkedNamesAssistProposal.class, RenameRefactoringProposal.class,*/AssignToVariableAssistProposal.class};

    @Test
    public void testInvertEquals() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals(\"b\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"b\".equals(\"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals(\"b\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String s= \"a\";\n");
        buf.append("        s.equals(\"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "s.equals(\"a\")";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), str.length(), "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String s= \"a\";\n");
        buf.append("        \"a\".equals(s);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});

        str = "\"a\".equals(s)";
        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String s= \"a\";\n");
        buf.append("        s.equals(\"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    public void testInvertEquals3() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private String a= \"a\";\n");
        buf.append("    private String b= \"b\";\n");
        buf.append("    public void foo() {\n");
        buf.append("        a.equals(b);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), str.length(), "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private String a= \"a\";\n");
        buf.append("    private String b= \"b\";\n");
        buf.append("    public void foo() {\n");
        buf.append("        b.equals(a);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), str.length(), "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private String a= \"a\";\n");
        buf.append("    private String b= \"b\";\n");
        buf.append("    public void foo() {\n");
        buf.append("        a.equals(b);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals4() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("class S {\n");
        buf.append("    protected String sup= \"a\";\n");
        buf.append("}\n");
        buf.append("public class E extends S {\n");
        buf.append("    private String a= \"a\";\n");
        buf.append("    public void foo() {\n");
        buf.append("        sup.equals(this.a);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("class S {\n");
        buf.append("    protected String sup= \"a\";\n");
        buf.append("}\n");
        buf.append("public class E extends S {\n");
        buf.append("    private String a= \"a\";\n");
        buf.append("    public void foo() {\n");
        buf.append("        this.a.equals(sup);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("class S {\n");
        buf.append("    protected String sup= \"a\";\n");
        buf.append("}\n");
        buf.append("public class E extends S {\n");
        buf.append("    private String a= \"a\";\n");
        buf.append("    public void foo() {\n");
        buf.append("        sup.equals(this.a);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals5() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("class A {\n");
        buf.append("    static String A= \"a\";\n");
        buf.append("}\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals(A.A);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("class A {\n");
        buf.append("    static String A= \"a\";\n");
        buf.append("}\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        A.A.equals(\"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("class A {\n");
        buf.append("    static String A= \"a\";\n");
        buf.append("}\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals(A.A);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals6() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("class A {\n");
        buf.append("    static String get() {\n");
        buf.append("        return \"a\";\n");
        buf.append("    }\n");
        buf.append("}\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals(A.get());\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("class A {\n");
        buf.append("    static String get() {\n");
        buf.append("        return \"a\";\n");
        buf.append("    }\n");
        buf.append("}\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        A.get().equals(\"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("class A {\n");
        buf.append("    static String get() {\n");
        buf.append("        return \"a\";\n");
        buf.append("    }\n");
        buf.append("}\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals(A.get());\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals7() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".getClass().equals(String.class);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String.class.equals(\"a\".getClass());\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".getClass().equals(String.class);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals8() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        boolean x = false && \"a\".equals(get());\n");
        buf.append("    }\n");
        buf.append("    String get() {\n");
        buf.append("        return \"a\";\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        boolean x = false && get().equals(\"a\");\n");
        buf.append("    }\n");
        buf.append("    String get() {\n");
        buf.append("        return \"a\";\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        boolean x = false && \"a\".equals(get());\n");
        buf.append("    }\n");
        buf.append("    String get() {\n");
        buf.append("        return \"a\";\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals9() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        equals(new E());\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        new E().equals(this);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        equals(new E());\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals10() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals(null);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);
    }

    @Test
    public void testInvertEquals11() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    boolean equals(Object o, boolean a) {\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        new E().equals(\"a\", false);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "E().equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().lastIndexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);
    }

    @Test
    public void testInvertEquals12() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    boolean equals(boolean b) {\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        new E().equals(false);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "E().equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().lastIndexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);
    }

    @Test
    public void testInvertEquals13() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    boolean equals(boolean b) {\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        new E().equals(true ? true : false);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "E().equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().lastIndexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);
    }

    @Test
    public void testInvertEquals14() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("class Super {\n");
        buf.append("    protected boolean sBool= false;\n");
        buf.append("}\n");
        buf.append("public class E extends Super {\n");
        buf.append("    boolean equals(boolean b) {\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        new E().equals(sBool);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "E().equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().lastIndexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);
    }

    @Test
    public void testInvertEquals15() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    boolean equals(int i) {\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        new E().equals(1 + 1);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "E().equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().lastIndexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);
    }

    @Test
    public void testInvertEquals16() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    boolean equals(int i) {\n");
        buf.append("        return false;\n");
        buf.append("    }\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i= 1;\n");
        buf.append("        new E().equals(i + i);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "E().equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().lastIndexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);
    }

    @Test
    public void testInvertEquals17() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("       \"a\".equals(null);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);
    }

    @Test
    public void testInvertEquals18() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean equals(Object o) {\n");
        buf.append("       return super.equals(o);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals(o)";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().lastIndexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 0);
        assertCorrectLabels(proposals);
    }

    @Test
    public void testInvertEquals19() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private String a= \"a\";\n");
        buf.append("    public void foo() {\n");
        buf.append("        a.equals((Object) \"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private String a= \"a\";\n");
        buf.append("    public void foo() {\n");
        buf.append("        ((Object) \"a\").equals(a);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private String a= \"a\";\n");
        buf.append("    public void foo() {\n");
        buf.append("        a.equals((Object) \"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals20() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String s= null;\n");
        buf.append("        \"a\".equals(s = \"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String s= null;\n");
        buf.append("        (s = \"a\").equals(\"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String s= null;\n");
        buf.append("        \"a\".equals(s = \"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals21() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"aaa\".equals(\"a\" + \"a\" + \"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        (\"a\" + \"a\" + \"a\").equals(\"aaa\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"aaa\".equals(\"a\" + \"a\" + \"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals22() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals(true ? \"a\" : \"b\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        (true ? \"a\" : \"b\").equals(\"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals(true ? \"a\" : \"b\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testInvertEquals23() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals((\"a\"));\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "equals";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        CUCorrectionProposal proposal = (CUCorrectionProposal)proposals.get(0);
        String preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        (\"a\").equals(\"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

        context = getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        proposals = collectAssists(context, FILTER_EQ);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        proposal = (CUCorrectionProposal)proposals.get(0);
        preview = getPreviewContent(proposal);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        \"a\".equals(\"a\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    @Ignore
    public void testAddTypeToArrayInitializer() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int[][] numbers= {{ 1, 2 }, { 3, 4 }, { 4, 5 }};\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "{{";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str) + str.length(), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        ArrayList previews = new ArrayList();
        ArrayList expecteds = new ArrayList();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int[][] numbers= new int[][]{{ 1, 2 }, { 3, 4 }, { 4, 5 }};\n");
        buf.append("    }\n");
        buf.append("}\n");
        addPreviewAndExpected(proposals, buf, expecteds, previews);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    private static final int[] INTS = { 1, 2 };\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        int[][] numbers= {INTS, { 3, 4 }, { 4, 5 }};\n");
        buf.append("    }\n");
        buf.append("}\n");
        addPreviewAndExpected(proposals, buf, expecteds, previews);

        assertEqualStringsIgnoreOrder(previews, expecteds);
    }

    //	public void testCreateInSuper() throws Exception {
    //
    //			StringBuffer buf= new StringBuffer();
    //			buf.append("package test1;\n");
    //			buf.append("public class A {\n");
    //			buf.append("}\n");
    //			pack1.createCompilationUnit("A.java", buf.toString(), false, null);
    //
    //			buf= new StringBuffer();
    //			buf.append("package test1;\n");
    //			buf.append("public interface IB {\n");
    //			buf.append("}\n");
    //			pack1.createCompilationUnit("IB.java", buf.toString(), false, null);
    //
    //			buf= new StringBuffer();
    //			buf.append("package test1;\n");
    //			buf.append("import java.io.IOException;\n");
    //			buf.append("import java.util.Vector;\n");
    //			buf.append("public class E extends A implements IB {\n");
    //			buf.append("    public Vector foo(int count) throws IOException {\n");
    //			buf.append("        return null;\n");
    //			buf.append("    }\n");
    //			buf.append("}\n");
    //			ICompilationUnit cu= pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //			String str= "foo";
    //			AssistContext context= getCorrectionContext(cu, buf.toString().indexOf(str) + str.length(), 0);
    //			List proposals= collectAssists(context, false);
    //
    //			assertNumberOfProposals(proposals, 2);
    //			assertCorrectLabels(proposals);
    //
    //			CUCorrectionProposal proposal= (CUCorrectionProposal) proposals.get(0);
    //			String preview1= getPreviewContent(proposal);
    //
    //			buf= new StringBuffer();
    //			buf.append("package test1;\n");
    //			buf.append("\n");
    //			buf.append("import java.io.IOException;\n");
    //			buf.append("import java.util.Vector;\n");
    //			buf.append("\n");
    //			buf.append("public interface IB {\n");
    //			buf.append("\n");
    //			buf.append("    Vector foo(int count) throws IOException;\n");
    //			buf.append("}\n");
    //			String expected1= buf.toString();
    //
    //			proposal= (CUCorrectionProposal) proposals.get(1);
    //			String preview2= getPreviewContent(proposal);
    //
    //			buf= new StringBuffer();
    //			buf.append("package test1;\n");
    //			buf.append("\n");
    //			buf.append("import java.io.IOException;\n");
    //			buf.append("import java.util.Vector;\n");
    //			buf.append("\n");
    //			buf.append("public class A {\n");
    //			buf.append("\n");
    //			buf.append("    public Vector foo(int count) throws IOException {\n");
    //			buf.append("        //TODO\n");
    //			buf.append("        return null;\n");
    //			buf.append("    }\n");
    //			buf.append("}\n");
    //			String expected2= buf.toString();
    //
    //			assertEqualStringsIgnoreOrder(new String[] { preview1, preview2 }, new String[] { expected1, expected2 });
    //
    //		}
    //
    //	public void testCreateInSuperInGeneric() throws Exception {
    //
    //			IPackageFragment pack1= fSourceFolder.createPackageFragment("test1", false, null);
    //			StringBuffer buf= new StringBuffer();
    //			buf.append("package test1;\n");
    //			buf.append("public class A<T> {\n");
    //			buf.append("}\n");
    //			pack1.createCompilationUnit("A.java", buf.toString(), false, null);
    //
    //			buf= new StringBuffer();
    //			buf.append("package test1;\n");
    //			buf.append("public interface IB<T> {\n");
    //			buf.append("}\n");
    //			pack1.createCompilationUnit("IB.java", buf.toString(), false, null);
    //
    //			buf= new StringBuffer();
    //			buf.append("package test1;\n");
    //			buf.append("import java.io.IOException;\n");
    //			buf.append("import java.util.Vector;\n");
    //			buf.append("public class E extends A<String> implements IB<String> {\n");
    //			buf.append("    public Vector<String> foo(int count) throws IOException {\n");
    //			buf.append("        return null;\n");
    //			buf.append("    }\n");
    //			buf.append("}\n");
    //			ICompilationUnit cu= pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //			String str= "foo";
    //			AssistContext context= getCorrectionContext(cu, buf.toString().indexOf(str) + str.length(), 0);
    //			List proposals= collectAssists(context, false);
    //
    //			assertNumberOfProposals(proposals, 2);
    //			assertCorrectLabels(proposals);
    //
    //			CUCorrectionProposal proposal= (CUCorrectionProposal) proposals.get(0);
    //			String preview1= getPreviewContent(proposal);
    //
    //			buf= new StringBuffer();
    //			buf.append("package test1;\n");
    //			buf.append("\n");
    //			buf.append("import java.io.IOException;\n");
    //			buf.append("import java.util.Vector;\n");
    //			buf.append("\n");
    //			buf.append("public interface IB<T> {\n");
    //			buf.append("\n");
    //			buf.append("    Vector<String> foo(int count) throws IOException;\n");
    //			buf.append("}\n");
    //			String expected1= buf.toString();
    //
    //			proposal= (CUCorrectionProposal) proposals.get(1);
    //			String preview2= getPreviewContent(proposal);
    //
    //			buf= new StringBuffer();
    //			buf.append("package test1;\n");
    //			buf.append("\n");
    //			buf.append("import java.io.IOException;\n");
    //			buf.append("import java.util.Vector;\n");
    //			buf.append("\n");
    //			buf.append("public class A<T> {\n");
    //			buf.append("\n");
    //			buf.append("    public Vector<String> foo(int count) throws IOException {\n");
    //			buf.append("        //TODO\n");
    //			buf.append("        return null;\n");
    //			buf.append("    }\n");
    //			buf.append("}\n");
    //			String expected2= buf.toString();
    //
    //			assertEqualStringsIgnoreOrder(new String[] { preview1, preview2 }, new String[] { expected1, expected2 });
    //
    //		}

    @Test
    public void testChangeIfStatementToBlock() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) \n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "if (";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, true);

        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) \n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2, expected3});
    }

    @Test
    public void testChangeElseStatementToBlock() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;;\n");
        buf.append("        } else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "else";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;;\n");
        buf.append("        } else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false)\n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("            ;;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2});
    }

    @Test
    public void testChangeIfWithElseStatementToBlock() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("            ;;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "if (";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("            ;;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false) {\n");
        buf.append("            ;;\n");
        buf.append("        } else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2});
    }

    @Test
    public void testChangeIfAndElseStatementToBlock1() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "if (";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false)\n");
        buf.append("            ;\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2, expected3});
    }

    @Test
    public void testChangeIfAndElseStatementToBlock2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "else";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false)\n");
        buf.append("            ;\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2, expected3});
    }

    @Test
    public void testChangeIfAndElseIfStatementToBlock() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else if (true)\n");
        buf.append("            ;\n");
        buf.append("        else if (false)\n");
        buf.append("            ;\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "else if (";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("            if (true)\n");
        buf.append("                ;\n");
        buf.append("            else if (false)\n");
        buf.append("                ;\n");
        buf.append("            else\n");
        buf.append("                ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else if (false) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false) {\n");
        buf.append("            if (true)\n");
        buf.append("                ;\n");
        buf.append("            else if (false)\n");
        buf.append("                ;\n");
        buf.append("            else\n");
        buf.append("                ;\n");
        buf.append("        } else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2, expected3});
    }

    @Test
    public void testChangeIfAndElseIfStatementWithBlockToBlock() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else if (false)\n");
        buf.append("            ;\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "else if (";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("            if (true) {\n");
        buf.append("                ;\n");
        buf.append("            } else if (false)\n");
        buf.append("                ;\n");
        buf.append("            else\n");
        buf.append("                ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else if (false) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false) {\n");
        buf.append("            if (true) {\n");
        buf.append("                ;\n");
        buf.append("            } else if (false)\n");
        buf.append("                ;\n");
        buf.append("            else\n");
        buf.append("                ;\n");
        buf.append("        } else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2, expected3});
    }

    @Test
    public void testRemoveIfBlock01() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "if (";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2, expected3});
    }

    @Test
    public void testRemoveIfBlock02() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "if (";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 4);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected4 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2, expected3, expected4});
    }

    @Test
    public void testRemoveIfBlock03() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "{\n            ;";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2, expected3});
    }

    @Test
    public void testRemoveIfBlock04() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public int foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            return 1; /* comment*/\n");
        buf.append("        else\n");
        buf.append("            return 2;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "/* comment*/";
        int indexOf = buf.toString().indexOf(str) + str.length();
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), indexOf, 0, "E");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public int foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            return 1; /* comment*/\n");
        buf.append("        } else\n");
        buf.append("            return 2;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testRemoveIfBlockBug128843() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else if (false) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = " (false) {";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 4);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else if (false)\n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else if (false) {\n");
        buf.append("            ;\n");
        buf.append("        } else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else if (false)\n");
        buf.append("            ;\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else if (true) {\n");
        buf.append("            ;\n");
        buf.append("        } else {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected4 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2, expected3, expected4});
    }

    @Test
    public void testRemoveIfBlockBug138628() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true) {\n");
        buf.append("            if (true)\n");
        buf.append("                ;\n");
        buf.append("        } else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = " (true) {";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false)\n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("            if (true)\n");
        buf.append("                ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testRemoveIfBlockBug149990_1() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false) {\n");
        buf.append("            while (true)\n");
        buf.append("                if (false) {\n");
        buf.append("                    ;\n");
        buf.append("                }\n");
        buf.append("        } else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = " (false) {";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (true)\n");
        buf.append("            ;\n");
        buf.append("        else {\n");
        buf.append("            while (true)\n");
        buf.append("                if (false) {\n");
        buf.append("                    ;\n");
        buf.append("                }\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testRemoveIfBlockBug139675() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false)\n");
        buf.append("            if (true) {\n");
        buf.append("                ;\n");
        buf.append("            } else if (false) {\n");
        buf.append("                ;\n");
        buf.append("            } else {\n");
        buf.append("                ;\n");
        buf.append("            }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = " (true) {";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 3);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false)\n");
        buf.append("            if (true)\n");
        buf.append("                ;\n");
        buf.append("            else if (false) {\n");
        buf.append("                ;\n");
        buf.append("            } else {\n");
        buf.append("                ;\n");
        buf.append("            }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false)\n");
        buf.append("            if (true)\n");
        buf.append("                ;\n");
        buf.append("            else if (false)\n");
        buf.append("                ;\n");
        buf.append("            else\n");
        buf.append("                ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false)\n");
        buf.append("            if (false) {\n");
        buf.append("                if (false) {\n");
        buf.append("                    ;\n");
        buf.append("                } else {\n");
        buf.append("                    ;\n");
        buf.append("                }\n");
        buf.append("            } else {\n");
        buf.append("                ;\n");
        buf.append("            }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected3 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2, expected3});
    }

    @Test
    public void testRemoveIfBlockBug149990_2() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false)\n");
        buf.append("            while (true)\n");
        buf.append("                while (true) {\n");
        buf.append("                    while (true)\n");
        buf.append("                        if (false)\n");
        buf.append("                            ;\n");
        buf.append("                }\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = " (true) {";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        if (false)\n");
        buf.append("            while (true)\n");
        buf.append("                while (true)\n");
        buf.append("                    if (false)\n");
        buf.append("                        ;\n");
        buf.append("        else\n");
        buf.append("            ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});

    }

    @Test
    public void testRemoveWhileBlock01() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        while (true) {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "while (";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        while (true);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveForBlock01() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        for (;;) {\n");
        buf.append("            ;\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "for (";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        for (;;);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2});
    }

    @Test
    public void testRemoveDoBlock01() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        do {\n");
        buf.append("            ;\n");
        buf.append("        } while (true);\n");
        buf.append("    }\n");
        buf.append("}\n");

        String str = "do {";
        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(str), 0, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        do; while (true);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        ;\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2});
    }

    @Test
    @Ignore
    public void testMakeFinal01() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private int i= 0;\n");
        buf.append("    private void foo() {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset1 = buf.toString().indexOf("public");
        int offset2 = buf.toString().lastIndexOf("}");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset1, offset2 - offset1, "E");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private final int i= 0;\n");
        buf.append("    private void foo() {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testMakeFinal02() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private final int i= 0;\n");
        buf.append("    private void foo() {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset1 = buf.toString().indexOf("public");
        int offset2 = buf.toString().lastIndexOf("}");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset1, offset2 - offset1, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    public void testMakeFinal03() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public int i= 0;\n");
        buf.append("    private void foo() {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("i=");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 1, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    @Ignore
    public void testMakeFinal04() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private void foo() {\n");
        buf.append("        int i= 0, j= 0;\n");
        buf.append("        System.out.println(i);\n");
        buf.append("        System.out.println(j);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("int i= 0");
        int length = "int i= 0".length();
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, length, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private void foo() {\n");
        buf.append("        final int i= 0;\n");
        buf.append("        int j= 0;\n");
        buf.append("        System.out.println(i);\n");
        buf.append("        System.out.println(j);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    @Ignore
    public void testMakeFinal05() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private void foo(int i, int j) {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("        System.out.println(j);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("int i");
        int length = "int i".length();
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, length, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private void foo(final int i, int j) {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("        System.out.println(j);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    public void testMakeFinal06() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i= 0;\n");
        buf.append("        i= 1;\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset1 = buf.toString().indexOf("public");
        int offset2 = buf.toString().lastIndexOf("}");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset1, offset2 - offset1, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    public void testMakeFinal07() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private int i= 0;\n");
        buf.append("    public void foo() {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("    public void set(int i) {\n");
        buf.append("        this.i= i;\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("private int i= 0");
        int length = "private int i= 0".length();
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, length, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    public void testMakeFinal08() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private int i= 0;\n");
        buf.append("    public void foo() {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("    public void reset() {\n");
        buf.append("        i= 0;\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset1 = buf.toString().indexOf("public");
        int offset2 = buf.toString().lastIndexOf("}");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset1, offset2 - offset1, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    public void testMakeFinal09() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private int i= 0;\n");
        buf.append("    public void foo() {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("    public void reset() {\n");
        buf.append("        i--;\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset1 = buf.toString().indexOf("public");
        int offset2 = buf.toString().lastIndexOf("}");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset1, offset2 - offset1, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    public void testMakeFinal10() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private int i= 0;\n");
        buf.append("    public void foo() {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("    public void reset() {\n");
        buf.append("        this.i++;\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset1 = buf.toString().indexOf("public");
        int offset2 = buf.toString().lastIndexOf("}");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset1, offset2 - offset1, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    public void testMakeFinal11() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        for (int j= 0, i= 0; j < (new int[0]).length; j++) {\n");
        buf.append("            System.out.println(i);\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset1 = buf.toString().indexOf("public");
        int offset2 = buf.toString().lastIndexOf("}");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset1, offset2 - offset1, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    @Ignore
    public void testMakeFinal12() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i= 1, j= i + 1, h= j + 1;\n");
        buf.append("        System.out.println(i);\n");
        buf.append("        System.out.println(j);\n");
        buf.append("        System.out.println(h);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("int i= 1");
        int length = "int i= 1".length();
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, length, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        final int i= 1;\n");
        buf.append("        int j= i + 1, h= j + 1;\n");
        buf.append("        System.out.println(i);\n");
        buf.append("        System.out.println(j);\n");
        buf.append("        System.out.println(h);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    @Ignore
    public void testMakeFinal13() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i= 1, j= i + 1, h= j + 1;\n");
        buf.append("        System.out.println(i);\n");
        buf.append("        System.out.println(j);\n");
        buf.append("        System.out.println(h);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("j= i + 1");
        int length = "j= i + 1".length();
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, length, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i= 1;\n");
        buf.append("        final int j= i + 1;\n");
        buf.append("        int h= j + 1;\n");
        buf.append("        System.out.println(i);\n");
        buf.append("        System.out.println(j);\n");
        buf.append("        System.out.println(h);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    @Ignore
    public void testMakeFinal14() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i= 1, j= i + 1, h= j + 1;\n");
        buf.append("        System.out.println(i);\n");
        buf.append("        System.out.println(j);\n");
        buf.append("        System.out.println(h);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("h= j + 1");
        int length = "h= j + 1".length();
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, length, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 2);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i= 1, j= i + 1;\n");
        buf.append("        final int h= j + 1;\n");
        buf.append("        System.out.println(i);\n");
        buf.append("        System.out.println(j);\n");
        buf.append("        System.out.println(h);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    @Ignore
    public void testMakeFinal15() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("import java.io.Serializable;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        Serializable ser= new Serializable() {\n");
        buf.append("            private int i= 0;\n");
        buf.append("            Serializable ser2= new Serializable() {\n");
        buf.append("                public void foo() {\n");
        buf.append("                    System.out.println(i);\n");
        buf.append("                }\n");
        buf.append("            };\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset1 = buf.toString().indexOf("public");
        int offset2 = buf.toString().lastIndexOf("}");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset1, offset2 - offset1, "E");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("import java.io.Serializable;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        final Serializable ser= new Serializable() {\n");
        buf.append("            private final int i= 0;\n");
        buf.append("            Serializable ser2= new Serializable() {\n");
        buf.append("                public void foo() {\n");
        buf.append("                    System.out.println(i);\n");
        buf.append("                }\n");
        buf.append("            };\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testMakeFinal16() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int i= 0;\n");
        buf.append("        Integer in= new Integer(i++);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("int i= 0");
        int length = "int i= 0".length();
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, length, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    public void testMakeFinal17() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private int i= 0;\n");
        buf.append("    private void foo() {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("i=");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 1, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    public void testMakeFinal18() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private int i= 0;\n");
        buf.append("    private void foo() {\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("E");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 1, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    public void testMakeFinal19() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    private void foo() {\n");
        buf.append("        int i= 0;\n");
        buf.append("        System.out.println(i);\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("foo");
        int length = "foo".length();
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, length, "E");
        List proposals = collectAssists(context, false);

        assertProposalDoesNotExist(proposals, CHANGE_MODIFIER_TO_FINAL);
    }

    @Test
    @Ignore
    public void testMakeFinalBug148373() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(Integer i) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        String selection = "public void foo(Integer i)";
        int offset = buf.toString().indexOf(selection);
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, selection.length(), "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo(final Integer i) {\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    @Test
    @Ignore
    public void testConvertAnonymousToNested1() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    public Object foo(final String name) {\n");
        buf.append("        return new Runnable() {\n");
        buf.append("            public void run() {\n");
        buf.append("                foo(name);\n");
        buf.append("            }\n");
        buf.append("        };\n");
        buf.append("    }\n");
        buf.append("}\n");

        int offset = buf.toString().indexOf("Runnable");
        AssistContext context = getCorrectionContext(new DocumentImpl(buf.toString()), offset, 1, "E");
        List proposals = collectAssists(context, false);

        assertNumberOfProposals(proposals, 1);
        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package pack;\n");
        buf.append("public class E {\n");
        buf.append("    private final class RunnableImplementation implements Runnable {\n");
        buf.append("        private final String name;\n");
        buf.append("        private RunnableImplementation(String name) {\n");
        buf.append("            this.name = name;\n");
        buf.append("        }\n");
        buf.append("        public void run() {\n");
        buf.append("            foo(name);\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("\n");
        buf.append("    public Object foo(final String name) {\n");
        buf.append("        return new RunnableImplementation(name);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    }

    //   public void testConvertAnonymousToNested2() throws Exception
    //   {
    //      Preferences corePrefs = JavaPlugin.getJavaCorePluginPreferences();
    //      corePrefs.setValue(JavaCore.CODEASSIST_FIELD_PREFIXES, "f");
    //      corePrefs.setValue(JavaCore.CODEASSIST_LOCAL_PREFIXES, "l");
    //      corePrefs.setValue(JavaCore.CODEASSIST_ARGUMENT_PREFIXES, "p");
    //
    //      IPackageFragment pack1 = fSourceFolder.createPackageFragment("pack", false, null);
    //      StringBuffer buf = new StringBuffer();
    //      buf.append("package pack;\n");
    //      buf.append("import java.util.ArrayList;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    public Object foo(final String pName) {\n");
    //      buf.append("        int lVar= 8;\n");
    //      buf.append("        return new ArrayList(lVar) {\n");
    //      buf.append("            String fExisting= pName;\n");
    //      buf.append("            public void run() {\n");
    //      buf.append("                foo(fExisting);\n");
    //      buf.append("            }\n");
    //      buf.append("        };\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      buf.append("class ArrayListExtension {\n");
    //      buf.append("}\n");
    //      ICompilationUnit cu = pack1.createCompilationUnit("E.java", buf.toString(), false, null);
    //
    //      int offset = buf.toString().indexOf("ArrayList(lVar)");
    //      AssistContext context = getCorrectionContext(cu, offset, 1);
    //      List proposals = collectAssists(context, false);
    //
    //      assertNumberOfProposals(proposals, 1);
    //      assertCorrectLabels(proposals);
    //
    //      buf = new StringBuffer();
    //      buf.append("package pack;\n");
    //      buf.append("import java.util.ArrayList;\n");
    //      buf.append("public class E {\n");
    //      buf.append("    private final class ArrayListExtension2 extends ArrayList {\n");
    //      buf.append("        private final String fName;\n");
    //      buf.append("        String fExisting;\n");
    //      buf.append("        private ArrayListExtension2(int pArg0, String pName) {\n");
    //      buf.append("            super(pArg0);\n");
    //      buf.append("            fName = pName;\n");
    //      buf.append("            fExisting = fName;\n");
    //      buf.append("        }\n");
    //      buf.append("        public void run() {\n");
    //      buf.append("            foo(fExisting);\n");
    //      buf.append("        }\n");
    //      buf.append("    }\n");
    //      buf.append("\n");
    //      buf.append("    public Object foo(final String pName) {\n");
    //      buf.append("        int lVar= 8;\n");
    //      buf.append("        return new ArrayListExtension2(lVar, pName);\n");
    //      buf.append("    }\n");
    //      buf.append("}\n");
    //      buf.append("class ArrayListExtension {\n");
    //      buf.append("}\n");
    //      assertExpectedExistInProposals(proposals, new String[]{buf.toString()});
    //   }
    @Test
    public void testConvertToStringBuffer1() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String strX = \"foo\"+\"bar\"+\"baz\"+\"biz\";\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("\"+\""), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        StringBuilder stringBuilder = new StringBuilder();\n");
        buf.append("        stringBuilder.append(\"foo\");\n");
        buf.append("        stringBuilder.append(\"bar\");\n");
        buf.append("        stringBuilder.append(\"baz\");\n");
        buf.append("        stringBuilder.append(\"biz\");\n");
        buf.append("        String strX = stringBuilder.toString();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected});
    }

    @Test
    public void testConvertToStringBufferStringAndVar() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String foo = \"foo\";\n");
        buf.append("        String fuu = \"fuu\";\n");
        buf.append("        String strX = foo+\"bar\"+fuu;\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("strX ="), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String foo = \"foo\";\n");
        buf.append("        String fuu = \"fuu\";\n");
        buf.append("        StringBuilder stringBuilder = new StringBuilder();\n");
        buf.append("        stringBuilder.append(foo);\n");
        buf.append("        stringBuilder.append(\"bar\");\n");
        buf.append("        stringBuilder.append(fuu);\n");
        buf.append("        String strX = stringBuilder.toString();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected});
    }

    @Test
    public void testConvertToStringBufferNoFixWithoutString() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int strX = 5+1;\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("strX ="), 0, "A");
        List proposals = collectAssists(context, false);

        assertCommandIdDoesNotExist(proposals, QuickAssistProcessorImpl.CONVERT_TO_STRING_BUFFER_ID);
    }

    @Test
    public void testConvertToStringBufferNoFixWithoutString2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int strX;\n");
        buf.append("        strX = 5+1;\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("strX ="), 0, "A");
        List proposals = collectAssists(context, false);

        assertCommandIdDoesNotExist(proposals, QuickAssistProcessorImpl.CONVERT_TO_STRING_BUFFER_ID);
    }

    @Test
    public void testConvertToStringBufferNoFixOutsideMethod() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    String strX = \"foo\"+\"bar\"\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("strX ="), 0, "A");
        List proposals = collectAssists(context, false);

        assertCommandIdDoesNotExist(proposals, QuickAssistProcessorImpl.CONVERT_TO_STRING_BUFFER_ID);
    }

    @Test
    public void testConvertToStringBufferDupVarName() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int stringBuilder = 5;\n");
        buf.append("        String stringBuilder2;\n");
        buf.append("        StringBuilder stringBuilder3 = null;\n");
        buf.append("        String strX = \"foo\"+\"bar\";\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("strX ="), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        int stringBuilder = 5;\n");
        buf.append("        String stringBuilder2;\n");
        buf.append("        StringBuilder stringBuilder3 = null;\n");
        buf.append("        StringBuilder stringBuilder4 = new StringBuilder();\n");
        buf.append("        stringBuilder4.append(\"foo\");\n");
        buf.append("        stringBuilder4.append(\"bar\");\n");
        buf.append("        String strX = stringBuilder4.toString();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected});
    }

    @Test
    public void testConvertToStringBufferInIfStatement() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String strX;\n");
        buf.append("        if(true) strX = \"foo\"+\"bar\";\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("\"+\""), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String strX;\n");
        buf.append("        if(true) {\n");
        buf.append("            StringBuilder stringBuilder = new StringBuilder();\n");
        buf.append("            stringBuilder.append(\"foo\");\n");
        buf.append("            stringBuilder.append(\"bar\");\n");
        buf.append("            strX = stringBuilder.toString();\n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected});
    }

    @Test
    public void testConvertToStringBufferAsParamter() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        System.out.println(\"foo\"+\"bar\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("\"+\""), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        StringBuilder stringBuilder = new StringBuilder();\n");
        buf.append("        stringBuilder.append(\"foo\");\n");
        buf.append("        stringBuilder.append(\"bar\");\n");
        buf.append("        System.out.println(stringBuilder.toString());\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testConvertToStringBufferExisting1() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        StringBuffer buf= new StringBuffer();\n");
        buf.append("        buf.append(\"high\" + 5);\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("\" + 5"), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        StringBuffer buf= new StringBuffer();\n");
        buf.append("        buf.append(\"high\");\n");
        buf.append("        buf.append(5);\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testConvertToStringBufferExisting2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        StringBuilder sb= new StringBuilder();\n");
        buf.append("        sb.append(\"high\" + 5 + \" ho\");\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("\" + 5"), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        StringBuilder sb= new StringBuilder();\n");
        buf.append("        sb.append(\"high\");\n");
        buf.append("        sb.append(5);\n");
        buf.append("        sb.append(\" ho\");\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testConvertToMessageFormatStringConcat() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo(Object o1, Object o2) {\n");
        buf.append("        System.out.println(\"foo\" + \"\" + \" \\\"bar\\\" \");\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("\" + \"\" + \""), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        assertCommandIdDoesNotExist(proposals, QuickAssistProcessorImpl.CONVERT_TO_MESSAGE_FORMAT_ID);
    }

    @Test
    public void testConvertToMessageFormatStringBoxing15() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo(Object o1, Object o2) {\n");
        buf.append("        System.out.println(\"foo\" + 1 + \" \\\"bar\\\" \");\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("\" + 1 + \""), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.text.MessageFormat;\n");
        buf.append("\n");
        buf.append("public class A {\n");
        buf.append("    public void foo(Object o1, Object o2) {\n");
        buf.append("        System.out.println(MessageFormat.format(\"foo{0} \\\"bar\\\" \", 1));\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testConvertToMessageFormat15() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo(Object o1, Object o2) {\n");
        buf.append("        System.out.println(\"foo\" + o1 + \" \\\"bar\\\" \" + o2);\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("\" + o1 + \""), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.text.MessageFormat;\n");
        buf.append("\n");
        buf.append("public class A {\n");
        buf.append("    public void foo(Object o1, Object o2) {\n");
        buf.append("        System.out.println(MessageFormat.format(\"foo{0} \\\"bar\\\" {1}\", o1, o2));\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testConvertToMessageFormatApostrophe() throws Exception {

        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo(Object o1, Object o2) {\n");
        buf.append("        System.out.println(\"foo'\" + o1 + \"' \\\"bar\\\" \" + o2);\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("\" + o1 + \""), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.text.MessageFormat;\n");
        buf.append("\n");
        buf.append("public class A {\n");
        buf.append("    public void foo(Object o1, Object o2) {\n");
        buf.append("        System.out.println(MessageFormat.format(\"foo''{0}'' \\\"bar\\\" {1}\", o1, o2));\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1});
    }

    @Test
    public void testConvertToMessageFormatExtendedOperands() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String s2= \"a\" + \"b\" + 3L + \"c\" + (4-2) + \"d\" + \"e\" + \"f\";\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf(" + "), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("\n");
        buf.append("import java.text.MessageFormat;\n");
        buf.append("\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        String s2= MessageFormat.format(\"ab{0}c{1}def\", 3L, (4-2));\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected1 = buf.toString();

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class A {\n");
        buf.append("    public void foo() {\n");
        buf.append("        StringBuilder stringBuilder = new StringBuilder();\n");
        buf.append("        stringBuilder.append(\"a\");\n");
        buf.append("        stringBuilder.append(\"b\");\n");
        buf.append("        stringBuilder.append(3L);\n");
        buf.append("        stringBuilder.append(\"c\");\n");
        buf.append("        stringBuilder.append((4-2));\n");
        buf.append("        stringBuilder.append(\"d\");\n");
        buf.append("        stringBuilder.append(\"e\");\n");
        buf.append("        stringBuilder.append(\"f\");\n");
        buf.append("        String s2= stringBuilder.toString();\n");
        buf.append("    }\n");
        buf.append("}\n");
        String expected2 = buf.toString();

        assertExpectedExistInProposals(proposals, new String[]{expected1, expected2});
    }

    @Test
    public void testMissingEnumConstantsInCase1() throws Exception {
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

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("switch"), 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

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
        buf.append("        \n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("break;") + 7, 0, "A");
        List proposals = collectAssists(context, false);

        assertCorrectLabels(proposals);
        assertNumberOfProposals(proposals, 2);

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
    public void testMissingEnumConstantsInCase3() throws Exception {
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
        buf.append("        \n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("case"), 0, "A");
        List proposals = collectAssists(context, false);

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
    public void testMissingEnumConstantsInCase4() throws Exception {
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
        buf.append("            default :\n");
        buf.append("                break;\n");
        buf.append("        \n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");

        AssistContext context =
                getCorrectionContext(new DocumentImpl(buf.toString()), buf.toString().indexOf("default"), 0, "A");
        List proposals = collectAssists(context, false);

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
        buf.append("            case X3 :\n");
        buf.append("            default :\n");
        buf.append("                break;\n");
        buf.append("        \n");
        buf.append("        }\n");
        buf.append("    }\n");
        buf.append("}\n");
        expected[0] = buf.toString();

        assertExpectedExistInProposals(proposals, expected);
    }
}
