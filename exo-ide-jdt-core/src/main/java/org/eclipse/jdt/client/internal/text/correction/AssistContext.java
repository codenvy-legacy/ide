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
package org.eclipse.jdt.client.internal.text.correction;

import org.eclipse.jdt.client.codeassistant.api.IInvocationContext;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.NodeFinder;
import org.eclipse.jdt.client.internal.corext.codemanipulation.ASTResolving;
import org.exoplatform.ide.editor.text.IDocument;

public class AssistContext implements IInvocationContext
{

   //	private final ICompilationUnit fCompilationUnit;
   //	private final IEditorPart fEditor;

   private CompilationUnit fASTRoot;

   //	private final SharedASTProvider.WAIT_FLAG fWaitFlag;

   private int fOffset;

   private int fLength;

   /**
    * The cached node finder, can be null.
    * @since 3.6
    */
   private NodeFinder fNodeFinder;

   private final IDocument document;

   //TODO
   //	/*
   //	 * @since 3.5
   //	 */
   //	private AssistContext(ICompilationUnit cu, ISourceViewer sourceViewer, IEditorPart editor, int offset, int length, SharedASTProvider.WAIT_FLAG waitFlag) {
   //		super(sourceViewer, offset, length);
   //		Assert.isLegal(cu != null);
   //		Assert.isLegal(waitFlag != null);
   //		fCompilationUnit= cu;
   //		fEditor= editor;
   //		fWaitFlag= waitFlag;
   //	}
   //	
   //	/*
   //	 * @since 3.5
   //	 */
   //	public AssistContext(ICompilationUnit cu, ISourceViewer sourceViewer, int offset, int length, SharedASTProvider.WAIT_FLAG waitFlag) {
   //		this(cu, sourceViewer, null, offset, length, waitFlag);
   //	}
   //	
   //	/*
   //	 * @since 3.5
   //	 */
   //	public AssistContext(ICompilationUnit cu, ISourceViewer sourceViewer, IEditorPart editor, int offset, int length) {
   //		this(cu, sourceViewer, editor, offset, length, SharedASTProvider.WAIT_YES);
   //	}
   //	
   //	/*
   //	 * Constructor for CorrectionContext.
   //	 * @since 3.4
   //	 */
   //	public AssistContext(ICompilationUnit cu, ISourceViewer sourceViewer, int offset, int length) {
   //		this(cu, sourceViewer, null, offset, length);
   //	}
   //
   /*
    * Constructor for CorrectionContext.
    */
   public AssistContext(IDocument document, int offset, int length)
   {
      this.document = document;
      fOffset = offset;
      fLength = length;
   }

   //	/**
   //	 * Returns the compilation unit.
   //	 * @return an <code>ICompilationUnit</code>
   //	 */
   //	public ICompilationUnit getCompilationUnit() {
   //		return fCompilationUnit;
   //	}

   //	/**
   //	 * Returns the editor or <code>null</code> if none.
   //	 * @return an <code>IEditorPart</code> or <code>null</code> if none
   //	 * @since 3.5
   //	 */
   //	public IEditorPart getEditor() {
   //		return fEditor;
   //	}

   /**
    * Returns the length.
    * @return int
    */
   public int getSelectionLength()
   {
      return Math.max(getLength(), 0);
   }

   /**
    * Returns the offset.
    * @return int
    */
   public int getSelectionOffset()
   {
      return getOffset();
   }

   public CompilationUnit getASTRoot()
   {
      if (fASTRoot == null)
      {
         //TODO
         //         fASTRoot = SharedASTProvider.getAST(fCompilationUnit, fWaitFlag, null);
         //         if (fASTRoot == null)
         //         {
         //            // see bug 63554
         //         }
         fASTRoot = ASTResolving.createQuickFixAST(document, null);
      }
      return fASTRoot;
   }

   /*
    * @see org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext#getOffset()
    */
   public int getOffset()
   {
      return fOffset;
   }

   /*
    * @see org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext#getLength()
    */
   public int getLength()
   {
      return fLength;
   }

   /**
    * @param root The ASTRoot to set.
    */
   public void setASTRoot(CompilationUnit root)
   {
      fASTRoot = root;
   }

   /*(non-Javadoc)
    * @see org.eclipse.jdt.ui.text.java.IInvocationContext#getCoveringNode()
    */
   public ASTNode getCoveringNode()
   {
      if (fNodeFinder == null)
      {
         fNodeFinder = new NodeFinder(getASTRoot(), getOffset(), getLength());
      }
      return fNodeFinder.getCoveringNode();
   }

   /*(non-Javadoc)
    * @see org.eclipse.jdt.ui.text.java.IInvocationContext#getCoveredNode()
    */
   public ASTNode getCoveredNode()
   {
      if (fNodeFinder == null)
      {
         fNodeFinder = new NodeFinder(getASTRoot(), getOffset(), getLength());
      }
      return fNodeFinder.getCoveredNode();
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.api.IInvocationContext#getDocument()
    */
   @Override
   public IDocument getDocument()
   {
      return null;
   }

}
