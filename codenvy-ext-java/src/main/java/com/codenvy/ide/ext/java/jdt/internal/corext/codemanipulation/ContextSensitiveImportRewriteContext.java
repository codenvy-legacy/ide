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

import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.AbstractTypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.IBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.ImportDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Modifier;
import com.codenvy.ide.ext.java.jdt.core.dom.Name;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ScopeAnalyzer;
import com.codenvy.ide.ext.java.jdt.internal.corext.util.CodeFormatterUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This <code>ImportRewriteContext</code> is aware of all the types visible in <code>compilationUnit</code> at
 * <code>position</code>.
 * <p>
 * <b>Note:</b> This context only works if the AST was created with bindings!
 * </p>
 */
public class ContextSensitiveImportRewriteContext extends ImportRewriteContext {

    private final CompilationUnit fCompilationUnit;

    private final int fPosition;

    private IBinding[] fDeclarationsInScope;

    private Name[] fImportedNames;

    private final ImportRewrite fImportRewrite;

    /**
     * Creates an import rewrite context at the given node's start position.
     *
     * @param node
     *         the node to use as context
     * @param importRewrite
     *         the import rewrite
     * @since 3.6
     */
    public ContextSensitiveImportRewriteContext(ASTNode node, ImportRewrite importRewrite) {
        this((CompilationUnit)node.getRoot(), node.getStartPosition(), importRewrite);
    }

    /**
     * Creates an import rewrite context at the given start position.
     *
     * @param compilationUnit
     *         the root
     * @param position
     *         the context position
     * @param importRewrite
     *         the import rewrite
     */
    public ContextSensitiveImportRewriteContext(CompilationUnit compilationUnit, int position,
                                                ImportRewrite importRewrite) {
        fCompilationUnit = compilationUnit;
        fPosition = position;
        fImportRewrite = importRewrite;
        fDeclarationsInScope = null;
        fImportedNames = null;
    }

    @Override
    public int findInContext(String qualifier, String name, int kind) {
        IBinding[] declarationsInScope = getDeclarationsInScope();
        for (int i = 0; i < declarationsInScope.length; i++) {
            if (declarationsInScope[i] instanceof ITypeBinding) {
                ITypeBinding typeBinding = (ITypeBinding)declarationsInScope[i];
                if (isSameType(typeBinding, qualifier, name)) {
                    return RES_NAME_FOUND;
                } else if (isConflicting(typeBinding, name)) {
                    return RES_NAME_CONFLICT;
                }
            } else if (declarationsInScope[i] != null) {
                if (isConflicting(declarationsInScope[i], name)) {
                    return RES_NAME_CONFLICT;
                }
            }
        }

        Name[] names = getImportedNames();
        for (int i = 0; i < names.length; i++) {
            IBinding binding = names[i].resolveBinding();
            if (binding instanceof ITypeBinding && !binding.isRecovered()) {
                ITypeBinding typeBinding = (ITypeBinding)binding;
                if (isConflictingType(typeBinding, qualifier, name)) {
                    return RES_NAME_CONFLICT;
                }
            }
        }

        List<AbstractTypeDeclaration> list = fCompilationUnit.types();
        for (Iterator<AbstractTypeDeclaration> iter = list.iterator(); iter.hasNext(); ) {
            AbstractTypeDeclaration type = iter.next();
            ITypeBinding binding = type.resolveBinding();
            if (binding != null) {
                if (isSameType(binding, qualifier, name)) {
                    return RES_NAME_FOUND;
                } else {
                    ITypeBinding decl = containingDeclaration(binding, qualifier, name);
                    while (decl != null && !decl.equals(binding)) {
                        int modifiers = decl.getModifiers();
                        if (Modifier.isPrivate(modifiers))
                            return RES_NAME_CONFLICT;
                        decl = decl.getDeclaringClass();
                    }
                }
            }
        }

        String[] addedImports = fImportRewrite.getAddedImports();
        String qualifiedName = CodeFormatterUtil.concatenateName(qualifier, name);
        for (int i = 0; i < addedImports.length; i++) {
            String addedImport = addedImports[i];
            if (qualifiedName.equals(addedImport)) {
                return RES_NAME_FOUND;
            } else {
                if (isConflicting(name, addedImport))
                    return RES_NAME_CONFLICT;
            }
        }
        //TODO maybe need search for types in project that has package 'java.lang'
        //      if (qualifier.equals("java.lang"))
        //      {
        //         // No explicit import statement required
        //         ITypeRoot typeRoot = fCompilationUnit.getTypeRoot();
        //         if (typeRoot != null)
        //         {
        //            IPackageFragment packageFragment = (IPackageFragment)typeRoot.getParent();
        //            try
        //            {
        //               ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
        //               for (int i = 0; i < compilationUnits.length; i++)
        //               {
        //                  ICompilationUnit cu = compilationUnits[i];
        //                  IType[] allTypes = cu.getAllTypes();
        //                  for (int j = 0; j < allTypes.length; j++)
        //                  {
        //                     IType type = allTypes[j];
        //                     String packageTypeName = type.getFullyQualifiedName();
        //                     if (isConflicting(name, packageTypeName))
        //                        return RES_NAME_CONFLICT;
        //                  }
        //               }
        //            }
        //            catch (JavaModelException e)
        //            {
        //            }
        //         }
        //      }

        return fImportRewrite.getDefaultImportRewriteContext().findInContext(qualifier, name, kind);
    }

