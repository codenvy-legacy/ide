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
package com.codenvy.ide.ext.java.jdt.refactoring;

import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.edits.MalformedTreeException;
import com.codenvy.ide.text.edits.UndoEdit;


/**
 * A text change that operates directly on instances of {@link IDocument}.
 * The document change uses a simple length compare to check if it
 * is still valid. So as long as its length hasn't changed the text edits
 * managed have a valid range and can be applied to the document. The
 * same applies to the undo change returned from the perform method.
 * <p/>
 * <p>
 * Note: this class is not intended to be extended by clients.
 * </p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @since 3.0
 */
public class DocumentChange extends TextChange {

    private Document fDocument;

    private int fLength;

    /**
     * Creates a new <code>DocumentChange</code> for the given
     * {@link IDocument}.
     *
     * @param name
     *         the change's name. Has to be a human readable name.
     * @param document
     *         the document this change is working on
     */
    public DocumentChange(String name, Document document) {
        super(name);
        Assert.isNotNull(document);
        fDocument = document;
    }

    /** {@inheritDoc} */
    public Object getModifiedElement() {
        return fDocument;
    }

    /** {@inheritDoc} */
    public void initializeValidationData() {
        // as long as we don't have modification stamps on documents
        // we can only remember its length.
        fLength = fDocument.getLength();
    }

    /** {@inheritDoc} */
    public RefactoringStatus isValid() throws CoreException {
        RefactoringStatus result = TextChanges.isValid(fDocument, fLength);
        return result;
    }

    /** {@inheritDoc} */
    protected Document acquireDocument() throws CoreException {
        return fDocument;
    }

    /** {@inheritDoc} */
    protected void commit(Document document) throws CoreException {
        // do nothing
    }

    /** {@inheritDoc} */
    protected void releaseDocument(Document document) throws CoreException {
        //do nothing
    }

    /*
     * @see org.eclipse.ltk.core.refactoring.TextChange#performEdits(org.eclipse.jface.text.IDocument)
     * @since 3.6
     */
    protected UndoEdit performEdits(final Document document) throws BadLocationException, MalformedTreeException {
        //      ITextFileBufferManager fileBufferManager = FileBuffers.getTextFileBufferManager();
        //
        //      ITextFileBuffer fileBuffer = fileBufferManager.getTextFileBuffer(document);
        //      if (fileBuffer == null || !fileBuffer.isSynchronizationContextRequested())
        //      {
        //TODO
        return super.performEdits(document);
        //      }
        //
        //      final UndoEdit[] result = new UndoEdit[1];
        //      final BadLocationException[] exception = new BadLocationException[1];
        //      Runnable runnable = new Runnable()
        //      {
        //         public void run()
        //         {
        //               try
        //               {
        //                  result[0] = DocumentChange.super.performEdits(document);
        //               }
        //               catch (BadLocationException e)
        //               {
        //                  exception[0] = e;
        //               }
        //         }
        //      };
        //
        //         fileBufferManager.execute(runnable);
        //         while (!completionLock.fDone)
        //         {
        //            try
        //            {
        //               completionLock.wait(500);
        //            }
        //            catch (InterruptedException x)
        //            {
        //            }
        //         }
        //
        //      if (exception[0] != null)
        //      {
        //         throw exception[0];
        //      }
        //
        //      return result[0];
    }

    /** {@inheritDoc} */
    protected Change createUndoChange(UndoEdit edit) {
        return new UndoDocumentChange(getName(), fDocument, edit);
    }
}
