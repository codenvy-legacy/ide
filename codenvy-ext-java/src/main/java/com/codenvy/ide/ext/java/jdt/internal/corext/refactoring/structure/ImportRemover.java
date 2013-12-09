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
package com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.structure;

import com.codenvy.ide.ext.java.jdt.core.dom.*;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ImportReferencesCollector;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Bindings;

import java.util.*;


public class ImportRemover {

    private static class StaticImportData {

        private boolean fField;

        private String fMember;

        private String fQualifier;

        private StaticImportData(String qualifier, String member, boolean field) {
            fQualifier = qualifier;
            fMember = member;
            fField = field;
        }
    }

    private Set<String> fAddedImports = new HashSet<String>();

    private Set<StaticImportData> fAddedStaticImports = new HashSet<StaticImportData>();

    private List<ASTNode> fRemovedNodes = new ArrayList<ASTNode>();

    private List<ImportDeclaration> fInlinedStaticImports = new ArrayList<ImportDeclaration>();

    private final CompilationUnit fRoot;

    public ImportRemover(CompilationUnit root) {
        fRoot = root;
    }

    private void divideTypeRefs(List<SimpleName> importNames, List<SimpleName> staticNames,
                                List<SimpleName> removedRefs, List<SimpleName> unremovedRefs) {
        int[] removedStartsEnds = new int[2 * fRemovedNodes.size()];
        for (int index = 0; index < fRemovedNodes.size(); index++) {
            ASTNode node = fRemovedNodes.get(index);
            int start = node.getStartPosition();
            removedStartsEnds[2 * index] = start;
            removedStartsEnds[2 * index + 1] = start + node.getLength();
        }
        for (Iterator<SimpleName> iterator = importNames.iterator(); iterator.hasNext(); ) {
            SimpleName name = iterator.next();
            if (isInRemoved(name, removedStartsEnds))
                removedRefs.add(name);
            else
                unremovedRefs.add(name);
        }
        for (Iterator<SimpleName> iterator = staticNames.iterator(); iterator.hasNext(); ) {
            SimpleName name = iterator.next();
            if (isInRemoved(name, removedStartsEnds))
                removedRefs.add(name);
            else
                unremovedRefs.add(name);
        }
        for (Iterator<ImportDeclaration> iterator = fInlinedStaticImports.iterator(); iterator.hasNext(); ) {
            ImportDeclaration importDecl = iterator.next();
            Name name = importDecl.getName();
            if (name instanceof QualifiedName)
                name = ((QualifiedName)name).getName();
            removedRefs.add((SimpleName)name);
        }
    }

    public IBinding[] getImportsToRemove() {
        ArrayList<SimpleName> importNames = new ArrayList<SimpleName>();
        ArrayList<SimpleName> staticNames = new ArrayList<SimpleName>();

        ImportReferencesCollector.collect(fRoot, null, importNames, staticNames);

        List<SimpleName> removedRefs = new ArrayList<SimpleName>();
        List<SimpleName> unremovedRefs = new ArrayList<SimpleName>();
        divideTypeRefs(importNames, staticNames, removedRefs, unremovedRefs);
        if (removedRefs.size() == 0)
            return new IBinding[0];

        HashMap<String, IBinding> potentialRemoves = getPotentialRemoves(removedRefs);
        for (Iterator<SimpleName> iterator = unremovedRefs.iterator(); iterator.hasNext(); ) {
            SimpleName name = iterator.next();
            potentialRemoves.remove(name.getIdentifier());
        }

        Collection<IBinding> importsToRemove = potentialRemoves.values();
        return importsToRemove.toArray(new IBinding[importsToRemove.size()]);
    }

    private HashMap<String, IBinding> getPotentialRemoves(List<SimpleName> removedRefs) {
        HashMap<String, IBinding> potentialRemoves = new HashMap<String, IBinding>();
        for (Iterator<SimpleName> iterator = removedRefs.iterator(); iterator.hasNext(); ) {
            SimpleName name = iterator.next();
            if (fAddedImports.contains(name.getIdentifier()) || hasAddedStaticImport(name))
                continue;
            IBinding binding = name.resolveBinding();
            if (binding != null)
                potentialRemoves.put(name.getIdentifier(), binding);
        }
        return potentialRemoves;
    }

