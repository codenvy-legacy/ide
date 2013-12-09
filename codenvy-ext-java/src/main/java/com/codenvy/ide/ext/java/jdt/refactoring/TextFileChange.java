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

import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.edits.MalformedTreeException;
import com.codenvy.ide.text.edits.UndoEdit;

/**
 * A special {@link TextChange} that operates on a <code>IFile</code>.
 * <p>
 * As of 3.1 the content stamp managed by a text file change maps to the modification
 * stamp of its underlying <code>IFile</code>. Undoing a text file change will
 * roll back the modification stamp of a resource to its original value using
 * the new API {@link org.eclipse.core.resources.IResource#revertModificationStamp(long)}
 * </p>
 * <p>
 * The class should be subclassed by clients which need to perform
 * special operation when acquiring or releasing a document.
 * </p>
 *
 * @since 3.0
 */
public class TextFileChange extends TextChange {

    /**
     * Flag (value 1) indicating that the file's save state has to be kept. This means an
     * unsaved file is still unsaved after performing the change and a saved one
     * will be saved.
     */
    public static final int KEEP_SAVE_STATE = 1 << 0;

    /** Flag (value 2) indicating that the file is to be saved after the change has been applied. */
    public static final int FORCE_SAVE = 1 << 1;

    /** Flag (value 4) indicating that the file will not be saved after the change has been applied. */
    public static final int LEAVE_DIRTY = 1 << 2;

    // the file to change
    //	private IFile fFile;
    private int fSaveMode = KEEP_SAVE_STATE;

    // the mapped text buffer
    private int fAcquireCount;

    private final Document document;

    //	private ITextFileBuffer fBuffer;
//   	private BufferValidationState fValidationState;
    //	private ContentStamp fContentStamp;

    /**
     * Creates a new <code>TextFileChange</code> for the given file.
     *
     * @param name
     *         the change's name mainly used to render the change in the UI
     * @param file
     *         the file this text change operates on
     */
    public TextFileChange(String name, Document document) {
        super(name);
        //		Assert.isNotNull(file);
        //		fFile= file;
        this.document = document;
    }

    /**
     * Sets the save state. Must be one of <code>KEEP_SAVE_STATE</code>,
     * <code>FORCE_SAVE</code> or <code>LEAVE_DIRTY</code>.
     *
     * @param saveMode
     *         indicating how save is handled when the document
     *         gets committed
     */
    public void setSaveMode(int saveMode) {
        fSaveMode = saveMode;
    }

    /**
     * Returns the save state set via {@link #setSaveMode(int)}.
     *
     * @return the save state
     */
    public int getSaveMode() {
        return fSaveMode;
    }

    //   /**
    //    * Hook to create an undo change for the given undo edit and content stamp.
    //    * This hook gets called while performing the change to construct the
    //    * corresponding undo change object.
    //    *
    //    * @param edit the {@link UndoEdit} to create an undo change for
    //    * @param stampToRestore the content stamp to restore when the undo
    //    *  edit is executed.
    //    *
    //    * @return the undo change or <code>null</code> if no undo change can
    //    *  be created. Returning <code>null</code> results in the fact that
    //    *  the whole change tree can't be undone. So returning <code>null</code>
    //    *  is only recommended if an exception occurred during creating the
    //    *  undo change.
    //    */
    //   protected Change createUndoChange(UndoEdit edit)
    //   {
    //      //TODO
    ////      return new UndoTextFileChange(getName(), fFile, edit, stampToRestore, fSaveMode);
    //      return null;
    //   }

    //	/**
    //	 * {@inheritDoc}
    //	 */
    //	public Object getModifiedElement(){
    //		return fFile;
    //	}

    @Override
    public Object[] getAffectedObjects() {
        Object modifiedElement = getModifiedElement();
        if (modifiedElement == null) {
            return null;
        }
        return new Object[]{modifiedElement};
    }

    /** {@inheritDoc} */
    @Override
    public void initializeValidationData() {
        //TODO
        //      if (monitor == null)
        //         monitor = new NullProgressMonitor();
        //      try
        //      {
        //         monitor.beginTask("", 1); //$NON-NLS-1$
        //         fValidationState = BufferValidationState.create(fFile);
        //      }
        //      finally
        //      {
        //         monitor.done();
        //      }
    }

    /** {@inheritDoc} */
    @Override
    public RefactoringStatus isValid() throws CoreException {
        //TODO
//            if (monitor == null)
//               monitor = new NullProgressMonitor();
//            try
//            {
//               monitor.beginTask("", 1); //$NON-NLS-1$
//               if (fValidationState == null)
//                  throw new CoreException(new Status(IStatus.ERROR, "",
//                     "TextFileChange has not been initialialized")); //$NON-NLS-1$
//      
//               boolean needsSaving = needsSaving();
//               RefactoringStatus result = fValidationState.isValid(needsSaving);
//               if (needsSaving)
//               {
//                  result.merge(Changes.validateModifiesFiles(new IFile[]{fFile}));
//               }
//               else
//               {
//                  // we are reading the file. So it should be at least in sync
//                  result.merge(Changes.checkInSync(new IFile[]{fFile}));
//               }
//               return result;
//            }
//            finally
//            {
//               monitor.done();
//            }
//      return null;
        return new RefactoringStatus();
    }

