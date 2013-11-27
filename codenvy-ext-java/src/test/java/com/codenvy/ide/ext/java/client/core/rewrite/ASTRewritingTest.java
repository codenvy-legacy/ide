/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.client.core.rewrite;

import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.dom.*;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.formatter.DefaultCodeFormatterConstants;
import com.codenvy.ide.ext.java.jdt.core.formatter.DefaultCodeFormatterOptions;

import com.codenvy.ide.ext.java.client.BaseTest;
import com.codenvy.ide.ext.java.client.JavaExtension;
import com.codenvy.ide.ext.java.client.core.quickfix.StringAsserts;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit;
import com.codenvy.ide.ext.java.emul.FileSystem;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentImpl;
import com.codenvy.ide.text.edits.TextEdit;

import org.junit.Before;

import java.util.HashMap;
import java.util.List;

public abstract class ASTRewritingTest extends BaseTest {
    /** @deprecated using deprecated code */
    private static final int AST_INTERNAL_JLS2 = AST.JLS2;


    public ASTRewritingTest() {
        //      super(name);
    }

    /** @see com.google.gwt.junit.client.GWTTestCase#gwtSetUp() */
    @Before
    public void gwtSetUp() throws Exception {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
        options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES,
                    DefaultCodeFormatterConstants.TRUE);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH,
                    DefaultCodeFormatterConstants.TRUE);
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
        options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
        options.putAll(DefaultCodeFormatterOptions.getEclipseDefaultSettings().getMap());
        new JavaExtension();
        JavaExtension.get().getOptions().putAll(options);
    }

    //   protected void setUp() throws Exception {
    //      super.setUp();
    //
    //      IJavaProject proj= createProject("P", JavaCore.VERSION_1_5);
    //
    //      this.project1 = proj;
    //      this.sourceFolder = getPackageFragmentRoot("P", "src");
    //
    //      waitUntilIndexesReady();
    //   }

    //   protected IJavaProject createProject(String projectName, String complianceVersion) throws CoreException {
    //      IJavaProject proj = createJavaProject(projectName, new String[] {"src"}, "bin");
    //      proj.setOption(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
    //      proj.setOption(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
    //      proj.setOption(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES,
    // DefaultCodeFormatterConstants.TRUE);
    //      proj.setOption(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH,
    // DefaultCodeFormatterConstants.TRUE);
    //      proj.setOption(JavaCore.COMPILER_COMPLIANCE, complianceVersion);
    //      proj.setOption(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
    //      proj.setOption(JavaCore.COMPILER_SOURCE, complianceVersion);
    //      proj.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, complianceVersion);
    //      return proj;
    //   }
    //   protected void tearDown() throws Exception {
    //      deleteProject("P");
    //      super.tearDown();
    //   }

    protected CompilationUnit createAST(ICompilationUnit cu) {
        return createAST(AST_INTERNAL_JLS2, cu, false);
    }

    protected CompilationUnit createAST3(ICompilationUnit cu) {
        return createAST(AST.JLS3, cu, false);
    }

    protected CompilationUnit createAST3(ICompilationUnit cu, boolean statementsRecovery) {
        return createAST(AST.JLS3, cu, statementsRecovery);
    }

    protected CompilationUnit createAST(int JLSLevel, ICompilationUnit cu, boolean statementsRecovery) {
        return createAST(JLSLevel, cu, false, statementsRecovery);
    }

    protected CompilationUnit createAST(int JLSLevel, ICompilationUnit cu, boolean resolveBindings,
                                        boolean statementsRecovery) {
        ASTParser parser = ASTParser.newParser(JLSLevel);
        parser.setSource(cu.getContents());
        parser.setResolveBindings(resolveBindings);
        parser.setStatementsRecovery(statementsRecovery);
        parser.setCompilerOptions(JavaCore.getOptions());
        parser.setNameEnvironment(new FileSystem(new String[]{System.getProperty("java.home") + "/lib/rt.jar"}, null, "UTF-8"));
        return (CompilationUnit)parser.createAST();
    }

    protected String evaluateRewrite(ICompilationUnit cu, ASTRewrite rewrite) throws Exception {
        Document document1 = new DocumentImpl(new String(cu.getContents()));
        TextEdit res = rewrite.rewriteAST(document1, JavaCore.getOptions());
        res.apply(document1);
        String content1 = document1.get();

        //      Document document2= new Document(String.valueOf(cu.getContents()));
        //      TextEdit res2= rewrite.rewriteAST(document2, JavaCore.getOptions());
        //      res2.apply(document2);
        //      String content2= document2.get();
        //
        //      assertEquals(content1, content2);

        return content1;
    }

    public static void assertEqualString(String actual, String expected) {
        StringAsserts.assertEqualString(actual, expected);
    }

    public static TypeDeclaration findTypeDeclaration(CompilationUnit astRoot, String simpleTypeName) {
        return (TypeDeclaration)findAbstractTypeDeclaration(astRoot, simpleTypeName);
    }

    public static AbstractTypeDeclaration findAbstractTypeDeclaration(CompilationUnit astRoot, String simpleTypeName) {
        List types = astRoot.types();
        for (int i = 0; i < types.size(); i++) {
            AbstractTypeDeclaration elem = (AbstractTypeDeclaration)types.get(i);
            if (simpleTypeName.equals(elem.getName().getIdentifier())) {
                return elem;
            }
        }
        return null;
    }

    public static MethodDeclaration findMethodDeclaration(TypeDeclaration typeDecl, String methodName) {
        MethodDeclaration[] methods = typeDecl.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methodName.equals(methods[i].getName().getIdentifier())) {
                return methods[i];
            }
        }
        return null;
    }

    public static SingleVariableDeclaration createNewParam(AST ast, String name) {
        SingleVariableDeclaration newParam = ast.newSingleVariableDeclaration();
        newParam.setType(ast.newPrimitiveType(PrimitiveType.FLOAT));
        newParam.setName(ast.newSimpleName(name));
        return newParam;
    }

    /** @deprecated using deprecated code */
    private void setModifiers(BodyDeclaration bodyDeclaration, int modifiers) {
        bodyDeclaration.setModifiers(modifiers);
    }

    /** @deprecated using deprecated code */
    private void setReturnType(MethodDeclaration methodDeclaration, Type type) {
        methodDeclaration.setReturnType(type);
    }

    protected FieldDeclaration createNewField(AST ast, String name) {
        VariableDeclarationFragment frag = ast.newVariableDeclarationFragment();
        frag.setName(ast.newSimpleName(name));
        FieldDeclaration newFieldDecl = ast.newFieldDeclaration(frag);
        if (ast.apiLevel() == AST_INTERNAL_JLS2) {
            setModifiers(newFieldDecl, Modifier.PRIVATE);
        } else {
            newFieldDecl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
        }
        newFieldDecl.setType(ast.newPrimitiveType(PrimitiveType.DOUBLE));
        return newFieldDecl;
    }

    protected MethodDeclaration createNewMethod(AST ast, String name, boolean isAbstract) {
        MethodDeclaration decl = ast.newMethodDeclaration();
        decl.setName(ast.newSimpleName(name));
        if (ast.apiLevel() == AST_INTERNAL_JLS2) {
            setModifiers(decl, isAbstract ? (Modifier.ABSTRACT | Modifier.PRIVATE) : Modifier.PRIVATE);
            setReturnType(decl, ast.newPrimitiveType(PrimitiveType.VOID));
        } else {
            decl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
            if (isAbstract) {
                decl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
            }
            decl.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
        }
        SingleVariableDeclaration param = ast.newSingleVariableDeclaration();
        param.setName(ast.newSimpleName("str"));
        param.setType(ast.newSimpleType(ast.newSimpleName("String")));
        decl.parameters().add(param);
        decl.setBody(isAbstract ? null : ast.newBlock());
        return decl;
    }

}
