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

/**
 * @author Evgen Vidolob
 */
public class CompilationUnitResolverTest extends BaseProjectTest {


//
//    @Test
//    public void testParse() throws Exception {
//        VirtualFileImpl file = mountPoint.getVirtualFile("/project");
//        JavaProject project = new JavaProject(file, TEMP_DIR);
//        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(project, null);
//        char[][] packages = new char[][]{"com".toCharArray(), "codenvy".toCharArray(),"test".toCharArray()};
//        NameEnvironmentAnswer answer = environment.findType("MyClass".toCharArray(), packages);
//        assertNotNull(answer);
//        ICompilationUnit compilationUnit = answer.getCompilationUnit();
//        assertNotNull(compilationUnit);
//        int flags = 0;
//        flags |= org.eclipse.jdt.core.ICompilationUnit.ENABLE_STATEMENTS_RECOVERY;
//        flags |= org.eclipse.jdt.core.ICompilationUnit.IGNORE_METHOD_BODIES;
//        flags |= org.eclipse.jdt.core.ICompilationUnit.ENABLE_BINDINGS_RECOVERY;
//        CompilationUnitDeclaration compilationUnitDeclaration =
//                CodenvyCompilationUnitResolver.resolve(compilationUnit, project, environment, new HashMap<String, String>(), flags, null);
//        CompilationUnit result = CodenvyCompilationUnitResolver.convert(
//                compilationUnitDeclaration,
//                compilationUnit.getContents(),
//                flags,
//                new HashMap()
//                );
//        AbstractTypeDeclaration o = (AbstractTypeDeclaration)result.types().get(0);
//        ITypeBinding typeBinding = o.resolveBinding();
//        Map<TypeBinding,?> bindings = (Map<TypeBinding, ?>)result.getProperty("compilerBindingsToASTBindings");
//        SourceTypeBinding binding = null;
//        for (Map.Entry<TypeBinding, ?> entry : bindings.entrySet()) {
//            if(entry.getValue().equals(typeBinding)){
//                binding = (SourceTypeBinding)entry.getKey();
//                break;
//            }
//        }
//        assertNotNull(binding);
//    }
}