    private boolean hasAddedStaticImport(SimpleName name) {
        IBinding binding = name.resolveBinding();
        if (binding instanceof IVariableBinding) {
            IVariableBinding variable = (IVariableBinding)binding;
            return hasAddedStaticImport(variable.getDeclaringClass().getQualifiedName(), variable.getName(), true);
        } else if (binding instanceof IMethodBinding) {
            IMethodBinding method = (IMethodBinding)binding;
            return hasAddedStaticImport(method.getDeclaringClass().getQualifiedName(), method.getName(), false);
        }
        return false;
    }

    private boolean hasAddedStaticImport(String qualifier, String member, boolean field) {
        StaticImportData data = null;
        for (final Iterator<StaticImportData> iterator = fAddedStaticImports.iterator(); iterator.hasNext(); ) {
            data = iterator.next();
            if (data.fQualifier.equals(qualifier) && data.fMember.equals(member) && data.fField == field)
                return true;
        }
        return false;
    }

    public boolean hasRemovedNodes() {
        return fRemovedNodes.size() != 0 || fInlinedStaticImports.size() != 0;
    }

    private boolean isInRemoved(SimpleName ref, int[] removedStartsEnds) {
        int start = ref.getStartPosition();
        int end = start + ref.getLength();
        for (int index = 0; index < removedStartsEnds.length; index += 2) {
            if (start >= removedStartsEnds[index] && end <= removedStartsEnds[index + 1])
                return true;
        }
        return false;
    }

    public void registerAddedImport(String typeName) {
        int dot = typeName.lastIndexOf('.');
        if (dot == -1)
            fAddedImports.add(typeName);
        else
            fAddedImports.add(typeName.substring(dot + 1));
    }

    public void registerAddedImports(Type newTypeNode) {
        newTypeNode.accept(new ASTVisitor(true) {

            private void addName(SimpleName name) {
                fAddedImports.add(name.getIdentifier());
            }

            @Override
            public boolean visit(QualifiedName node) {
                addName(node.getName());
                return false;
            }

            @Override
            public boolean visit(QualifiedType node) {
                addName(node.getName());
                return false;
            }

            @Override
            public boolean visit(SimpleName node) {
                addName(node);
                return false;
            }
        });
    }

    public void registerAddedStaticImport(String qualifier, String member, boolean field) {
        fAddedStaticImports.add(new StaticImportData(qualifier, member, field));
    }

    public void registerAddedStaticImport(IBinding binding) {
        if (binding instanceof IVariableBinding) {
            ITypeBinding declaringType = ((IVariableBinding)binding).getDeclaringClass();
            fAddedStaticImports.add(new StaticImportData(Bindings.getRawQualifiedName(declaringType), binding.getName(),
                                                         true));

        } else if (binding instanceof IMethodBinding) {
            ITypeBinding declaringType = ((IMethodBinding)binding).getDeclaringClass();
            fAddedStaticImports.add(new StaticImportData(Bindings.getRawQualifiedName(declaringType), binding.getName(),
                                                         false));

        } else {
            throw new IllegalArgumentException(binding.toString());
        }
    }

    public void registerRemovedNode(ASTNode removed) {
        fRemovedNodes.add(removed);
    }

    public void applyRemoves(ImportRewrite importRewrite) {
        IBinding[] bindings = getImportsToRemove();
        for (int i = 0; i < bindings.length; i++) {
            if (bindings[i] instanceof ITypeBinding) {
                ITypeBinding typeBinding = (ITypeBinding)bindings[i];
                importRewrite.removeImport(typeBinding.getTypeDeclaration().getQualifiedName());
            } else if (bindings[i] instanceof IMethodBinding) {
                IMethodBinding binding = (IMethodBinding)bindings[i];
                importRewrite.removeStaticImport(binding.getDeclaringClass().getQualifiedName() + '.' + binding.getName());
            } else if (bindings[i] instanceof IVariableBinding) {
                IVariableBinding binding = (IVariableBinding)bindings[i];
                importRewrite.removeStaticImport(binding.getDeclaringClass().getQualifiedName() + '.' + binding.getName());
            }
        }
    }

    public void registerInlinedStaticImport(ImportDeclaration importDecl) {
        fInlinedStaticImports.add(importDecl);
    }
}
