/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.text.correction;

import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.NodeFinder;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ASTResolving;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;
import com.codenvy.ide.ext.java.jdt.quickassist.api.TextInvocationContext;
import com.codenvy.ide.text.Document;


public class AssistContext extends TextInvocationContext implements InvocationContext {

    private CompilationUnit fASTRoot;

    private int fOffset;

    private int fLength;

    /** The cached node finder, can be null. */
    private NodeFinder fNodeFinder;

    private final Document document;


   /**
    * Constructor for AssistContext.
    */
    public AssistContext(Document document, int offset, int length) {
        super(offset, length);
        this.document = document;
        fOffset = offset;
        fLength = length;
    }

    /**
     * @param document
     * @param documentOffset
     * @param length
     * @param cu
     */
    public AssistContext(Document document, int documentOffset, int length,
                         CompilationUnit cu) {
        this(document, documentOffset, length);
        fASTRoot = cu;
    }

    /**
     * Returns the length.
     *
     * @return int
     */
    @Override
    public int getSelectionLength() {
        return Math.max(getLength(), 0);
    }

    /**
     * Returns the offset.
     *
     * @return int
     */
    @Override
    public int getSelectionOffset() {
        return getOffset();
    }

    /** {@inheritDoc} */
    @Override
    public CompilationUnit getASTRoot() {
        if (fASTRoot == null) {
            //TODO
            //         fASTRoot = SharedASTProvider.getAST(fCompilationUnit, fWaitFlag, null);
            //         if (fASTRoot == null)
            //         {
            //            // see bug 63554
            //         }
            fASTRoot = ASTResolving.createQuickFixAST(document);
        }
        return fASTRoot;
    }


    /** {@inheritDoc} */
    public int getOffset() {
        return fOffset;
    }


    /** {@inheritDoc} */
    public int getLength() {
        return fLength;
    }

    /**
     * @param root
     *         The ASTRoot to set.
     */
    public void setASTRoot(CompilationUnit root) {
        fASTRoot = root;
    }


    /** {@inheritDoc} */
    @Override
    public ASTNode getCoveringNode() {
        if (fNodeFinder == null) {
            fNodeFinder = new NodeFinder(getASTRoot(), getOffset(), getLength());
        }
        return fNodeFinder.getCoveringNode();
    }


    /** {@inheritDoc} */
    @Override
    public ASTNode getCoveredNode() {
        if (fNodeFinder == null) {
            fNodeFinder = new NodeFinder(getASTRoot(), getOffset(), getLength());
        }
        return fNodeFinder.getCoveredNode();
    }

    /** {@inheritDoc} */
    @Override
    public Document getDocument() {
        return document;
    }

}
