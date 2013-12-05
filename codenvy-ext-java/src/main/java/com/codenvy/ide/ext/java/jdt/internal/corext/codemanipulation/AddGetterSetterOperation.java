/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Mateusz Wenus <mateusz.wenus@gmail.com> - [override method] generate in declaration order [code generation] - https://bugs.eclipse
 *     .org/bugs/show_bug.cgi?id=140971
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation;

import com.codenvy.ide.ext.java.jdt.core.Flags;
import com.codenvy.ide.ext.java.jdt.core.dom.*;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ListRewrite;
import com.codenvy.ide.ext.java.jdt.core.formatter.CodeFormatter;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ModifierRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.util.CodeFormatterUtil;

import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.codenvy.ide.runtime.*;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.edits.MalformedTreeException;
import com.codenvy.ide.text.edits.TextEdit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Workspace runnable to add accessor methods to fields.
 *
 * @since 3.1
 */
public final class AddGetterSetterOperation {

    /** The empty strings constant */
    private static final String[] EMPTY_STRINGS = new String[0];

    /** The accessor fields */
    private final IVariableBinding[] fAccessorFields;

    /** Should the resulting edit be applied? */
    private boolean fApply = true;

    /** The resulting text edit */
    private TextEdit fEdit = null;

    /** The getter fields */
    private final IVariableBinding[] fGetterFields;

    /** Should the compilation unit content be saved? */
    private final boolean fSave;

    /** The setter fields */
    private final IVariableBinding[] fSetterFields;

    /** The code generation settings to use */
    private final CodeGenerationSettings fSettings;

    /** Should all existing members be skipped? */
    private boolean fSkipAllExisting = false;

    /** Should the accessors be sorted? */
    private boolean fSort = false;

    /** The type declaration to add the constructors to */
    private final ITypeBinding fType;

    /** The compilation unit ast node */
    private final CompilationUnit fASTRoot;

    /** The visibility flags of the new accessors */
    private int fVisibility = Modifier.PUBLIC;

    private final TypeDeclaration typeDeclaration;

    private final int insertPos;

    private final Document document;

    /**
     * Creates a new add getter setter operation.
     *
     * @param type
     *         the type to add the accessors to
     * @param getters
     *         the fields to create getters for
     * @param setters
     *         the fields to create setters for
     * @param accessors
     *         the fields to create both
     * @param unit
     *         the compilation unit ast node
     * @param skipExistingQuery
     *         the request query
     * @param insert
     *         the insertion point, or <code>null</code>
     * @param settings
     *         the code generation settings to use
     * @param apply
     *         <code>true</code> if the resulting edit should be applied, <code>false</code> otherwise
     * @param save
     *         <code>true</code> if the changed compilation unit should be saved, <code>false</code> otherwise
     */
    public AddGetterSetterOperation(final ITypeBinding type, final TypeDeclaration typeDeclaration, int insertPos, Document document,
                                    final IVariableBinding[] getters, final IVariableBinding[] setters, final IVariableBinding[] accessors,
                                    final CompilationUnit unit, final CodeGenerationSettings settings, final boolean apply,
                                    final boolean save) {
        this.typeDeclaration = typeDeclaration;
        this.insertPos = insertPos;
        this.document = document;
        Assert.isNotNull(type);
        Assert.isNotNull(unit);
        Assert.isNotNull(settings);
        fType = type;
        fGetterFields = getters;
        fSetterFields = setters;
        fAccessorFields = accessors;
        fASTRoot = unit;
        fSettings = settings;
        fSave = save;
        fApply = apply;
    }

