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

import com.codenvy.ide.ext.java.client.BaseTest;
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTParser;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.codeassist.ISearchRequestor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.NameEnvironmentAnswer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

import org.junit.Before;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 12, 2012 3:23:29 PM evgen $
 */
public abstract class ParserBaseTest extends BaseTest {

    protected CompilationUnit unit;

    protected char[] javaFiles;

    protected interface Resources extends ClientBundle {
        @Source("CreateJavaClassPresenter.txt")
        TextResource resource();
    }


    @Before
    public void gwtSetUp() {
        Resources rs = GWT.create(Resources.class);
        javaFiles = rs.resource().getText().toCharArray();

        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setUnitName("/P/org/test/CreateJavaClassPresenter.java");
        parser.setSource(javaFiles);
        parser.setNameEnvironment(new MockNameEnv());
        ASTNode ast = parser.createAST();
        unit = (CompilationUnit)ast;

//      new JavaCodeAssistantService(null, null);
    }

    private class MockNameEnv implements INameEnvironment {

        /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment#findType(char[][]) */
        @Override
        public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
            // TODO Auto-generated method stub
            return null;
        }

        /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment#findType(char[], char[][]) */
        @Override
        public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
            // TODO Auto-generated method stub
            return null;
        }

        /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment#isPackage(char[][], char[]) */
        @Override
        public boolean isPackage(char[][] parentPackageName, char[] packageName) {
            // TODO Auto-generated method stub
            return false;
        }

        /** @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment#cleanup() */
        @Override
        public void cleanup() {
            // TODO Auto-generated method stub

        }

        /**
         * @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment#findTypes(char[], boolean, boolean, int,
         *      com.codenvy.ide.ext.java.jdt.internal.codeassist.ISearchRequestor, com.codenvy.ide.ext.java.jdt.runtime.IProgressMonitor)
         */
        @Override
        public void findTypes(char[] qualifiedName, boolean b, boolean camelCaseMatch, int searchFor,
                              final ISearchRequestor requestor) {
        }

        /**
         * @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment#findPackages(char[],
         *      com.codenvy.ide.ext.java.jdt.internal.codeassist.ISearchRequestor)
         */
        @Override
        public void findPackages(char[] qualifiedName, ISearchRequestor requestor) {
        }

        /**
         * @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment#findConstructorDeclarations(char[], boolean,
         *      com.codenvy.ide.ext.java.jdt.internal.codeassist.ISearchRequestor, com.codenvy.ide.ext.java.jdt.runtime.IProgressMonitor)
         */
        @Override
        public void findConstructorDeclarations(char[] prefix, boolean camelCaseMatch, final ISearchRequestor requestor) {
        }

        /**
         * @see com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment#findExactTypes(char[], boolean, int,
         *      com.codenvy.ide.ext.java.jdt.internal.codeassist.ISearchRequestor)
         */
        @Override
        public void findExactTypes(char[] missingSimpleName, boolean b, int type, ISearchRequestor storage) {
        }

    }

}