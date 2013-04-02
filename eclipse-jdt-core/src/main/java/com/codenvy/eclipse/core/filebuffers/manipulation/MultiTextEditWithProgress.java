/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.core.filebuffers.manipulation;

import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.OperationCanceledException;

import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.shared.text.edits.MultiTextEdit;
import org.exoplatform.ide.editor.shared.text.edits.TextEdit;
import org.exoplatform.ide.editor.shared.text.edits.UndoEdit;

/**
 * Multi-text edit with progress reporting.
 *
 * @since 3.1
 */
public class MultiTextEditWithProgress extends MultiTextEdit {

    IProgressMonitor fProgressMonitor;

    private final String fTaskName;

    public MultiTextEditWithProgress(String taskName) {
        fTaskName = taskName;
    }

    /*
     * @see TextEdit#apply(IDocument)
     */
    public final UndoEdit apply(IDocument document,
                                IProgressMonitor progressMonitor) throws MalformedTreeException, BadLocationException {
        return apply(document, CREATE_UNDO | UPDATE_REGIONS, progressMonitor);
    }

    /*
     * @see TextEdit#apply(IDocument, int)
     */
    public final UndoEdit apply(IDocument document, int style,
                                IProgressMonitor progressMonitor) throws MalformedTreeException, BadLocationException {
        fProgressMonitor = progressMonitor;
        try {

            int count = getChildrenSize();
            if ((style & TextEdit.UPDATE_REGIONS) != 0) {
                count = 2 * count;
            }

            fProgressMonitor.beginTask(fTaskName, count);
            try {
                return super.apply(document, style);
            } finally {
                fProgressMonitor.done();
            }

        } finally {
            fProgressMonitor = null;
        }
    }

    /*
     * @see org.eclipse.text.edits.TextEdit#childDocumentUpdated()
     */
    protected void childDocumentUpdated() {
        if (fProgressMonitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        fProgressMonitor.worked(1);
    }

    /*
     * @see org.eclipse.text.edits.TextEdit#childRegionUpdated()
     */
    protected void childRegionUpdated() {
        if (fProgressMonitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        fProgressMonitor.worked(1);
    }
}