    /**
     * Adds a new accessor for the specified field.
     *
     * @param type
     *         the type
     * @param field
     *         the field
     * @param contents
     *         the contents of the accessor method
     * @param rewrite
     *         the list rewrite to use
     * @param insertion
     *         the insertion point
     * @throws JavaModelException
     *         if an error occurs
     */
    private void addNewAccessor(final ITypeBinding type, final IVariableBinding field, final String contents,
                                final ListRewrite rewrite, final ASTNode insertion) {
        final String delimiter = StubUtility.getLineDelimiterUsed();
        final MethodDeclaration declaration =
                (MethodDeclaration)rewrite.getASTRewrite().createStringPlaceholder(
                        CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, contents, 0, delimiter),
                        ASTNode.METHOD_DECLARATION);
        if (insertion != null)
            rewrite.insertBefore(declaration, insertion, null);
        else
            rewrite.insertLast(declaration, null);
    }

    /**
     * Generates a new getter method for the specified field
     *
     * @param field
     *         the field
     * @param rewrite
     *         the list rewrite to use
     * @throws CoreException
     *         if an error occurs
     * @throws OperationCanceledException
     *         if the operation has been cancelled
     */
    private void generateGetterMethod(final IVariableBinding field, final ListRewrite rewrite) throws CoreException,
                                                                                                      OperationCanceledException {
        final ITypeBinding type = field.getDeclaringClass();
        final String name = GetterSetterUtil.getGetterName(field, null);
        final MethodDeclaration existing = GetterSetterUtil.findMethod(name, EMPTY_STRINGS, false, typeDeclaration);
        if (existing == null || !querySkipExistingMethods(existing)) {
            BodyDeclaration sibling = null;
            int ins = 0;
            if (existing != null) {
                sibling = findNextSibling(existing);
                removeExistingAccessor(existing, rewrite);
                ins = sibling.getStartPosition();
            } else
                ins = insertPos;

            ASTNode insertion = StubUtility2.getNodeToInsertBefore(rewrite, ins);
            addNewAccessor(
                    type,
                    field,
                    GetterSetterUtil.getGetterStub(field, name, fSettings.createComments, fVisibility
                                                                                          | (field.getModifiers() & Flags.AccStatic)),
                    rewrite, insertion);
        }
    }

    /**
     * Returns the element after the give element.
     *
     * @param member
     *         a Java element
     * @return the next sibling of the given element or <code>null</code>
     * @throws JavaModelException
     *         thrown if the element could not be accessed
     */
    public static BodyDeclaration findNextSibling(BodyDeclaration member) {
        ASTNode parent = member.getParent();
        if (parent instanceof TypeDeclaration) {
            List<BodyDeclaration> elements = ((TypeDeclaration)parent).bodyDeclarations();
            for (int i = elements.size() - 2; i >= 0; i--) {
                if (member.equals(elements.get(i))) {
                    return elements.get(i + 1);
                }
            }
        }
        return null;
    }

    /**
     * Generates a new setter method for the specified field
     *
     * @param field
     *         the field
     * @param astRewrite
     *         the AST rewrite to use
     * @param rewrite
     *         the list rewrite to use
     * @throws CoreException
     *         if an error occurs
     * @throws OperationCanceledException
     *         if the operation has been cancelled
     */
    private void generateSetterMethod(final IVariableBinding field, ASTRewrite astRewrite, final ListRewrite rewrite)
            throws CoreException, OperationCanceledException {
        final ITypeBinding type = field.getDeclaringClass();
        final String name = GetterSetterUtil.getSetterName(field, null);
        final MethodDeclaration existing =
                GetterSetterUtil.findMethod(name, new String[]{field.getType().getQualifiedName()}, false, typeDeclaration);
        if (existing == null || !querySkipExistingMethods(existing)) {
            BodyDeclaration sibling = null;
            int ins = 0;
            if (existing != null) {
                sibling = findNextSibling(existing);
                removeExistingAccessor(existing, rewrite);
                ins = sibling.getStartPosition();
            } else
                ins = insertPos;
            ASTNode insertion = StubUtility2.getNodeToInsertBefore(rewrite, ins);
            addNewAccessor(
                    type,
                    field,
                    GetterSetterUtil.getSetterStub(field, name, fSettings.createComments, fVisibility
                                                                                          | (field.getModifiers() & Flags.AccStatic)),
                    rewrite, insertion);
            if (Flags.isFinal(field.getModifiers())) {

                ASTNode fieldDecl = getField(field);
                //               ASTNodes.getParent(NodeFinder.perform(fASTRoot, field.getNameRange()), FieldDeclaration.FIELD_DECLARATION);
                if (fieldDecl != null) {
                    ModifierRewrite.create(astRewrite, fieldDecl).setModifiers(0, Modifier.FINAL, null);
                }
            }

        }
    }

    /**
     * @param field
     * @return
     */
    private ASTNode getField(IVariableBinding field) {
        for (FieldDeclaration f : typeDeclaration.getFields()) {
            if (f.getType().resolveBinding().getBinaryName().equals(field.getType().getBinaryName())) {
                List<VariableDeclarationFragment> fragments = f.fragments();
                for (VariableDeclarationFragment frag : fragments) {
                    if (frag.getName().getFullyQualifiedName().equals(field.getName()))
                        return f;
                }

            }

        }
        return null;
    }

    /**
     * Returns the resulting text edit.
     *
     * @return the resulting text edit
     */
    public final TextEdit getResultingEdit() {
        return fEdit;
    }

    /**
     * Returns the visibility modifier of the generated constructors.
     *
     * @return the visibility modifier
     */
    public final int getVisibility() {
        return fVisibility;
    }

    /**
     * Should all existing members be skipped?
     *
     * @return <code>true</code> if they should be skipped, <code>false</code> otherwise
     */
    public final boolean isSkipAllExisting() {
        return fSkipAllExisting;
    }

    /**
     * Queries the user whether to skip existing methods.
     *
     * @param method
     *         the method in question
     * @return <code>true</code> to skip existing methods, <code>false</code> otherwise
     * @throws OperationCanceledException
     *         if the operation has been cancelled
     */
    private boolean querySkipExistingMethods(final MethodDeclaration method) throws OperationCanceledException {
        if (!fSkipAllExisting) {
            //TODO
            //         switch (fSkipExistingQuery.doQuery(method))
            //         {
            //            case IRequestQuery.CANCEL :
            //               throw new OperationCanceledException();
            //            case IRequestQuery.NO :
            //               return false;
            //            case IRequestQuery.YES_ALL :
            //               fSkipAllExisting = true;
            //         }
            return false;
        }
        return true;
    }

    /**
     * Removes an existing accessor method.
     *
     * @param accessor
     *         the accessor method to remove
     * @param rewrite
     *         the list rewrite to use
     */
    private void removeExistingAccessor(final MethodDeclaration accessor, final ListRewrite rewrite) {
        final MethodDeclaration declaration =
                (MethodDeclaration)ASTNodes.getParent(NodeFinder.perform(rewrite.getParent().getRoot(), accessor.getName()
                                                                                                                .getStartPosition(),
                                                                         accessor.getName().getLength()),
                                                      MethodDeclaration.METHOD_DECLARATION);
        if (declaration != null)
            rewrite.remove(declaration, null);
    }

    /*
     * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public final void run() throws CoreException {
        try {
            //         monitor.setTaskName(CodeGenerationMessages.AddGetterSetterOperation_description);
            //         monitor.beginTask("", fGetterFields.length + fSetterFields.length); //$NON-NLS-1$
            //         final ICompilationUnit unit = fType.getCompilationUnit();
            final ASTRewrite astRewrite = ASTRewrite.create(fASTRoot.getAST());
            ListRewrite listRewriter = null;
            if (fType.isAnonymous()) {
                final ClassInstanceCreation creation =
                        (ClassInstanceCreation)ASTNodes.getParent(NodeFinder.perform(fASTRoot, typeDeclaration.getName()
                                                                                                              .getStartPosition(),
                                                                                     typeDeclaration.getName().getLength()),
                                                                  ClassInstanceCreation.CLASS_INSTANCE_CREATION);
                if (creation != null) {
                    final AnonymousClassDeclaration declaration = creation.getAnonymousClassDeclaration();
                    if (declaration != null)
                        listRewriter =
                                astRewrite.getListRewrite(declaration, AnonymousClassDeclaration.BODY_DECLARATIONS_PROPERTY);
                }
            } else {
                final AbstractTypeDeclaration declaration =
                        (AbstractTypeDeclaration)ASTNodes.getParent(NodeFinder.perform(fASTRoot, typeDeclaration.getName()
                                                                                                                .getStartPosition(),
                                                                                       typeDeclaration.getName().getLength()),
                                                                    AbstractTypeDeclaration.TYPE_DECLARATION);
                if (declaration != null)
                    listRewriter = astRewrite.getListRewrite(declaration, declaration.getBodyDeclarationsProperty());
            }
            if (listRewriter == null) {
                throw new CoreException(new Status(IStatus.ERROR, "", IStatus.ERROR,
                                                   "Input type not found", null));
            }

            fSkipAllExisting = false;

            Set<IVariableBinding> accessors = new HashSet<IVariableBinding>(Arrays.asList(fAccessorFields));
            Set<IVariableBinding> getters = new HashSet<IVariableBinding>(Arrays.asList(fGetterFields));
            Set<IVariableBinding> setters = new HashSet<IVariableBinding>(Arrays.asList(fSetterFields));
            IVariableBinding[] fields = fType.getDeclaredFields(); // generate methods in order of field declarations
            if (!fSort) {
                for (int i = 0; i < fields.length; i++) {
                    if (accessors.contains(fields[i])) {
                        generateGetterMethod(fields[i], listRewriter);
                        generateSetterMethod(fields[i], astRewrite, listRewriter);
                    }
                }
            }
            for (int i = 0; i < fields.length; i++) {
                if (getters.contains(fields[i])) {
                    generateGetterMethod(fields[i], listRewriter);
                }
            }
            for (int i = 0; i < fields.length; i++) {
                if (setters.contains(fields[i])) {
                    generateSetterMethod(fields[i], astRewrite, listRewriter);
                }
            }
            fEdit = astRewrite.rewriteAST(document, WorkerMessageHandler.get().getOptions());
            if (fApply) {
                fEdit.apply(document);
            }
        } catch (MalformedTreeException ignore) {
        } catch (BadLocationException ignore) {
        }
    }

    /**
     * Determines whether existing members should be skipped.
     *
     * @param skip
     *         <code>true</code> to skip existing members, <code>false</code> otherwise
     */
    public final void setSkipAllExisting(final boolean skip) {
        fSkipAllExisting = skip;
    }

    public void setSort(boolean sort) {
        fSort = sort;
    }

    /**
     * Sets the visibility modifier of the generated constructors.
     *
     * @param visibility
     *         the visibility modifier
     */
    public final void setVisibility(final int visibility) {
        fVisibility = visibility;
    }
}
