/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.java.client.core;

import com.codenvy.ide.java.client.BaseTest;
import com.codenvy.ide.java.client.core.dom.AST;
import com.codenvy.ide.java.client.core.dom.ASTNode;
import com.codenvy.ide.java.client.core.dom.ASTParser;
import com.codenvy.ide.java.client.core.dom.CompilationUnit;
import com.codenvy.ide.java.client.internal.codeassist.ISearchRequestor;
import com.codenvy.ide.java.client.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.java.client.internal.compiler.env.NameEnvironmentAnswer;
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

        /** @see com.codenvy.ide.java.client.internal.compiler.env.INameEnvironment#findType(char[][]) */
        @Override
        public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
            // TODO Auto-generated method stub
            return null;
        }

        /** @see com.codenvy.ide.java.client.internal.compiler.env.INameEnvironment#findType(char[], char[][]) */
        @Override
        public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
            // TODO Auto-generated method stub
            return null;
        }

        /** @see com.codenvy.ide.java.client.internal.compiler.env.INameEnvironment#isPackage(char[][], char[]) */
        @Override
        public boolean isPackage(char[][] parentPackageName, char[] packageName) {
            // TODO Auto-generated method stub
            return false;
        }

        /** @see com.codenvy.ide.java.client.internal.compiler.env.INameEnvironment#cleanup() */
        @Override
        public void cleanup() {
            // TODO Auto-generated method stub

        }

        /**
         * @see com.codenvy.ide.java.client.internal.compiler.env.INameEnvironment#findTypes(char[], boolean, boolean, int,
         *      com.codenvy.ide.java.client.internal.codeassist.ISearchRequestor, com.codenvy.ide.java.client.runtime.IProgressMonitor)
         */
        @Override
        public void findTypes(char[] qualifiedName, boolean b, boolean camelCaseMatch, int searchFor,
                              final ISearchRequestor requestor) {
        }

        /**
         * @see com.codenvy.ide.java.client.internal.compiler.env.INameEnvironment#findPackages(char[],
         *      com.codenvy.ide.java.client.internal.codeassist.ISearchRequestor)
         */
        @Override
        public void findPackages(char[] qualifiedName, ISearchRequestor requestor) {
        }

        /**
         * @see com.codenvy.ide.java.client.internal.compiler.env.INameEnvironment#findConstructorDeclarations(char[], boolean,
         *      com.codenvy.ide.java.client.internal.codeassist.ISearchRequestor, com.codenvy.ide.java.client.runtime.IProgressMonitor)
         */
        @Override
        public void findConstructorDeclarations(char[] prefix, boolean camelCaseMatch, final ISearchRequestor requestor) {
        }

        /**
         * @see com.codenvy.ide.java.client.internal.compiler.env.INameEnvironment#findExactTypes(char[], boolean, int,
         *      com.codenvy.ide.java.client.internal.codeassist.ISearchRequestor)
         */
        @Override
        public void findExactTypes(char[] missingSimpleName, boolean b, int type, ISearchRequestor storage) {
        }

    }

}