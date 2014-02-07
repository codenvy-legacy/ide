/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.eclipse;


import com.codenvy.ide.ext.java.server.core.search.IJavaSearchConstants;
import com.codenvy.ide.ext.java.server.core.search.SearchEngine;
import com.codenvy.ide.ext.java.server.core.search.SearchMatch;
import com.codenvy.ide.ext.java.server.core.search.SearchParticipant;
import com.codenvy.ide.ext.java.server.core.search.SearchPattern;
import com.codenvy.ide.ext.java.server.core.search.SearchRequestor;
import com.codenvy.ide.ext.java.server.internal.core.search.JavaSearchScope;
import com.codenvy.ide.ext.java.server.internal.core.search.indexing.IndexManager;
import com.codenvy.ide.ext.java.server.internal.core.search.processing.JobManager;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * @author Evgen Vidolob
 */
public class EclipseParserTest {

//    @Test
//    public void testParser() throws Exception {
//        IndexingParser parser =
//                new IndexingParser(new SourceElementRequestorAdapter(), new DefaultProblemFactory(), new CompilerOptions(), false, false,
//                                   false);
//        StringBuilder b = new StringBuilder("package com.codenvy.test;\n");
//        b.append("public class MyClass {\n");
//        b.append("public String aa;\n");
//        b.append("}");
////        ICompilationUnit unit = new CompilationUnit(b.toString().toCharArray(),"MyClass.java","UTF-8");
////        CompilationResult result = new CompilationResult(unit, 0, 0, 100);
////        CompilationUnitDeclaration unitDeclaration = parser.parse(unit, result);
////        unitDeclaration.resolve();
//        ASTParser astParser = ASTParser.newParser(AST.JLS4);
//        astParser.setBindingsRecovery(true);
//        astParser.setResolveBindings(true);
//        astParser.setUnitName("com/codenvy/test/MyClass.java");
//        astParser.setSource(b.toString().toCharArray());
//        astParser.setEnvironment(new String[0],new String[0],new String[0], true);
//        ASTNode ast = astParser.createAST(new NullProgressMonitor());
//        org.eclipse.jdt.core.dom.CompilationUnit compilationUnit = (org.eclipse.jdt.core.dom.CompilationUnit)ast;
//        //astParser.setEnvironment();
//        AbstractTypeDeclaration o = (AbstractTypeDeclaration)compilationUnit.types().get(0);
//        System.out.println(o.resolveBinding().toString());
//    }


    @Test
    public void testJarIndex() throws Exception {
        IndexManager indexManager = new IndexManager();
        JobManager.VERBOSE = true;
        Thread thread = new Thread(indexManager,"index thread");
        indexManager.reset();
        thread.start();
        Path path = new Path("/home/evgen/tmp/gwt-user.jar");
        indexManager.indexLibrary(path,null/* new URL("file://home/evgen/tmp/indexes/gwt.index")*/);
        indexManager.saveIndexes();
//        Thread.sleep(5000);

        // step 1: Create a search pattern
        // search methods having &quot;abcde&quot; as name
        SearchPattern pattern = SearchPattern.createPattern("com.google.gwt.core.client.GWT",
        IJavaSearchConstants.TYPE, IJavaSearchConstants.CLASS,
                SearchPattern.R_EXACT_MATCH);

        // step 2: Create search scope
        // IJavaSearchScope scope = SearchEngine.createJavaSearchScope(packages);
        JavaSearchScope scope =  new JavaSearchScope(); //SearchEngine.createWorkspaceScope();
        scope.addEnclosingProjectOrJar(path);

        // step3: define a result collector
        SearchRequestor requestor = new SearchRequestor() {
            public void acceptSearchMatch(SearchMatch match) {
                System.out.println(match.getElement());
            }
        };

        // step4: start searching
        SearchEngine searchEngine = new SearchEngine();
//        try {
            searchEngine
                    .search(pattern, new SearchParticipant[] { SearchEngine
                                                                       .getDefaultSearchParticipant() }, scope, requestor,
                            null /* progress monitor is not used here */);
//        } catch (CoreException e) {
//            e.printStackTrace();
//        }


    }
}