    /** {@inheritDoc} */
    @Override
    public void dispose() {
        //		if (fValidationState != null) {
        //			fValidationState.dispose();
        //		}
    }

    /** {@inheritDoc} */
    @Override
    protected Document acquireDocument() throws CoreException {
        //TODO
        //		fAcquireCount++;
        //		if (fAcquireCount > 1)
        //			return fBuffer.getDocument();
        //
        //		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
        //		IPath path= fFile.getFullPath();
        //		manager.connect(path, LocationKind.IFILE, pm);
        //		fBuffer= manager.getTextFileBuffer(path, LocationKind.IFILE);
        //		IDocument result= fBuffer.getDocument();
        //		fContentStamp= ContentStamps.get(fFile, result);
        //		return result;
        return document;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation of this method only commits the underlying buffer if
     * {@link #needsSaving()} and {@link #isDocumentModified()} returns <code>true</code>.
     * </p>
     */
    protected void commit(Document document) throws CoreException {
        //TODO
        //      if (needsSaving())
        //      {
        //         fBuffer.commit(pm, false);
        //      }
    }

    /** {@inheritDoc} */
    protected void releaseDocument(Document document) throws CoreException {
        //		Assert.isTrue(fAcquireCount > 0);
        //		if (fAcquireCount == 1) {
        //			ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
        //			manager.disconnect(fFile.getFullPath(), LocationKind.IFILE, pm);
        //		}
        //		fAcquireCount--;
    }

    /** {@inheritDoc} */
    protected final Change createUndoChange(UndoEdit edit) {
        //TODO
        //      return createUndoChange(edit);
        return null;
    }

    /*
     * @see org.eclipse.ltk.core.refactoring.TextChange#performEdits(org.eclipse.jface.text.IDocument)
     * @since 3.5
     */
    protected UndoEdit performEdits(final Document document) throws BadLocationException, MalformedTreeException {
        //		if (! fBuffer.isSynchronizationContextRequested()) {
        return super.performEdits(document);
        //		}

        //		ITextFileBufferManager fileBufferManager= FileBuffers.getTextFileBufferManager();

        /** The lock for waiting for computation in the UI thread to complete. */
        //TODO
        //		final Lock completionLock= new Lock();
        //		final UndoEdit[] result= new UndoEdit[1];
        //		final BadLocationException[] exception= new BadLocationException[1];
        //		Runnable runnable= new Runnable() {
        //			public void run() {
        //				synchronized (completionLock) {
        //					try {
        //						result[0]= TextFileChange.super.performEdits(document);
        //					} catch (BadLocationException e) {
        //						exception[0]= e;
        //					} finally {
        //						completionLock.fDone= true;
        //						completionLock.notifyAll();
        //					}
        //				}
        //			}
        //		};
        //
        //		synchronized (completionLock) {
        //			fileBufferManager.execute(runnable);
        //			while (! completionLock.fDone) {
        //				try {
        //					completionLock.wait(500);
        //				} catch (InterruptedException x) {
        //				}
        //			}
        //		}
        //
        //		if (exception[0] != null) {
        //			throw exception[0];
        //		}
        //
        //		return result[0];
//      return null;
    }

    /**
     * Is the document currently acquired?
     *
     * @return <code>true</code> if the document is currently acquired,
     *         <code>false</code> otherwise
     * @since 3.2
     */
    protected boolean isDocumentAcquired() {
        return fAcquireCount > 0;
    }

    /**
     * Has the document been modified since it has been first acquired by the change?
     *
     * @return Returns true if the document has been modified since it got acquired by the change.
     *         <code>false</code> is returned if the document has not been acquired yet, or has been released
     *         already.
     * @since 3.3
     */
    protected boolean isDocumentModified() {
        //TODO
        //		if (fAcquireCount > 0) {
        //			ContentStamp currentStamp= ContentStamps.get(fFile, fBuffer.getDocument());
        //			return !currentStamp.equals(fContentStamp);
        //		}
        return false;
    }

    /**
     * Does the text file change need saving?
     * <p>
     * The implementation of this method returns <code>true</code> if the
     * <code>FORCE_SAVE</code> flag is enabled, or the underlying file is not
     * dirty and <code>KEEP_SAVE_STATE</code> is enabled.
     * </p>
     *
     * @return <code>true</code> if it needs saving according to its dirty
     *         state and the save mode flags, <code>false</code> otherwise
     * @since 3.3
     */
    protected boolean needsSaving() {
        //TODO
        //		if ((fSaveMode & FORCE_SAVE) != 0) {
        //			return true;
        //		}
        //		if ((fSaveMode & KEEP_SAVE_STATE) != 0) {
        //			return fValidationState == null || !fValidationState.wasDirty();
        //		}
        return false;
    }

    /** @see com.codenvy.ide.ext.java.jdt.ltk.refactoring.Change#getModifiedElement() */
    @Override
    public Object getModifiedElement() {
        // TODO Auto-generated method stub
        return null;
    }
}
