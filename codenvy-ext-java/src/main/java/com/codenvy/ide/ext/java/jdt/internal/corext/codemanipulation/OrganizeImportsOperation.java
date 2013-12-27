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
package com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation;

import com.codenvy.ide.ext.java.jdt.core.Flags;
import com.codenvy.ide.ext.java.jdt.core.IPackageFragment;
import com.codenvy.ide.ext.java.jdt.core.ISourceRange;
import com.codenvy.ide.ext.java.jdt.core.SourceRange;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.AbstractTypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.IBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.ImportDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Modifier;
import com.codenvy.ide.ext.java.jdt.core.dom.Name;
import com.codenvy.ide.ext.java.jdt.core.dom.SimpleName;
import com.codenvy.ide.ext.java.jdt.core.dom.Type;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite;
import com.codenvy.ide.ext.java.jdt.core.search.SearchEngine;
import com.codenvy.ide.ext.java.jdt.core.search.SearchEngine.SearchCallback;
import com.codenvy.ide.ext.java.jdt.core.search.TypeNameMatch;
import com.codenvy.ide.ext.java.jdt.env.PackageFragment;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Bindings;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ScopeAnalyzer;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.edits.TextEdit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrganizeImportsOperation {
    public static interface IChooseImportQuery {
        /**
         * Selects imports from a list of choices.
         *
         * @param openChoices
         *         From each array, a type reference has to be selected
         * @param ranges
         *         For each choice the range of the corresponding  type reference.
         * @return Returns <code>null</code> to cancel the operation, or the
         *         selected imports.
         */
        void chooseImports(TypeNameMatch[][] openChoices, ISourceRange[] ranges, TypeNameMatchCallback callback);
    }

    static interface ReferenceProcessorCallback {
        void finished(boolean hasOpenChoices);
    }

    public static interface ITextEditCallback {
        void textEditCreated(TextEdit edit);
    }

    public static interface TypeNameMatchCallback {
        void typeNameMatch(TypeNameMatch[] chosen);
    }

    private static class TypeReferenceProcessor {

        private static class UnresolvedTypeData {
            final SimpleName ref;

            final int typeKinds;

            final List<TypeNameMatch> foundInfos;

            public UnresolvedTypeData(SimpleName ref) {
                this.ref = ref;
                this.typeKinds = ASTResolving.getPossibleTypeKinds(ref, true);
                this.foundInfos = new ArrayList<TypeNameMatch>(3);
            }

            public void addInfo(TypeNameMatch info) {
                for (int i = this.foundInfos.size() - 1; i >= 0; i--) {
                    TypeNameMatch curr = this.foundInfos.get(i);
                    if (curr.getTypeContainerName().equals(info.getTypeContainerName())) {
                        return; // not added. already contains type with same name
                    }
                }
                foundInfos.add(info);
            }
        }

        private Set<String> fOldSingleImports;

        private Set<String> fOldDemandImports;

        private Set<String> fImplicitImports;

        private ImportRewrite fImpStructure;

        private boolean fDoIgnoreLowerCaseNames;

        private IPackageFragment fCurrPackage;

        private ScopeAnalyzer fAnalyzer;

        private boolean fAllowDefaultPackageImports;

        private Map<String, UnresolvedTypeData> fUnresolvedTypes;

        private Set<String> fImportsAdded;

        private TypeNameMatch[][] fOpenChoices;

        private SourceRange[] fSourceRanges;

        private final String projectId;

        public TypeReferenceProcessor(Set<String> oldSingleImports, Set<String> oldDemandImports, CompilationUnit root,
                                      ImportRewrite impStructure, boolean ignoreLowerCaseNames, String projectId) {
            fOldSingleImports = oldSingleImports;
            fOldDemandImports = oldDemandImports;
            fImpStructure = impStructure;
            fDoIgnoreLowerCaseNames = ignoreLowerCaseNames;
            this.projectId = projectId;

            //TODO
            //         ICompilationUnit cu = impStructure.getCompilationUnit();

            fImplicitImports = new HashSet<String>(3);
            fImplicitImports.add(""); //$NON-NLS-1$
            fImplicitImports.add("java.lang"); //$NON-NLS-1$
            fImplicitImports.add(root.getPackage().getName().getFullyQualifiedName());

            fAnalyzer = new ScopeAnalyzer(root);

            fCurrPackage = new PackageFragment(root.getPackage().getName());

            fAllowDefaultPackageImports = false;
            //            cu.getJavaProject().getOption(JavaCore.COMPILER_SOURCE, true).equals(JavaCore.VERSION_1_3);

            fImportsAdded = new HashSet<String>();
            fUnresolvedTypes = new HashMap<String, UnresolvedTypeData>();
        }

        private boolean needsImport(ITypeBinding typeBinding, SimpleName ref) {
            if (!typeBinding.isTopLevel() && !typeBinding.isMember() || typeBinding.isRecovered()) {
                return false; // no imports for anonymous, local, primitive types or parameters types
            }
            int modifiers = typeBinding.getModifiers();
            if (Modifier.isPrivate(modifiers)) {
                return false; // imports for privates are not required
            }
            ITypeBinding currTypeBinding = Bindings.getBindingOfParentType(ref);
            if (currTypeBinding == null) {
                if (ASTNodes.getParent(ref, ASTNode.PACKAGE_DECLARATION) != null) {
                    return true; // reference in package-info.java
                }
                return false; // not in a type
            }
            if (!Modifier.isPublic(modifiers)) {
                if (!currTypeBinding.getPackage().getName().equals(typeBinding.getPackage().getName())) {
                    return false; // not visible
                }
            }

            ASTNode parent = ref.getParent();
            while (parent instanceof Type) {
                parent = parent.getParent();
            }
            if (parent instanceof AbstractTypeDeclaration && parent.getParent() instanceof CompilationUnit) {
                return true;
            }

            if (typeBinding.isMember()) {
                if (fAnalyzer.isDeclaredInScope(typeBinding, ref, ScopeAnalyzer.TYPES | ScopeAnalyzer.CHECK_VISIBILITY))
                    return false;
            }
            return true;
        }

        /**
         * Tries to find the given type name and add it to the import structure.
         *
         * @param ref
         *         the name node
         */
        public void add(SimpleName ref) {
            String typeName = ref.getIdentifier();

            if (fImportsAdded.contains(typeName)) {
                return;
            }

            IBinding binding = ref.resolveBinding();
            if (binding != null) {
                if (binding.getKind() != IBinding.TYPE) {
                    return;
                }
                ITypeBinding typeBinding = (ITypeBinding)binding;
                if (typeBinding.isArray()) {
                    typeBinding = typeBinding.getElementType();
                }
                typeBinding = typeBinding.getTypeDeclaration();
                if (!typeBinding.isRecovered()) {
                    if (needsImport(typeBinding, ref)) {
                        fImpStructure.addImport(typeBinding);
                        fImportsAdded.add(typeName);
                    }
                    return;
                }
            } else {
                if (fDoIgnoreLowerCaseNames && typeName.length() > 0) {
                    char ch = typeName.charAt(0);
                    if (Character.isLowerCase(ch) && Character.isLetter(ch)) {
                        return;
                    }
                }
            }
            fImportsAdded.add(typeName);
            fUnresolvedTypes.put(typeName, new UnresolvedTypeData(ref));
        }

        public void process(final ReferenceProcessorCallback callback) {
            try {
                final int nUnresolved = fUnresolvedTypes.size();
                if (nUnresolved == 0) {
                    callback.finished(false);
                    return;
                }
                char[][] allTypes = new char[nUnresolved][];
                int i = 0;
                for (Iterator<String> iter = fUnresolvedTypes.keySet().iterator(); iter.hasNext(); ) {
                    allTypes[i++] = iter.next().toCharArray();
                }
                final ArrayList<TypeNameMatch> typesFound = new ArrayList<TypeNameMatch>();
                new SearchEngine(projectId, fCurrPackage).searchAllTypeNames(allTypes, typesFound, new SearchCallback() {

                    @Override
                    public void searchFinished(ArrayList<TypeNameMatch> typesFound) {

                        boolean is50OrHigher = true; //JavaModelUtil.is50OrHigher(project);

                        for (int i = 0; i < typesFound.size(); i++) {
                            TypeNameMatch curr = typesFound.get(i);
                            UnresolvedTypeData data = fUnresolvedTypes.get(curr.getSimpleTypeName());
                            if (data != null && isVisible(curr) && isOfKind(curr, data.typeKinds, is50OrHigher)) {
                                if (fAllowDefaultPackageImports || curr.getPackageName().length() > 0) {
                                    data.addInfo(curr);
                                }
                            }
                        }
                        ArrayList<TypeNameMatch[]> openChoices = new ArrayList<TypeNameMatch[]>(nUnresolved);
                        ArrayList<SourceRange> sourceRanges = new ArrayList<SourceRange>(nUnresolved);
                        for (Iterator<UnresolvedTypeData> iter = fUnresolvedTypes.values().iterator(); iter.hasNext(); ) {
                            UnresolvedTypeData data = iter.next();
                            TypeNameMatch[] openChoice = processTypeInfo(data.foundInfos);
                            if (openChoice != null) {
                                openChoices.add(openChoice);
                                sourceRanges.add(new SourceRange(data.ref.getStartPosition(), data.ref.getLength()));
                            }
                        }
                        if (openChoices.isEmpty()) {
                            callback.finished(false);
                            return;
                        }
                        fOpenChoices = openChoices.toArray(new TypeNameMatch[openChoices.size()][]);
                        fSourceRanges = sourceRanges.toArray(new SourceRange[sourceRanges.size()]);
                        callback.finished(true);
                    }
                });

            } finally {
                //            monitor.done();
            }
        }

        private TypeNameMatch[] processTypeInfo(List<TypeNameMatch> typeRefsFound) {
            int nFound = typeRefsFound.size();
            if (nFound == 0) {
                // nothing found
                return null;
            } else if (nFound == 1) {
                TypeNameMatch typeRef = typeRefsFound.get(0);
                fImpStructure.addImport(typeRef.getFullyQualifiedName());
                return null;
            } else {
                String typeToImport = null;
                boolean ambiguousImports = false;

                // multiple found, use old imports to find an entry
                for (int i = 0; i < nFound; i++) {
                    TypeNameMatch typeRef = typeRefsFound.get(i);
                    String fullName = typeRef.getFullyQualifiedName();
                    String containerName = typeRef.getTypeContainerName();
                    if (fOldSingleImports.contains(fullName)) {
                        // was single-imported
                        fImpStructure.addImport(fullName);
                        return null;
                    } else if (fOldDemandImports.contains(containerName) || fImplicitImports.contains(containerName)) {
                        if (typeToImport == null) {
                            typeToImport = fullName;
                        } else { // more than one import-on-demand
                            ambiguousImports = true;
                        }
                    }
                }

                if (typeToImport != null && !ambiguousImports) {
                    fImpStructure.addImport(typeToImport);
                    return null;
                }
                // return the open choices
                return typeRefsFound.toArray(new TypeNameMatch[nFound]);
            }
        }

        private boolean isOfKind(TypeNameMatch curr, int typeKinds, boolean is50OrHigher) {
            int flags = curr.getModifiers();
            if (Flags.isAnnotation(flags)) {
                return is50OrHigher && (typeKinds & SimilarElementsRequestor.ANNOTATIONS) != 0;
            }
            if (Flags.isEnum(flags)) {
                return is50OrHigher && (typeKinds & SimilarElementsRequestor.ENUMS) != 0;
            }
            if (Flags.isInterface(flags)) {
                return (typeKinds & SimilarElementsRequestor.INTERFACES) != 0;
            }
            return (typeKinds & SimilarElementsRequestor.CLASSES) != 0;
        }

        private boolean isVisible(TypeNameMatch curr) {
            int flags = curr.getModifiers();
            if (Flags.isPrivate(flags)) {
                return false;
            }
            if (Flags.isPublic(flags) || Flags.isProtected(flags)) {
                return true;
            }
            return curr.getPackageName().equals(fCurrPackage.getElementName());
        }

        public TypeNameMatch[][] getChoices() {
            return fOpenChoices;
        }

        public ISourceRange[] getChoicesSourceRanges() {
            return fSourceRanges;
        }
    }

    private boolean fDoSave;

    private boolean fIgnoreLowerCaseNames;

    private IChooseImportQuery fChooseImportQuery;

    private int fNumberOfImportsAdded;

    private int fNumberOfImportsRemoved;

    private IProblem fParsingError;

    //   private ICompilationUnit fCompilationUnit;

    private CompilationUnit fASTRoot;

    private final boolean fAllowSyntaxErrors;

    private final Document document;

    private final String projectId;

    public OrganizeImportsOperation(Document document, CompilationUnit astRoot, boolean ignoreLowerCaseNames,
                                    boolean save, boolean allowSyntaxErrors, IChooseImportQuery chooseImportQuery, String projectId) {
        this.document = document;
        //      fCompilationUnit = cu;
        fASTRoot = astRoot;

        fDoSave = save;
        fIgnoreLowerCaseNames = ignoreLowerCaseNames;
        fAllowSyntaxErrors = allowSyntaxErrors;
        fChooseImportQuery = chooseImportQuery;
        this.projectId = projectId;

        fNumberOfImportsAdded = 0;
        fNumberOfImportsRemoved = 0;

        fParsingError = null;
    }

    //   /**
    //    * Runs the operation.
    //    * @param monitor the progress monitor
    //    * @throws CoreException thrown when the operation failed
    //    * @throws OperationCanceledException Runtime error thrown when operation is canceled.
    //    */
    //   public void run() throws CoreException
    //   {
    //      //      if (monitor == null) {
    //      //         monitor= new NullProgressMonitor();
    //      //      }
    //      try
    //      {
    //         //         monitor.beginTask(Messages.format(CodeGenerationMessages.OrganizeImportsOperation_description,
    // BasicElementLabels.getFileName(fCompilationUnit)), 10);
    //
    //         TextEdit edit = createTextEdit();
    //         if (edit == null)
    //            return;
    //         //TODO
    //         //          JavaModelUtil.applyEdit(fCompilationUnit, edit, fDoSave, new SubProgressMonitor(monitor, 1));
    //      }
    //      finally
    //      {
    //         //         monitor.done();
    //      }
    //   }

    public void createTextEdit(final ITextEditCallback callback) {

        try {
            fNumberOfImportsAdded = 0;
            fNumberOfImportsRemoved = 0;

            //         monitor.beginTask(Messages.format(CodeGenerationMessages.OrganizeImportsOperation_description,
            // BasicElementLabels.getFileName(fCompilationUnit)), 9);

            CompilationUnit astRoot = fASTRoot;
            //         if (astRoot == null) {
            //            astRoot= SharedASTProvider.getAST(fCompilationUnit, SharedASTProvider.WAIT_YES, new SubProgressMonitor(monitor,
            // 2));
            //            if (monitor.isCanceled())
            //               throw new OperationCanceledException();
            //         } else {
            ////            monitor.worked(2);
            //         }

            final ImportRewrite importsRewrite = StubUtility.createImportRewrite(document, astRoot, false);

            final Set<String> oldSingleImports = new HashSet<String>();
            final Set<String> oldDemandImports = new HashSet<String>();
            final List<SimpleName> typeReferences = new ArrayList<SimpleName>();
            final List<SimpleName> staticReferences = new ArrayList<SimpleName>();

            if (!collectReferences(astRoot, typeReferences, staticReferences, oldSingleImports, oldDemandImports)) {
                callback.textEditCreated(null);
                return;
            }
            //         monitor.worked(1);

            final TypeReferenceProcessor processor =
                    new TypeReferenceProcessor(oldSingleImports, oldDemandImports, astRoot, importsRewrite,
                                               fIgnoreLowerCaseNames, projectId);

            Iterator<SimpleName> refIterator = typeReferences.iterator();
            while (refIterator.hasNext()) {
                SimpleName typeRef = refIterator.next();
                processor.add(typeRef);
            }

            processor.process(new ReferenceProcessorCallback() {

                @Override
                public void finished(boolean hasOpenChoices) {
                    addStaticImports(staticReferences, importsRewrite);

                    if (hasOpenChoices && fChooseImportQuery != null) {
                        TypeNameMatch[][] choices = processor.getChoices();
                        ISourceRange[] ranges = processor.getChoicesSourceRanges();
                        fChooseImportQuery.chooseImports(choices, ranges, new TypeNameMatchCallback() {

                            @Override
                            public void typeNameMatch(TypeNameMatch[] chosen) {
                                if (chosen == null) {
                                    //TODO
                                    // cancel pressed by the user
                                    //               throw new OperationCanceledException();
                                    return;
                                }
                                for (int i = 0; i < chosen.length; i++) {
                                    TypeNameMatch typeInfo = chosen[i];
                                    importsRewrite.addImport(typeInfo.getFullyQualifiedName());
                                }

                                createTextEdit(callback, importsRewrite, oldSingleImports, oldDemandImports);
                            }

                        });
                    } else
                        createTextEdit(callback, importsRewrite, oldSingleImports, oldDemandImports);
                }
            });

        } finally {
            //         monitor.done();
        }
    }

    /**
     * @param callback
     * @param importsRewrite
     * @param oldSingleImports
     * @param oldDemandImports
     */
    private void createTextEdit(final ITextEditCallback callback, final ImportRewrite importsRewrite,
                                final Set<String> oldSingleImports, final Set<String> oldDemandImports) {
        TextEdit result = null;
        result = importsRewrite.rewriteImports();

        determineImportDifferences(importsRewrite, oldSingleImports, oldDemandImports);

        callback.textEditCreated(result);
    }

    private void determineImportDifferences(ImportRewrite importsStructure, Set<String> oldSingleImports,
                                            Set<String> oldDemandImports) {
        ArrayList<String> importsAdded = new ArrayList<String>();
        importsAdded.addAll(Arrays.asList(importsStructure.getCreatedImports()));
        importsAdded.addAll(Arrays.asList(importsStructure.getCreatedStaticImports()));

        Object[] content = oldSingleImports.toArray();
        for (int i = 0; i < content.length; i++) {
            String importName = (String)content[i];
            if (importsAdded.remove(importName))
                oldSingleImports.remove(importName);
        }
        content = oldDemandImports.toArray();
        for (int i = 0; i < content.length; i++) {
            String importName = (String)content[i];
            if (importsAdded.remove(importName + ".*")) //$NON-NLS-1$
                oldDemandImports.remove(importName);
        }
        fNumberOfImportsAdded = importsAdded.size();
        fNumberOfImportsRemoved = oldSingleImports.size() + oldDemandImports.size();
    }

    private void addStaticImports(List<SimpleName> staticReferences, ImportRewrite importsStructure) {
        for (int i = 0; i < staticReferences.size(); i++) {
            Name name = staticReferences.get(i);
            IBinding binding = name.resolveBinding();
            if (binding != null) { // paranoia check
                importsStructure.addStaticImport(binding);
            }
        }
    }

    // find type references in a compilation unit
    private boolean collectReferences(CompilationUnit astRoot, List<SimpleName> typeReferences,
                                      List<SimpleName> staticReferences, Set<String> oldSingleImports, Set<String> oldDemandImports) {
        if (!fAllowSyntaxErrors) {
            IProblem[] problems = astRoot.getProblems();
            for (int i = 0; i < problems.length; i++) {
                IProblem curr = problems[i];
                if (curr.isError() && (curr.getID() & IProblem.Syntax) != 0) {
                    fParsingError = problems[i];
                    return false;
                }
            }
        }
        List<ImportDeclaration> imports = astRoot.imports();
        for (int i = 0; i < imports.size(); i++) {
            ImportDeclaration curr = imports.get(i);
            String id = ASTResolving.getFullName(curr.getName());
            if (curr.isOnDemand()) {
                oldDemandImports.add(id);
            } else {
                oldSingleImports.add(id);
            }
        }

        //      IJavaProject project = fCompilationUnit.getJavaProject();
        ImportReferencesCollector.collect(astRoot, null, typeReferences, staticReferences);

        return true;
    }

    /**
     * After executing the operation, returns <code>null</code> if the operation has been executed successfully or
     * the range where parsing failed.
     *
     * @return returns the parse error
     */
    public IProblem getParseError() {
        return fParsingError;
    }

    public int getNumberOfImportsAdded() {
        return fNumberOfImportsAdded;
    }

    public int getNumberOfImportsRemoved() {
        return fNumberOfImportsRemoved;
    }

    //   /**
    //    * @return Returns the scheduling rule for this operation
    //    */
    //   public ISchedulingRule getScheduleRule() {
    //      return fCompilationUnit.getResource();
    //   }

}
