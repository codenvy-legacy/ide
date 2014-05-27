/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.server.internal.core.search.matching;

import com.codenvy.ide.ext.java.server.core.search.IJavaSearchConstants;
import com.codenvy.ide.ext.java.server.core.search.IJavaSearchScope;
import com.codenvy.ide.ext.java.server.core.search.SearchPattern;
import com.codenvy.ide.ext.java.server.internal.core.ClasspathEntry;
import com.codenvy.ide.ext.java.server.internal.core.JavaModelManager;
import com.codenvy.ide.ext.java.server.internal.core.JavaProject;
import com.codenvy.ide.ext.java.server.internal.core.PackageFragmentRoot;
import com.codenvy.ide.ext.java.server.internal.core.builder.ClasspathDirectory;
import com.codenvy.ide.ext.java.server.internal.core.builder.ClasspathJar;
import com.codenvy.ide.ext.java.server.internal.core.builder.CodenvyClasspathLocation;
import com.codenvy.ide.ext.java.server.internal.core.search.BasicSearchEngine;
import com.codenvy.ide.ext.java.server.internal.core.search.IRestrictedAccessConstructorRequestor;
import com.codenvy.ide.ext.java.server.internal.core.search.IRestrictedAccessTypeRequestor;
import com.codenvy.ide.ext.java.server.internal.core.search.indexing.IndexManager;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.codeassist.ISearchRequestor;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.core.INameEnvironmentWithProgress;
import org.eclipse.jdt.internal.core.NameLookup;
import org.eclipse.jdt.internal.core.util.Util;

import java.io.File;
import java.util.HashMap;
import java.util.zip.ZipFile;

/*
 * A name environment based on the classpath of a Java project.
 */
public class JavaSearchNameEnvironment implements INameEnvironment, SuffixConstants, INameEnvironmentWithProgress {

    CodenvyClasspathLocation[] locations;

    /*
     * A map from the fully qualified slash-separated name of the main type (String) to the working copy
     */
    HashMap workingCopies;
    private JavaProject                                                  javaProject;
    private com.codenvy.ide.ext.java.server.core.search.IJavaSearchScope searchScope;

    public JavaSearchNameEnvironment(JavaProject javaProject, org.eclipse.jdt.core.ICompilationUnit[] copies) {
        this.javaProject = javaProject;
        computeClasspathLocations(javaProject);
        try {
            int length = copies == null ? 0 : copies.length;
            this.workingCopies = new HashMap(length);
            if (copies != null) {
                for (int i = 0; i < length; i++) {
                    org.eclipse.jdt.core.ICompilationUnit workingCopy = copies[i];
                    IPackageDeclaration[] pkgs = workingCopy.getPackageDeclarations();
                    String pkg = pkgs.length > 0 ? pkgs[0].getElementName() : ""; //$NON-NLS-1$
                    String cuName = workingCopy.getElementName();
                    String mainTypeName = Util.getNameWithoutJavaLikeExtension(cuName);
                    String qualifiedMainTypeName = pkg.length() == 0 ? mainTypeName : pkg.replace('.', '/') + '/' + mainTypeName;
                    this.workingCopies.put(qualifiedMainTypeName, workingCopy);
                }
            }
        } catch (JavaModelException e) {
            // working copy doesn't exist: cannot happen
        }
    }

    public void cleanup() {
        for (CodenvyClasspathLocation location : this.locations) {
            location.cleanup();
        }
    }

    /**
     * reset only source locations
     */
    public void reset() {
        for (CodenvyClasspathLocation location : this.locations) {
            if (location instanceof ClasspathSourceDirectory)
                location.cleanup();
        }
    }


