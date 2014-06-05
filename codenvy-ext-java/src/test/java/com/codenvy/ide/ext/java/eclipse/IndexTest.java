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

import com.codenvy.api.project.server.Project;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.internal.codeassist.impl.AssistOptions;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.ext.java.server.core.search.IJavaSearchConstants;
import com.codenvy.ide.ext.java.server.core.search.IJavaSearchScope;
import com.codenvy.ide.ext.java.server.core.search.SearchPattern;
import com.codenvy.ide.ext.java.server.internal.core.JavaProject;
import com.codenvy.ide.ext.java.server.internal.core.search.BasicSearchEngine;
import com.codenvy.ide.ext.java.server.internal.core.search.IRestrictedAccessConstructorRequestor;
import com.codenvy.ide.ext.java.server.internal.core.search.IRestrictedAccessTypeRequestor;
import com.codenvy.ide.ext.java.server.internal.core.search.indexing.IndexManager;
import com.codenvy.ide.ext.java.server.internal.core.search.processing.JobManager;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;

/**
 * @author Evgen Vidolob
 */
public class IndexTest extends BaseProjectTest {

    protected IJavaSearchScope searchScope;

    static JavaProject javaProject;
    private static Map<String, String> options = new HashMap<>();

    static {
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
        options.put(JavaCore.CORE_ENCODING, "UTF-8");
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
        options.put(CompilerOptions.OPTION_TargetPlatform, JavaCore.VERSION_1_7);
        options.put(AssistOptions.OPTION_PerformVisibilityCheck, AssistOptions.ENABLED);
        options.put(CompilerOptions.OPTION_ReportUnusedLocal, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_TaskTags, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_ReportUnusedPrivateMember, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_SuppressWarnings, CompilerOptions.DISABLED);
        options.put(JavaCore.COMPILER_TASK_TAGS, "TODO,FIXME,XXX");
        options.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE, JavaCore.ENABLED);
        options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
        options.put(CompilerOptions.OPTION_Process_Annotations, JavaCore.DISABLED);
    }

    @BeforeClass
    public static void createProject() throws InterruptedException {
        Project project = pm.getProject(MY_WORKSPACE_ID, "/project");
        IndexManager.VERBOSE = true;
        IndexManager.DEBUG = true;
        JobManager.VERBOSE = true;
        javaProject = new JavaProject(project, TEMP_DIR, pm, MY_WORKSPACE_ID, new HashMap<>(options));
    }

    @Test
    public void testTypeFind() throws Exception {

        int matchRule = SearchPattern.R_PREFIX_MATCH;
        int searchFor = IJavaSearchConstants.TYPE;
        char[] qualification = "java.util".toCharArray();
        char[] simpleName = "Map".toCharArray();
        final List<String> types = new ArrayList<>();
        IRestrictedAccessTypeRequestor typeRequestor = new IRestrictedAccessTypeRequestor() {
            public void acceptType(int modifiers, char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path,
                                   AccessRestriction access) {
//                if (excludePath != null && excludePath.equals(path))
//                    return;
//                if (!findMembers && enclosingTypeNames != null && enclosingTypeNames.length > 0)
//                    return; // accept only top level types
//                storage.acceptType(packageName, simpleTypeName, enclosingTypeNames, modifiers, access);

                types.add(new String(packageName) + new String(simpleTypeName));
            }
        };
//        Thread.sleep(10000);

        // indexes were already there, so perform an immediate search to avoid any index rebuilt
        new BasicSearchEngine(javaProject.getIndexManager()).searchAllTypeNames(
                qualification,
                SearchPattern.R_EXACT_MATCH,
                simpleName,
                matchRule, // not case sensitive
                searchFor,
                getSearchScope(javaProject),
                typeRequestor,
                IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
                new NullProgressMonitor());
        assertFalse(types.isEmpty());
    }

    @Test
    public void testTypeFindByPrefix() throws Exception {

        int matchRule = SearchPattern.R_PREFIX_MATCH;
        int searchFor = IJavaSearchConstants.TYPE;
        char[] prefix = "Map".toCharArray();
        boolean camelCaseMatch = true;
        int lastDotIndex = CharOperation.lastIndexOf('.', prefix);
        char[] qualification, simpleName;
        if (lastDotIndex < 0) {
            qualification = null;
            if (camelCaseMatch) {
                simpleName = prefix;
            } else {
                simpleName = CharOperation.toLowerCase(prefix);
            }
        } else {
            qualification = CharOperation.subarray(prefix, 0, lastDotIndex);
            if (camelCaseMatch) {
                simpleName = CharOperation.subarray(prefix, lastDotIndex + 1, prefix.length);
            } else {
                simpleName =
                        CharOperation.toLowerCase(
                                CharOperation.subarray(prefix, lastDotIndex + 1, prefix.length));
            }
        }
        final List<String> types = new ArrayList<>();
        IRestrictedAccessTypeRequestor typeRequestor = new IRestrictedAccessTypeRequestor() {
            public void acceptType(int modifiers, char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path,
                                   AccessRestriction access) {
//                if (excludePath != null && excludePath.equals(path))
//                    return;
//                if (!findMembers && enclosingTypeNames != null && enclosingTypeNames.length > 0)
//                    return; // accept only top level types
//                storage.acceptType(packageName, simpleTypeName, enclosingTypeNames, modifiers, access);
                System.out.println(
                        "modifiers = [" + modifiers + "], packageName = [" + packageName + "], simpleTypeName = [" + simpleTypeName +
                        "], enclosingTypeNames = [" + enclosingTypeNames + "], path = [" + path + "], access = [" + access + "]");
                types.add(new String(packageName) + new String(simpleTypeName));
            }
        };
//        Thread.sleep(10000);

        // indexes were already there, so perform an immediate search to avoid any index rebuilt
        new BasicSearchEngine(javaProject.getIndexManager()).searchAllTypeNames(
                qualification,
                SearchPattern.R_EXACT_MATCH,
                simpleName,
                matchRule, // not case sensitive
                searchFor,
                getSearchScope(javaProject),
                typeRequestor,
                IJavaSearchConstants.FORCE_IMMEDIATE_SEARCH,
                new NullProgressMonitor());
        assertFalse(types.isEmpty());
    }

    @Test
    public void testSourceTypeFind() throws Exception {

        int matchRule = SearchPattern.R_PREFIX_MATCH;
        int searchFor = IJavaSearchConstants.TYPE;
        char[] qualification = "com.codenvy.test".toCharArray();
        char[] simpleName = "My".toCharArray();
        final List<String> types = new ArrayList<>();
        IRestrictedAccessTypeRequestor typeRequestor = new IRestrictedAccessTypeRequestor() {
            public void acceptType(int modifiers, char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path,
                                   AccessRestriction access) {
                System.out.println(
                        "modifiers = [" + modifiers + "], packageName = [" + packageName + "], simpleTypeName = [" + simpleTypeName +
                        "], enclosingTypeNames = [" + enclosingTypeNames + "], path = [" + path + "], access = [" + access + "]");
                types.add(new String(packageName) + "." + new String(simpleTypeName));
            }
        };

        // indexes were already there, so perform an immediate search to avoid any index rebuilt
        new BasicSearchEngine(javaProject.getIndexManager()).searchAllTypeNames(
                qualification,
                SearchPattern.R_EXACT_MATCH,
                simpleName,
                matchRule, // not case sensitive
                searchFor,
                getSearchScope(javaProject),
                typeRequestor,
                IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
                new NullProgressMonitor());

        assertFalse(types.isEmpty());
    }

    @Test
    public void testFindConstructor() throws Exception {
        int matchRule = SearchPattern.R_PREFIX_MATCH;
        char[] prefix = "Ha".toCharArray();
        boolean camelCaseMatch = false;
        int lastDotIndex = CharOperation.lastIndexOf('.', prefix);
        char[] qualification, simpleName;
        if (lastDotIndex < 0) {
            qualification = null;
            if (camelCaseMatch) {
                simpleName = prefix;
            } else {
                simpleName = CharOperation.toLowerCase(prefix);
            }
        } else {
            qualification = CharOperation.subarray(prefix, 0, lastDotIndex);
            if (camelCaseMatch) {
                simpleName = CharOperation.subarray(prefix, lastDotIndex + 1, prefix.length);
            } else {
                simpleName =
                        CharOperation.toLowerCase(
                                CharOperation.subarray(prefix, lastDotIndex + 1, prefix.length));
            }
        }
        final List<String> types = new ArrayList<>();
        IRestrictedAccessConstructorRequestor constructorRequestor = new IRestrictedAccessConstructorRequestor() {
            public void acceptConstructor(
                    int modifiers,
                    char[] simpleTypeName,
                    int parameterCount,
                    char[] signature,
                    char[][] parameterTypes,
                    char[][] parameterNames,
                    int typeModifiers,
                    char[] packageName,
                    int extraFlags,
                    String path,
                    AccessRestriction access) {
                types.add(new String(packageName) + "." + new String(simpleTypeName));
            }
        };
        new BasicSearchEngine(javaProject.getIndexManager()).searchAllConstructorDeclarations(
                qualification,
                simpleName,
                matchRule,
                getSearchScope(javaProject),
                constructorRequestor,
                IJavaSearchConstants.FORCE_IMMEDIATE_SEARCH,
                new NullProgressMonitor());
        assertFalse(types.isEmpty());
    }

    private IJavaSearchScope getSearchScope(IJavaProject project) {
        if (this.searchScope == null) {
            // Create search scope with visible entry on the project's classpath
//            if(this.checkAccessRestrictions) {
            this.searchScope = BasicSearchEngine.createJavaSearchScope(new IJavaElement[]{project});
//            } else {
//                this.searchScope = BasicSearchEngine.createJavaSearchScope(this.nameLookup.packageFragmentRoots);
//            }
        }
        return this.searchScope;
    }
}
