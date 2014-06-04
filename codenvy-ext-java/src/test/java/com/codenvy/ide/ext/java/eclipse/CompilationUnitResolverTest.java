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