    private void computeClasspathLocations(JavaProject javaProject) {

        IPackageFragmentRoot[] roots = null;
        try {
            roots = javaProject.getAllPackageFragmentRoots();
        } catch (JavaModelException e) {
            // project doesn't exist
            this.locations = new CodenvyClasspathLocation[0];
            return;
        }
        int length = roots.length;
        CodenvyClasspathLocation[] cpLocations = new CodenvyClasspathLocation[length];
        int index = 0;
        JavaModelManager manager = JavaModelManager.getJavaModelManager();
        for (int i = 0; i < length; i++) {
            PackageFragmentRoot root = (PackageFragmentRoot)roots[i];
            IPath path = root.getPath();
            try {
                if (root.isArchive()) {
                    ZipFile zipFile = manager.getZipFile(path);
                    cpLocations[index++] = new ClasspathJar(zipFile, ((ClasspathEntry)root.getRawClasspathEntry()).getAccessRuleSet());
                } else {
                    Object target = JavaModelManager.getTarget(path, true);
                    if (target == null) {
                        // target doesn't exist any longer
                        // just resize cpLocations
                        System.arraycopy(cpLocations, 0, cpLocations = new CodenvyClasspathLocation[cpLocations.length - 1], 0, index);
                    } else if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
                        cpLocations[index++] = new ClasspathSourceDirectory((File)target, root.fullExclusionPatternChars(),
                                                                            root.fullInclusionPatternChars());
                    } else {
                        cpLocations[index++] = new ClasspathDirectory((IContainer)target, false,
                                                 ((ClasspathEntry)root.getRawClasspathEntry()).getAccessRuleSet());
                    }
                }
            } catch (CoreException e1) {
                // problem opening zip file or getting root kind
                // consider root corrupt and ignore
                // just resize cpLocations
                System.arraycopy(cpLocations, 0, cpLocations = new CodenvyClasspathLocation[cpLocations.length - 1], 0, index);
            }
        }
        this.locations = cpLocations;
    }

    private NameEnvironmentAnswer findClass(String qualifiedTypeName, char[] typeName) {
        String
                binaryFileName = null, qBinaryFileName = null,
                sourceFileName = null, qSourceFileName = null,
                qPackageName = null;
        NameEnvironmentAnswer suggestedAnswer = null;
        for (int i = 0, length = this.locations.length; i < length; i++) {
            CodenvyClasspathLocation location = this.locations[i];
            NameEnvironmentAnswer answer;
            if (location instanceof ClasspathSourceDirectory) {
                if (sourceFileName == null) {
                    qSourceFileName = qualifiedTypeName; // doesn't include the file extension
                    sourceFileName = qSourceFileName;
                    qPackageName = ""; //$NON-NLS-1$
                    if (qualifiedTypeName.length() > typeName.length) {
                        int typeNameStart = qSourceFileName.length() - typeName.length;
                        qPackageName = qSourceFileName.substring(0, typeNameStart - 1);
                        sourceFileName = qSourceFileName.substring(typeNameStart);
                    }
                }
                ICompilationUnit workingCopy = (ICompilationUnit)this.workingCopies.get(qualifiedTypeName);
                if (workingCopy != null) {
                    answer = new NameEnvironmentAnswer(workingCopy, null /*no access restriction*/);
                } else {
                    answer = location.findClass(
                            sourceFileName, // doesn't include the file extension
                            qPackageName,
                            qSourceFileName);  // doesn't include the file extension
                }
            } else {
                if (binaryFileName == null) {
                    qBinaryFileName = qualifiedTypeName + SUFFIX_STRING_class;
                    binaryFileName = qBinaryFileName;
                    qPackageName = ""; //$NON-NLS-1$
                    if (qualifiedTypeName.length() > typeName.length) {
                        int typeNameStart = qBinaryFileName.length() - typeName.length - 6; // size of ".class"
                        qPackageName = qBinaryFileName.substring(0, typeNameStart - 1);
                        binaryFileName = qBinaryFileName.substring(typeNameStart);
                    }
                }
                answer =
                        location.findClass(
                                binaryFileName,
                                qPackageName,
                                qBinaryFileName);
            }
            if (answer != null) {
                if (!answer.ignoreIfBetter()) {
                    if (answer.isBetter(suggestedAnswer))
                        return answer;
                } else if (answer.isBetter(suggestedAnswer))
                    // remember suggestion and keep looking
                    suggestedAnswer = answer;
            }
        }
        if (suggestedAnswer != null)
            // no better answer was found
            return suggestedAnswer;
        return null;
    }

    public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
        if (typeName != null)
            return findClass(
                    new String(CharOperation.concatWith(packageName, typeName, '/')),
                    typeName);
        return null;
    }

    public NameEnvironmentAnswer findType(char[][] compoundName) {
        if (compoundName != null)
            return findClass(
                    new String(CharOperation.concatWith(compoundName, '/')),
                    compoundName[compoundName.length - 1]);
        return null;
    }

    public boolean isPackage(char[][] compoundName, char[] packageName) {
        return isPackage(new String(CharOperation.concatWith(compoundName, packageName, '/')));
    }

    public boolean isPackage(String qualifiedPackageName) {
        for (CodenvyClasspathLocation location : this.locations)
            if (location.isPackage(qualifiedPackageName))
                return true;
        return false;
    }

    private static int convertSearchFilterToModelFilter(int searchFilter) {
        switch (searchFilter) {
            case org.eclipse.jdt.core.search.IJavaSearchConstants.CLASS:
                return NameLookup.ACCEPT_CLASSES;
            case org.eclipse.jdt.core.search.IJavaSearchConstants.INTERFACE:
                return NameLookup.ACCEPT_INTERFACES;
            case org.eclipse.jdt.core.search.IJavaSearchConstants.ENUM:
                return NameLookup.ACCEPT_ENUMS;
            case org.eclipse.jdt.core.search.IJavaSearchConstants.ANNOTATION_TYPE:
                return NameLookup.ACCEPT_ANNOTATIONS;
            case org.eclipse.jdt.core.search.IJavaSearchConstants.CLASS_AND_ENUM:
                return NameLookup.ACCEPT_CLASSES | NameLookup.ACCEPT_ENUMS;
            case org.eclipse.jdt.core.search.IJavaSearchConstants.CLASS_AND_INTERFACE:
                return NameLookup.ACCEPT_CLASSES | NameLookup.ACCEPT_INTERFACES;
            default:
                return NameLookup.ACCEPT_ALL;
        }
    }

    /**
     * Find the top-level types that are defined
     * in the current environment and whose name starts with the
     * given prefix. The prefix is a qualified name separated by periods
     * or a simple name (ex. java.util.V or V).
     *
     * The types found are passed to one of the following methods (if additional
     * information is known about the types):
     *    ISearchRequestor.acceptType(char[][] packageName, char[] typeName)
     *    ISearchRequestor.acceptClass(char[][] packageName, char[] typeName, int modifiers)
     *    ISearchRequestor.acceptInterface(char[][] packageName, char[] typeName, int modifiers)
     *
     * This method can not be used to find member types... member
     * types are found relative to their enclosing type.
     */
    public void findTypes(char[] prefix, final boolean findMembers, boolean camelCaseMatch, int searchFor, final ISearchRequestor storage) {
        findTypes(prefix, findMembers, camelCaseMatch, searchFor, storage, null);
    }

    /**
     * Must be used only by CompletionEngine.
     * The progress monitor is used to be able to cancel completion operations
     *
     * Find the top-level types that are defined
     * in the current environment and whose name starts with the
     * given prefix. The prefix is a qualified name separated by periods
     * or a simple name (ex. java.util.V or V).
     *
     * The types found are passed to one of the following methods (if additional
     * information is known about the types):
     *    ISearchRequestor.acceptType(char[][] packageName, char[] typeName)
     *    ISearchRequestor.acceptClass(char[][] packageName, char[] typeName, int modifiers)
     *    ISearchRequestor.acceptInterface(char[][] packageName, char[] typeName, int modifiers)
     *
     * This method can not be used to find member types... member
     * types are found relative to their enclosing type.
     */
    public void findTypes(char[] prefix, final boolean findMembers, boolean camelCaseMatch, int searchFor, final ISearchRequestor storage, IProgressMonitor monitor) {
        try {
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

            IProgressMonitor progressMonitor = new IProgressMonitor() {
                boolean isCanceled = false;
                public void beginTask(String name, int totalWork) {
                    // implements interface method
                }
                public void done() {
                    // implements interface method
                }
                public void internalWorked(double work) {
                    // implements interface method
                }
                public boolean isCanceled() {
                    return this.isCanceled;
                }
                public void setCanceled(boolean value) {
                    this.isCanceled = value;
                }
                public void setTaskName(String name) {
                    // implements interface method
                }
                public void subTask(String name) {
                    // implements interface method
                }
                public void worked(int work) {
                    // implements interface method
                }
            };
            IRestrictedAccessTypeRequestor typeRequestor = new IRestrictedAccessTypeRequestor() {
                public void acceptType(int modifiers, char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path, AccessRestriction access) {
//                    if (excludePath != null && excludePath.equals(path))
//                        return;
                    if (!findMembers && enclosingTypeNames != null && enclosingTypeNames.length > 0)
                        return; // accept only top level types
                    storage.acceptType(packageName, simpleTypeName, enclosingTypeNames, modifiers, access);
                }
            };

            int matchRule = SearchPattern.R_PREFIX_MATCH;
            if (camelCaseMatch) matchRule |= SearchPattern.R_CAMELCASE_MATCH;
            IndexManager indexManager = javaProject.getIndexManager();
            if (monitor != null) {
                if (indexManager.awaitingJobsCount() == 0) {
                    // indexes were already there, so perform an immediate search to avoid any index rebuilt
                    new BasicSearchEngine(indexManager).searchAllTypeNames(
                            qualification,
                            SearchPattern.R_EXACT_MATCH,
                            simpleName,
                            matchRule, // not case sensitive
                            searchFor,
                            getSearchScope(),
                            typeRequestor,
                            IJavaSearchConstants.FORCE_IMMEDIATE_SEARCH,
                            progressMonitor);
                } else {
                    // indexes were not ready, give the indexing a chance to finish small jobs by sleeping 100ms...
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }
                    if (indexManager.awaitingJobsCount() == 0) {
                        // indexes are now ready, so perform an immediate search to avoid any index rebuilt
                        new BasicSearchEngine(indexManager).searchAllTypeNames(
                                qualification,
                                SearchPattern.R_EXACT_MATCH,
                                simpleName,
                                matchRule, // not case sensitive
                                searchFor,
                                getSearchScope(),
                                typeRequestor,
                                IJavaSearchConstants.FORCE_IMMEDIATE_SEARCH,
                                progressMonitor);
                    }
                    //else {
                        // Indexes are still not ready, so look for types in the model instead of a search request
//                        findTypes(
//                                new String(prefix),
//                                storage,
//                                convertSearchFilterToModelFilter(searchFor));
//                    }
                }
            } else {
                try {
                    new BasicSearchEngine(indexManager).searchAllTypeNames(
                            qualification,
                            SearchPattern.R_EXACT_MATCH,
                            simpleName,
                            matchRule, // not case sensitive
                            searchFor,
                            getSearchScope(),
                            typeRequestor,
                            IJavaSearchConstants.CANCEL_IF_NOT_READY_TO_SEARCH,
                            progressMonitor);
                } catch (OperationCanceledException e) {
//                    findTypes(
//                            new String(prefix),
//                            storage,
//                            convertSearchFilterToModelFilter(searchFor));
                }
            }
        } catch (JavaModelException e) {
//            findTypes(
//                    new String(prefix),
//                    storage,
//                    convertSearchFilterToModelFilter(searchFor));
        }
    }

    /**
     * Must be used only by CompletionEngine.
     * The progress monitor is used to be able to cancel completion operations
     *
     * Find constructor declarations that are defined
     * in the current environment and whose name starts with the
     * given prefix. The prefix is a qualified name separated by periods
     * or a simple name (ex. java.util.V or V).
     *
     * The constructors found are passed to one of the following methods:
     *    ISearchRequestor.acceptConstructor(...)
     */
    public void findConstructorDeclarations(char[] prefix, boolean camelCaseMatch, final ISearchRequestor storage, IProgressMonitor monitor) {
        try {
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

            IProgressMonitor progressMonitor = new IProgressMonitor() {
                boolean isCanceled = false;
                public void beginTask(String name, int totalWork) {
                    // implements interface method
                }
                public void done() {
                    // implements interface method
                }
                public void internalWorked(double work) {
                    // implements interface method
                }
                public boolean isCanceled() {
                    return this.isCanceled;
                }
                public void setCanceled(boolean value) {
                    this.isCanceled = value;
                }
                public void setTaskName(String name) {
                    // implements interface method
                }
                public void subTask(String name) {
                    // implements interface method
                }
                public void worked(int work) {
                    // implements interface method
                }
            };

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

                    storage.acceptConstructor(
                            modifiers,
                            simpleTypeName,
                            parameterCount,
                            signature,
                            parameterTypes,
                            parameterNames,
                            typeModifiers,
                            packageName,
                            extraFlags,
                            path,
                            access);
                }
            };

            int matchRule = SearchPattern.R_PREFIX_MATCH;
            if (camelCaseMatch) matchRule |= SearchPattern.R_CAMELCASE_MATCH;
             IndexManager indexManager = javaProject.getIndexManager();
            if (monitor != null) {
                while (indexManager.awaitingJobsCount() > 0) {
                    try {
                        Thread.sleep(50); // indexes are not ready,  sleep 50ms...
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }
                }
                new BasicSearchEngine(indexManager).searchAllConstructorDeclarations(
                        qualification,
                        simpleName,
                        matchRule,
                        getSearchScope(),
                        constructorRequestor,
                        IJavaSearchConstants.FORCE_IMMEDIATE_SEARCH,
                        progressMonitor);
            } else {
                try {
                    new BasicSearchEngine(indexManager).searchAllConstructorDeclarations(
                            qualification,
                            simpleName,
                            matchRule,
                            getSearchScope(),
                            constructorRequestor,
                            IJavaSearchConstants.CANCEL_IF_NOT_READY_TO_SEARCH,
                            progressMonitor);
                } catch (OperationCanceledException e) {
                    // Do nothing
                }
            }
        } catch (JavaModelException e) {
            // Do nothing
        }
    }
    private IJavaSearchScope getSearchScope() {
        if (this.searchScope == null) {
//            // Create search scope with visible entry on the project's classpath
//            if(this.checkAccessRestrictions) {
                this.searchScope = BasicSearchEngine
                        .createJavaSearchScope(new IJavaElement[]{this.javaProject});
//            } else {
//                this.searchScope = BasicSearchEngine
//                        .createJavaSearchScope(this.nameLookup.packageFragmentRoots);
//            }
        }
        return this.searchScope;
    }
    @Override
    public void setMonitor(IProgressMonitor monitor) {

    }

    /**
     * Find the top-level types that are defined
     * in the current environment and whose simple name matches the given name.
     *
     * The types found are passed to one of the following methods (if additional
     * information is known about the types):
     *    ISearchRequestor.acceptType(char[][] packageName, char[] typeName)
     *    ISearchRequestor.acceptClass(char[][] packageName, char[] typeName, int modifiers)
     *    ISearchRequestor.acceptInterface(char[][] packageName, char[] typeName, int modifiers)
     *
     * This method can not be used to find member types... member
     * types are found relative to their enclosing type.
     */
    public void findExactTypes(char[] name, final boolean findMembers, int searchFor, final ISearchRequestor storage) {

        try {
            IProgressMonitor progressMonitor = new IProgressMonitor() {
                boolean isCanceled = false;
                public void beginTask(String n, int totalWork) {
                    // implements interface method
                }
                public void done() {
                    // implements interface method
                }
                public void internalWorked(double work) {
                    // implements interface method
                }
                public boolean isCanceled() {
                    return this.isCanceled;
                }
                public void setCanceled(boolean value) {
                    this.isCanceled = value;
                }
                public void setTaskName(String n) {
                    // implements interface method
                }
                public void subTask(String n) {
                    // implements interface method
                }
                public void worked(int work) {
                    // implements interface method
                }
            };
           IRestrictedAccessTypeRequestor
                    typeRequestor = new IRestrictedAccessTypeRequestor() {
                public void acceptType(int modifiers, char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path, AccessRestriction access) {
                    if (!findMembers && enclosingTypeNames != null && enclosingTypeNames.length > 0)
                        return; // accept only top level types
                    storage.acceptType(packageName, simpleTypeName, enclosingTypeNames, modifiers, access);
                }
            };
            try {
                new BasicSearchEngine(javaProject.getIndexManager()).searchAllTypeNames(
                        null,
                        SearchPattern.R_EXACT_MATCH,
                        name,
                        SearchPattern.R_EXACT_MATCH,
                        searchFor,
                        getSearchScope(),
                        typeRequestor,
                        IJavaSearchConstants.CANCEL_IF_NOT_READY_TO_SEARCH,
                        progressMonitor);
            } catch (OperationCanceledException e) {
//                findExactTypes(
//                        new String(name),
//                        storage,
//                        convertSearchFilterToModelFilter(searchFor));
            }
        } catch (JavaModelException e) {
//            findExactTypes(
//                    new String(name),
//                    storage,
//                    convertSearchFilterToModelFilter(searchFor));
        }
    }

    /**
     * Find the packages that start with the given prefix.
     * A valid prefix is a qualified name separated by periods
     * (ex. java.util).
     * The packages found are passed to:
     *    ISearchRequestor.acceptPackage(char[][] packageName)
     */
    public void findPackages(char[] prefix, ISearchRequestor requestor) {
        String name = new String(prefix);
        String[] splittedName = Util.splitOn('.', name, 0, name.length());
        for (CodenvyClasspathLocation location : this.locations) {
            location.findPackages(splittedName, requestor);
        }
    }
}
