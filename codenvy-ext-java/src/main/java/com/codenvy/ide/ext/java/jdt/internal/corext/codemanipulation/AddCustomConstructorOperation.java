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
package com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation;

import com.codenvy.ide.ext.java.jdt.core.dom.*;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ListRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;

import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.edits.MalformedTreeException;
import com.codenvy.ide.text.edits.MultiTextEdit;
import com.codenvy.ide.text.edits.TextEdit;


/**
 * Workspace runnable to add custom constructors initializing fields.
 *
 * @since 3.1
 */
public final class AddCustomConstructorOperation {

    /** The super constructor method binding */
    private final IMethodBinding fConstructorBinding;

    /** The variable bindings to implement */
    private final IVariableBinding[] fFieldBindings;

    /** The resulting text edit */
    private TextEdit fResultingEdit = null;

    /** Should the call to the super constructor be omitted? */
    private boolean fOmitSuper = false;

    /** The code generation settings to use */
    private final CodeGenerationSettings fSettings;

    /** The type declaration to add the constructors to */
    private final ITypeBinding fParentType;

    /** The compilation unit ast node */
    private final CompilationUnit fASTRoot;

    /** The visibility flags of the new constructor */
    private int fVisibility = 0;

    private final Document document;

    private final int insert;

    /**
     * Creates a new add custom constructor operation.
     *
     * @param astRoot
     *         the compilation unit ast node
     * @param parentType
     *         the type to add the methods to
     * @param variables
     *         the variable bindings to use in the constructor
     * @param constructor
     *         the method binding of the super constructor
     * @param insert
     *         the insertion point, or <code>null</code>
     * @param settings
     *         the code generation settings to use
     * @param apply
     *         <code>true</code> if the resulting edit should be applied, <code>false</code> otherwise
     * @param save
     *         <code>true</code> if the changed compilation unit should be saved, <code>false</code> otherwise
     */
    public AddCustomConstructorOperation(CompilationUnit astRoot, ITypeBinding parentType, IVariableBinding[] variables,
                                         IMethodBinding constructor, int insert, CodeGenerationSettings settings, Document document) {
        this.insert = insert;
        this.document = document;
        Assert.isTrue(astRoot != null);
        Assert.isNotNull(parentType);
        Assert.isNotNull(variables);
        Assert.isNotNull(constructor);
        Assert.isNotNull(settings);
        fParentType = parentType;
        fASTRoot = astRoot;
        fFieldBindings = variables;
        fConstructorBinding = constructor;
        fSettings = settings;
    }

    /**
     * Returns the resulting text edit.
     *
     * @return the resulting text edit
     */
    public final TextEdit getResultingEdit() {
        return fResultingEdit;
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
     * Should the call to the super constructor be omitted?
     *
     * @return <code>true</code> to omit the call, <code>false</code> otherwise
     */
    public final boolean isOmitSuper() {
        return fOmitSuper;
    }

    public final void run() {

        ASTRewrite astRewrite = ASTRewrite.create(fASTRoot.getAST());
        ImportRewrite importRewrite = StubUtility.createImportRewrite(document, fASTRoot, true);

        ListRewrite listRewriter = null;

        ASTNode typeDecl = fASTRoot.findDeclaringNode(fParentType);
        if (typeDecl instanceof AbstractTypeDeclaration) {
            listRewriter =
                    astRewrite.getListRewrite(typeDecl, ((AbstractTypeDeclaration)typeDecl).getBodyDeclarationsProperty());
        } else if (typeDecl instanceof AnonymousClassDeclaration) {
            listRewriter = astRewrite.getListRewrite(typeDecl, AnonymousClassDeclaration.BODY_DECLARATIONS_PROPERTY);
        }

        if (listRewriter != null) {
            ImportRewriteContext context = new ContextSensitiveImportRewriteContext(typeDecl, importRewrite);
            MethodDeclaration stub =
                    StubUtility2.createConstructorStub(astRewrite, importRewrite, context, fParentType, fOmitSuper ? null
                                                                                                                   : fConstructorBinding,
                                                       fFieldBindings, fVisibility, fSettings);
            if (stub != null) {
                ASTNode insertion = StubUtility2.getNodeToInsertBefore(listRewriter, insert);
                if (insertion != null && insertion.getParent() == typeDecl) {
                    listRewriter.insertBefore(stub, insertion, null);
                } else {
                    listRewriter.insertLast(stub, null);
                }
            }
            fResultingEdit = new MultiTextEdit();
            fResultingEdit.addChild(astRewrite.rewriteAST(document, WorkerMessageHandler.get().getOptions()));
            fResultingEdit.addChild(importRewrite.rewriteImports());
            try {
                fResultingEdit.apply(document);
            } catch (MalformedTreeException e) {
                e.printStackTrace();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Determines whether the call to the super constructor should be omitted.
     *
     * @param omit
     *         <code>true</code> to omit the call, <code>false</code> otherwise
     */
    public final void setOmitSuper(final boolean omit) {
        fOmitSuper = omit;
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