    private boolean isConflicting(String name, String importt) {
        int index = importt.lastIndexOf('.');
        String importedName;
        if (index == -1) {
            importedName = importt;
        } else {
            importedName = importt.substring(index + 1, importt.length());
        }
        if (importedName.equals(name)) {
            return true;
        }
        return false;
    }

    private ITypeBinding containingDeclaration(ITypeBinding binding, String qualifier, String name) {
        ITypeBinding[] declaredTypes = binding.getDeclaredTypes();
        for (int i = 0; i < declaredTypes.length; i++) {
            ITypeBinding childBinding = declaredTypes[i];
            if (isSameType(childBinding, qualifier, name)) {
                return childBinding;
            } else {
                ITypeBinding result = containingDeclaration(childBinding, qualifier, name);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private boolean isConflicting(IBinding binding, String name) {
        return binding.getName().equals(name);
    }

    private boolean isSameType(ITypeBinding binding, String qualifier, String name) {
        String qualifiedName = CodeFormatterUtil.concatenateName(qualifier, name);
        return binding.getQualifiedName().equals(qualifiedName);
    }

    private boolean isConflictingType(ITypeBinding binding, String qualifier, String name) {
        binding = binding.getTypeDeclaration();
        return !isSameType(binding, qualifier, name) && isConflicting(binding, name);
    }

    private IBinding[] getDeclarationsInScope() {
        if (fDeclarationsInScope == null) {
            ScopeAnalyzer analyzer = new ScopeAnalyzer(fCompilationUnit);
            fDeclarationsInScope =
                    analyzer.getDeclarationsInScope(fPosition, ScopeAnalyzer.METHODS | ScopeAnalyzer.TYPES
                                                               | ScopeAnalyzer.VARIABLES);
        }
        return fDeclarationsInScope;
    }

    private Name[] getImportedNames() {
        if (fImportedNames == null) {
            // IJavaProject project = null;
            // IJavaElement javaElement = fCompilationUnit.getJavaElement();
            // if (javaElement != null)
            // project = javaElement.getJavaProject();
            //
            // List<SimpleName> imports = new ArrayList<SimpleName>();
            // ImportReferencesCollector.collect(fCompilationUnit, project, null, imports, null);
            List<Name> imports = new ArrayList<Name>();
            for (Iterator iterator = fCompilationUnit.imports().iterator(); iterator.hasNext(); ) {
                ImportDeclaration im = (ImportDeclaration)iterator.next();
                imports.add(im.getName());
            }

            fImportedNames = imports.toArray(new Name[imports.size()]);
        }
        return fImportedNames;
    }
}
