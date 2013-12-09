/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.emul;

import com.codenvy.ide.ext.java.jdt.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.jdt.internal.codeassist.ISearchRequestor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.NameEnvironmentAnswer;
import com.codenvy.ide.ext.java.jdt.internal.compiler.util.SuffixConstants;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileSystem implements INameEnvironment, SuffixConstants {
    public interface Classpath {
        char[][][] findTypeNames(String qualifiedPackageName);

        NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName);

        NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String qualifiedBinaryFileName,
                                        boolean asBinaryOnly);

        boolean isPackage(String qualifiedPackageName);

        /**
         * Return a list of the jar file names defined in the Class-Path section
         * of the jar file manifest if any, null else. Only ClasspathJar (and
         * extending classes) instances may return a non-null result.
         *
         * @param problemReporter
         *         problem reporter with which potential
         *         misconfiguration issues are raised
         * @return a list of the jar file names defined in the Class-Path
         *         section of the jar file manifest if any
         */
        List fetchLinkedJars(ClasspathSectionProblemReporter problemReporter);

        /**
         * This method resets the environment. The resulting state is equivalent to
         * a new name environment without creating a new object.
         */
        void reset();

        /**
         * Return a normalized path for file based classpath entries. This is an
         * absolute path in which file separators are transformed to the
         * platform-agnostic '/', ending with a '/' for directories. This is an
         * absolute path in which file separators are transformed to the
         * platform-agnostic '/', deprived from the '.jar' (resp. '.zip')
         * extension for jar (resp. zip) files.
         *
         * @return a normalized path for file based classpath entries
         */
        char[] normalizedPath();

        /**
         * Return the path for file based classpath entries. This is an absolute path
         * ending with a file separator for directories, an absolute path including the '.jar'
         * (resp. '.zip') extension for jar (resp. zip) files.
         *
         * @return the path for file based classpath entries
         */
        String getPath();

        /** Initialize the entry */
        void initialize() throws IOException;
    }

    public interface ClasspathSectionProblemReporter {
        void invalidClasspathSection(String jarFilePath);

        void multipleClasspathSections(String jarFilePath);
    }

    /**
     * This class is defined how to normalize the classpath entries.
     * It removes duplicate entries.
     */
    public static class ClasspathNormalizer {
        /**
         * Returns the normalized classpath entries (no duplicate).
         * <p>The given classpath entries are FileSystem.Classpath. We check the getPath() in order to find
         * duplicate entries.</p>
         *
         * @param classpaths
         *         the given classpath entries
         * @return the normalized classpath entries
         */
        public static ArrayList normalize(ArrayList classpaths) {
            ArrayList normalizedClasspath = new ArrayList();
            HashSet cache = new HashSet();
            for (Iterator iterator = classpaths.iterator(); iterator.hasNext(); ) {
                FileSystem.Classpath classpath = (FileSystem.Classpath)iterator.next();
                if (!cache.contains(classpath)) {
                    normalizedClasspath.add(classpath);
                    cache.add(classpath);
                }
            }
            return normalizedClasspath;
        }
    }

    Classpath[] classpaths;

    Set knownFileNames;

    /*
        classPathNames is a collection is Strings representing the full path of each class path
        initialFileNames is a collection is Strings, the trailing '.java' will be removed if its not already.
    */
    public FileSystem(String[] classpathNames, String[] initialFileNames, String encoding) {
        final int classpathSize = classpathNames.length;
        this.classpaths = new Classpath[classpathSize];
        int counter = 0;
        for (int i = 0; i < classpathSize; i++) {
            Classpath classpath = getClasspath(classpathNames[i], encoding, null);
            try {
                classpath.initialize();
                this.classpaths[counter++] = classpath;
            } catch (IOException e) {
                // ignore
            }
        }
        if (counter != classpathSize) {
            System.arraycopy(this.classpaths, 0, (this.classpaths = new Classpath[counter]), 0, counter);
        }
        initializeKnownFileNames(initialFileNames);
    }

    protected FileSystem(Classpath[] paths, String[] initialFileNames) {
        final int length = paths.length;
        int counter = 0;
        this.classpaths = new FileSystem.Classpath[length];
        for (int i = 0; i < length; i++) {
            final Classpath classpath = paths[i];
            try {
                classpath.initialize();
                this.classpaths[counter++] = classpath;
            } catch (IOException exception) {
                // ignore
            }
        }
        if (counter != length) {
            // should not happen
            System.arraycopy(this.classpaths, 0, (this.classpaths = new FileSystem.Classpath[counter]), 0, counter);
        }
        initializeKnownFileNames(initialFileNames);
    }

    public static Classpath getClasspath(String classpathName, String encoding, AccessRuleSet accessRuleSet) {
        return getClasspath(classpathName, encoding, false, accessRuleSet, null);
    }

    public static Classpath getClasspath(String classpathName, String encoding, boolean isSourceOnly,
                                         AccessRuleSet accessRuleSet, String destinationPath) {
        Classpath result = null;
        File file = new File(convertPathSeparators(classpathName));
        if (file.isDirectory()) {
            if (file.exists()) {
                result =
                        new ClasspathDirectory(file, encoding, isSourceOnly ? ClasspathLocation.SOURCE
                                                                            : ClasspathLocation.SOURCE | ClasspathLocation.BINARY,
                                               accessRuleSet, destinationPath == null
                                                              || destinationPath == Main.NONE ? destinationPath :
                                                              // keep == comparison valid
                                                              convertPathSeparators(destinationPath));
            }
        } else {
            if (isPotentialZipArchive(classpathName)) {
                if (isSourceOnly) {
                    // source only mode
                    result =
                            new ClasspathSourceJar(file, true, accessRuleSet, encoding, destinationPath == null
                                                                                        || destinationPath == Main.NONE ? destinationPath :
                                                                                        // keep == comparison valid
                                                                                        convertPathSeparators(destinationPath));
                } else if (destinationPath == null) {
                    // class file only mode
                    result = new ClasspathJar(file, true, accessRuleSet, null);
                }
            }
        }
        return result;
    }

    private void initializeKnownFileNames(String[] initialFileNames) {
        if (initialFileNames == null) {
            this.knownFileNames = new HashSet(0);
            return;
        }
        this.knownFileNames = new HashSet(initialFileNames.length * 2);
        for (int i = initialFileNames.length; --i >= 0; ) {
            File compilationUnitFile = new File(initialFileNames[i]);
            char[] fileName = null;
            try {
                fileName = compilationUnitFile.getCanonicalPath().toCharArray();
            } catch (IOException e) {
                // this should not happen as the file exists
                continue;
            }
            char[] matchingPathName = null;
            final int lastIndexOf = CharOperation.lastIndexOf('.', fileName);
            if (lastIndexOf != -1) {
                fileName = CharOperation.subarray(fileName, 0, lastIndexOf);
            }
            CharOperation.replace(fileName, '\\', '/');
            boolean globalPathMatches = false;
            // the most nested path should be the selected one
            for (int j = 0, max = this.classpaths.length; j < max; j++) {
                char[] matchCandidate = this.classpaths[j].normalizedPath();
                boolean currentPathMatch = false;
                if (this.classpaths[j] instanceof ClasspathDirectory
                    && CharOperation.prefixEquals(matchCandidate, fileName)) {
                    currentPathMatch = true;
                    if (matchingPathName == null) {
                        matchingPathName = matchCandidate;
                    } else {
                        if (currentPathMatch) {
                            // we have a second source folder that matches the path of the source file
                            if (matchCandidate.length > matchingPathName.length) {
                                // we want to preserve the shortest possible path
                                matchingPathName = matchCandidate;
                            }
                        } else {
                            // we want to preserve the shortest possible path
                            if (!globalPathMatches && matchCandidate.length < matchingPathName.length) {
                                matchingPathName = matchCandidate;
                            }
                        }
                    }
                    if (currentPathMatch) {
                        globalPathMatches = true;
                    }
                }
            }
            if (matchingPathName == null) {
                this.knownFileNames.add(new String(fileName)); // leave as is...
            } else {
                this.knownFileNames.add(new String(CharOperation.subarray(fileName, matchingPathName.length,
                                                                          fileName.length)));
            }
            matchingPathName = null;
        }
    }

    @Override
    public void cleanup() {
        for (int i = 0, max = this.classpaths.length; i < max; i++) {
            this.classpaths[i].reset();
        }
    }

    private static String convertPathSeparators(String path) {
        return File.separatorChar == '/' ? path.replace('\\', '/') : path.replace('/', '\\');
    }

    private NameEnvironmentAnswer findClass(String qualifiedTypeName, char[] typeName, boolean asBinaryOnly) {
        if (this.knownFileNames.contains(qualifiedTypeName)) {
            return null; // looking for a file which we know was provided at the beginning of the compilation
        }

        String qualifiedBinaryFileName = qualifiedTypeName + SUFFIX_STRING_class;
        String qualifiedPackageName =
                qualifiedTypeName.length() == typeName.length ? "" : qualifiedBinaryFileName.substring(0,
                                                                                                       qualifiedTypeName.length() -
                                                                                                       typeName.length - 1);
        String qp2 =
                File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
        NameEnvironmentAnswer suggestedAnswer = null;
        if (qualifiedPackageName == qp2) {
            for (int i = 0, length = this.classpaths.length; i < length; i++) {
                NameEnvironmentAnswer answer =
                        this.classpaths[i].findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName, asBinaryOnly);
                if (answer != null) {
                    if (!answer.ignoreIfBetter()) {
                        if (answer.isBetter(suggestedAnswer)) {
                            return answer;
                        }
                    } else if (answer.isBetter(suggestedAnswer)) {
                        // remember suggestion and keep looking
                        suggestedAnswer = answer;
                    }
                }
            }
        } else {
            String qb2 = qualifiedBinaryFileName.replace('/', File.separatorChar);
            for (int i = 0, length = this.classpaths.length; i < length; i++) {
                Classpath p = this.classpaths[i];
                NameEnvironmentAnswer answer =
                        (p instanceof ClasspathJar) ? p.findClass(typeName, qualifiedPackageName, qualifiedBinaryFileName,
                                                                  asBinaryOnly) : p.findClass(typeName, qp2, qb2, asBinaryOnly);
                if (answer != null) {
                    if (!answer.ignoreIfBetter()) {
                        if (answer.isBetter(suggestedAnswer)) {
                            return answer;
                        }
                    } else if (answer.isBetter(suggestedAnswer)) {
                        // remember suggestion and keep looking
                        suggestedAnswer = answer;
                    }
                }
            }
        }
        if (suggestedAnswer != null) {
            // no better answer was found
            return suggestedAnswer;
        }
        return null;
    }

    @Override
    public NameEnvironmentAnswer findType(char[][] compoundName) {
        if (compoundName != null) {
            return findClass(new String(CharOperation.concatWith(compoundName, '/')),
                             compoundName[compoundName.length - 1], false);
        }
        return null;
    }

    public char[][][] findTypeNames(char[][] packageName) {
        char[][][] result = null;
        if (packageName != null) {
            String qualifiedPackageName = new String(CharOperation.concatWith(packageName, '/'));
            String qualifiedPackageName2 =
                    File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
            if (qualifiedPackageName == qualifiedPackageName2) {
                for (int i = 0, length = this.classpaths.length; i < length; i++) {
                    char[][][] answers = this.classpaths[i].findTypeNames(qualifiedPackageName);
                    if (answers != null) {
                        // concat with previous answers
                        if (result == null) {
                            result = answers;
                        } else {
                            int resultLength = result.length;
                            int answersLength = answers.length;
                            System
                                    .arraycopy(result, 0, (result = new char[answersLength + resultLength][][]), 0, resultLength);
                            System.arraycopy(answers, 0, result, resultLength, answersLength);
                        }
                    }
                }
            } else {
                for (int i = 0, length = this.classpaths.length; i < length; i++) {
                    Classpath p = this.classpaths[i];
                    char[][][] answers =
                            (p instanceof ClasspathJar) ? p.findTypeNames(qualifiedPackageName) : p
                                    .findTypeNames(qualifiedPackageName2);
                    if (answers != null) {
                        // concat with previous answers
                        if (result == null) {
                            result = answers;
                        } else {
                            int resultLength = result.length;
                            int answersLength = answers.length;
                            System
                                    .arraycopy(result, 0, (result = new char[answersLength + resultLength][][]), 0, resultLength);
                            System.arraycopy(answers, 0, result, resultLength, answersLength);
                        }
                    }
                }
            }
        }
        return result;
    }

    public NameEnvironmentAnswer findType(char[][] compoundName, boolean asBinaryOnly) {
        if (compoundName != null) {
            return findClass(new String(CharOperation.concatWith(compoundName, '/')),
                             compoundName[compoundName.length - 1], asBinaryOnly);
        }
        return null;
    }

    @Override
    public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
        if (typeName != null) {
            return findClass(new String(CharOperation.concatWith(packageName, typeName, '/')), typeName, false);
        }
        return null;
    }

    @Override
    public boolean isPackage(char[][] compoundName, char[] packageName) {
        String qualifiedPackageName = new String(CharOperation.concatWith(compoundName, packageName, '/'));
        String qp2 =
                File.separatorChar == '/' ? qualifiedPackageName : qualifiedPackageName.replace('/', File.separatorChar);
        if (qualifiedPackageName == qp2) {
            for (int i = 0, length = this.classpaths.length; i < length; i++) {
                if (this.classpaths[i].isPackage(qualifiedPackageName)) {
                    return true;
                }
            }
        } else {
            for (int i = 0, length = this.classpaths.length; i < length; i++) {
                Classpath p = this.classpaths[i];
                if ((p instanceof ClasspathJar) ? p.isPackage(qualifiedPackageName) : p.isPackage(qp2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see com.codenvy.ide.ext.java.client.internal.compiler.env.INameEnvironment#findTypes(char[], boolean, boolean, int,
     *      com.codenvy.ide.ext.java.client.internal.codeassist.ISearchRequestor, com.codenvy.ide.ext.java.client.runtime.IProgressMonitor)
     */
    @Override
    public void findTypes(char[] qualifiedName, boolean b, boolean camelCaseMatch, int searchFor,
                          final ISearchRequestor requestor) {
    }

    /**
     * @see com.codenvy.ide.ext.java.client.internal.compiler.env.INameEnvironment#findPackages(char[],
     *      com.codenvy.ide.ext.java.client.internal.codeassist.ISearchRequestor)
     */
    @Override
    public void findPackages(char[] qualifiedName, ISearchRequestor requestor) {
    }

    /**
     * @see com.codenvy.ide.ext.java.client.internal.compiler.env.INameEnvironment#findConstructorDeclarations(char[], boolean,
     *      com.codenvy.ide.ext.java.client.internal.codeassist.ISearchRequestor, com.codenvy.ide.ext.java.client.runtime.IProgressMonitor)
     */
    @Override
    public void findConstructorDeclarations(char[] prefix, boolean camelCaseMatch, final ISearchRequestor requestor) {
    }

    /**
     * @see com.codenvy.ide.ext.java.client.internal.compiler.env.INameEnvironment#findExactTypes(char[], boolean, int,
     *      com.codenvy.ide.ext.java.client.internal.codeassist.ISearchRequestor)
     */
    @Override
    public void findExactTypes(char[] missingSimpleName, boolean b, int type, ISearchRequestor storage) {
    }

    /**
     * Returns whether the given name is potentially a zip archive file name
     * (it has a file extension and it is not ".java" nor ".class")
     */
    public final static boolean isPotentialZipArchive(String name) {
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1) {
            return false; // no file extension, it cannot be a zip archive name
        }
        if (name.lastIndexOf(File.separatorChar) > lastDot) {
            return false; // dot was before the last file separator, it cannot be a zip archive name
        }
        int length = name.length();
        int extensionLength = length - lastDot - 1;
        if (extensionLength == EXTENSION_java.length()) {
            for (int i = extensionLength - 1; i >= 0; i--) {
                if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != EXTENSION_java.charAt(i)) {
                    break; // not a ".java" file, check ".class" file case below
                }
                if (i == 0) {
                    return false; // it is a ".java" file, it cannot be a zip archive name
                }
            }
        }
        if (extensionLength == EXTENSION_class.length()) {
            for (int i = extensionLength - 1; i >= 0; i--) {
                if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != EXTENSION_class.charAt(i)) {
                    return true; // not a ".class" file, so this is a potential archive name
                }
            }
            return false; // it is a ".class" file, it cannot be a zip archive name
        }
        return true; // it is neither a ".java" file nor a ".class" file, so this is a potential archive name
    }
}
