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

package com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.code.CompilationUnitChange;
import com.codenvy.ide.ext.java.jdt.internal.corext.util.Strings;
import com.codenvy.ide.ext.java.jdt.refactoring.Change;
import com.codenvy.ide.ext.java.jdt.refactoring.DocumentChange;
import com.codenvy.ide.ext.java.jdt.refactoring.TextChange;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.edits.CopyTargetEdit;
import com.codenvy.ide.text.edits.DeleteEdit;
import com.codenvy.ide.text.edits.InsertEdit;
import com.codenvy.ide.text.edits.MoveSourceEdit;
import com.codenvy.ide.text.edits.MoveTargetEdit;
import com.codenvy.ide.text.edits.MultiTextEdit;
import com.codenvy.ide.text.edits.ReplaceEdit;
import com.codenvy.ide.text.edits.TextEdit;
import com.codenvy.ide.text.edits.TextEditVisitor;
import com.google.gwt.user.client.ui.Image;


/**
 * A proposal for quick fixes and quick assist that work on a single compilation unit.
 * Either a {@link TextChange text change} is directly passed in the constructor or method
 * {@link #addEdits(IDocument, TextEdit)} is overridden to provide the text edits that are
 * applied to the document when the proposal is evaluated.
 * <p>
 * The proposal takes care of the preview of the changes as proposal information.
 * </p>
 *
 * @since 3.2
 */
public class CUCorrectionProposal extends ChangeCorrectionProposal {

    //   private ICompilationUnit fCompilationUnit;

    //   private LinkedProposalModel fLinkedProposalModel;

    //   private boolean fSwitchedEditor;

    protected final Document document;

    /**
     * Constructs a correction proposal working on a compilation unit with a given text change
     *
     * @param name
     *         the name that is displayed in the proposal selection dialog.
     * @param cu
     *         the compilation unit on that the change works.
     * @param change
     *         the change that is executed when the proposal is applied or <code>null</code>
     *         if implementors override {@link #addEdits(IDocument, TextEdit)} to provide
     *         the text edits or {@link #createTextChange()} to provide a text change.
     * @param relevance
     *         the relevance of this proposal.
     * @param image
     *         the image that is displayed for this proposal or <code>null</code> if no
     *         image is desired.
     */
    public CUCorrectionProposal(String name, TextChange change, int relevance, Document document, Images image) {
        super(name, change, relevance, image);
        //      if (cu == null)
        //      {
        //         throw new IllegalArgumentException("Compilation unit must not be null"); //$NON-NLS-1$
        //      }
        //      fCompilationUnit = cu;
        //      fLinkedProposalModel = null;
        this.document = document;
    }

    /**
     * Constructs a correction proposal working on a compilation unit.
     * <p>Users have to override {@link #addEdits(IDocument, TextEdit)} to provide
     * the text edits or {@link #createTextChange()} to provide a text change.
     * </p>
     *
     * @param name
     *         The name that is displayed in the proposal selection dialog.
     * @param cu
     *         The compilation unit on that the change works.
     * @param relevance
     *         The relevance of this proposal.
     * @param image
     *         The image that is displayed for this proposal or <code>null</code> if no
     *         image is desired.
     */
    protected CUCorrectionProposal(String name, int relevance, Document document, Images image) {
        this(name, null, relevance, document, image);
    }

    /**
     * Called when the {@link CompilationUnitChange} is initialized. Subclasses can override to
     * add text edits to the root edit of the change. Implementors must not access the proposal,
     * e.g getting the change.
     * <p>The default implementation does not add any edits</p>
     *
     * @param document
     *         content of the underlying compilation unit. To be accessed read only.
     * @param editRoot
     *         The root edit to add all edits to
     * @throws CoreException
     *         can be thrown if adding the edits is failing.
     */
    protected void addEdits(Document document, TextEdit editRoot) throws CoreException {
    }

    //   protected LinkedProposalModel getLinkedProposalModel()
    //   {
    //      if (fLinkedProposalModel == null)
    //      {
    //         fLinkedProposalModel = new LinkedProposalModel();
    //      }
    //      return fLinkedProposalModel;
    //   }
    //
    //   public void setLinkedProposalModel(LinkedProposalModel model)
    //   {
    //      fLinkedProposalModel = model;
    //   }

    @Override
    public Object getAdditionalInfo() {

        final StringBuffer buf = new StringBuffer();

        try {
            final TextChange change = getTextChange();

            change.setKeepPreviewEdits(true);
            final Document previewContent = change.getPreviewDocument();
            final TextEdit rootEdit = change.getPreviewEdit(change.getEdit());

            class EditAnnotator extends TextEditVisitor {
                private int fWrittenToPos = 0;

                public void unchangedUntil(int pos) {
                    if (pos > fWrittenToPos) {
                        appendContent(previewContent, fWrittenToPos, pos, buf, true);
                        fWrittenToPos = pos;
                    }
                }

                @Override
                public boolean visit(MoveTargetEdit edit) {
                    return true; //rangeAdded(edit);
                }

                @Override
                public boolean visit(CopyTargetEdit edit) {
                    return true; //return rangeAdded(edit);
                }

                @Override
                public boolean visit(InsertEdit edit) {
                    return rangeAdded(edit);
                }

                @Override
                public boolean visit(ReplaceEdit edit) {
                    if (edit.getLength() > 0)
                        return rangeAdded(edit);
                    return rangeRemoved(edit);
                }

                @Override
                public boolean visit(MoveSourceEdit edit) {
                    return rangeRemoved(edit);
                }

                @Override
                public boolean visit(DeleteEdit edit) {
                    return rangeRemoved(edit);
                }

                private boolean rangeRemoved(TextEdit edit) {
                    unchangedUntil(edit.getOffset());
                    return false;
                }

                private boolean rangeAdded(TextEdit edit) {
                    unchangedUntil(edit.getOffset());
                    buf.append("<b>"); //$NON-NLS-1$
                    appendContent(previewContent, edit.getOffset(), edit.getExclusiveEnd(), buf, false);
                    buf.append("</b>"); //$NON-NLS-1$
                    fWrittenToPos = edit.getExclusiveEnd();
                    return false;
                }
            }
            EditAnnotator ea = new EditAnnotator();
            rootEdit.accept(ea);

            // Final pre-existing region
            ea.unchangedUntil(previewContent.getLength());
        } catch (CoreException e) {
            //TODO log error
//            Log.error(getClass(), e);
        }
        return buf.toString();
    }

    private final int surroundLines = 1;

    private void appendContent(Document text, int startOffset, int endOffset, StringBuffer buf,
                               boolean surroundLinesOnly) {
        try {
            int startLine = text.getLineOfOffset(startOffset);
            int endLine = text.getLineOfOffset(endOffset);

            boolean dotsAdded = false;
            if (surroundLinesOnly && startOffset == 0) { // no surround lines for the top no-change range
                startLine = Math.max(endLine - surroundLines, 0);
                buf.append("...<br>"); //$NON-NLS-1$
                dotsAdded = true;
            }

            for (int i = startLine; i <= endLine; i++) {
                if (surroundLinesOnly) {
                    if ((i - startLine > surroundLines) && (endLine - i > surroundLines)) {
                        if (!dotsAdded) {
                            buf.append("...<br>"); //$NON-NLS-1$
                            dotsAdded = true;
                        } else if (endOffset == text.getLength()) {
                            return; // no surround lines for the bottom no-change range
                        }
                        continue;
                    }
                }

                Region lineInfo = text.getLineInformation(i);
                int start = lineInfo.getOffset();
                int end = start + lineInfo.getLength();

                int from = Math.max(start, startOffset);
                int to = Math.min(end, endOffset);
                String content = text.get(from, to - from);
                if (surroundLinesOnly && (from == start) && Strings.containsOnlyWhitespaces(content)) {
                    continue; // ignore empty lines except when range started in the middle of a line
                }
                for (int k = 0; k < content.length(); k++) {
                    char ch = content.charAt(k);
                    if (ch == '<') {
                        buf.append("&lt;"); //$NON-NLS-1$
                    } else if (ch == '>') {
                        buf.append("&gt;"); //$NON-NLS-1$
                    } else {
                        buf.append(ch);
                    }
                }
                if (to == end && to != endOffset) { // new line when at the end of the line, and not end of range
                    buf.append("<br>"); //$NON-NLS-1$
                }
            }
        } catch (BadLocationException e) {
            // ignore
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
     */
    @Override
    public void apply(Document document) {
        try {
            //         ICompilationUnit unit = getCompilationUnit();
            //         IEditorPart part = null;
            //         if (unit.getResource().exists())
            //         {
            //            boolean canEdit = performValidateEdit(unit);
            //            if (!canEdit)
            //            {
            //               return;
            //            }
            //            part = EditorUtility.isOpenInEditor(unit);
            //            if (part == null)
            //            {
            //               part = JavaUI.openInEditor(unit);
            //               if (part != null)
            //               {
            //                  fSwitchedEditor = true;
            //                  document = JavaUI.getDocumentProvider().getDocument(part.getEditorInput());
            //               }
            //            }
            //            IWorkbenchPage page = JavaPlugin.getActivePage();
            //            if (page != null && part != null)
            //            {
            //               page.bringToTop(part);
            //            }
            //            if (part != null)
            //            {
            //               part.setFocus();
            //            }
            //         }
            performChange(document);
        } catch (CoreException e) {
            //TODO log error
//            Log.error(getClass(), e);
        }
    }

    //   private boolean performValidateEdit(ICompilationUnit unit)
    //   {
    //      IStatus status = Resources.makeCommittable(unit.getResource(), JavaPlugin.getActiveWorkbenchShell());
    //      if (!status.isOK())
    //      {
    //         String label = CorrectionMessages.CUCorrectionProposal_error_title;
    //         String message = CorrectionMessages.CUCorrectionProposal_error_message;
    //         ErrorDialog.openError(JavaPlugin.getActiveWorkbenchShell(), label, message, status);
    //         return false;
    //      }
    //      return true;
    //   }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.ChangeCorrectionProposal#performChange(org.eclipse.jface.text.IDocument,
     * org.eclipse.ui.IEditorPart)
     */
    @Override
    protected void performChange(Document document) throws CoreException {
        //      try
        //      {
        super.performChange(document);
        //         if (part == null)
        //         {
        //            return;
        //         }
        //
        //         if (fLinkedProposalModel != null)
        //         {
        //            if (fLinkedProposalModel.hasLinkedPositions() && part instanceof JavaEditor)
        //            {
        //               // enter linked mode
        //               ITextViewer viewer = ((JavaEditor)part).getViewer();
        //               new LinkedProposalModelPresenter().enterLinkedMode(viewer, part, fSwitchedEditor, fLinkedProposalModel);
        //            }
        //            else if (part instanceof ITextEditor)
        //            {
        //               LinkedProposalPositionGroup.PositionInformation endPosition = fLinkedProposalModel.getEndPosition();
        //               if (endPosition != null)
        //               {
        //                  // select a result
        //                  int pos = endPosition.getOffset() + endPosition.getLength();
        //                  ((ITextEditor)part).selectAndReveal(pos, 0);
        //               }
        //            }
        //         }
        //      }
        //      catch (BadLocationException e)
        //      {
        //         throw new CoreException(JavaUIStatus.createError(IStatus.ERROR, e));
        //      }
    }

    /**
     * Creates the text change for this proposal.
     * This method is only called once and only when no text change has been passed in
     * {@link #CUCorrectionProposal(String, ICompilationUnit, TextChange, int, Image)}.
     *
     * @return returns the created text change.
     * @throws CoreException
     *         thrown if the creation of the text change failed.
     */
    protected TextChange createTextChange() throws CoreException {
        //      ICompilationUnit cu = getCompilationUnit();
        String name = getName();
        TextChange change;
        //      if (!cu.getResource().exists())
        //      {
        //         String source;
        //         try
        //         {
        //            source = cu.getSource();
        //         }
        //         catch (JavaModelException e)
        //         {
        //            JavaPlugin.log(e);
        //            source = new String(); // empty
        //         }
        //         Document document = new Document(source);
        //         document.setInitialLineDelimiter(StubUtility.getLineDelimiterUsed());
        change = new DocumentChange(name, document);
        //      }
        //      else
        //      {
        //         CompilationUnitChange cuChange = new CompilationUnitChange(name, cu);
        //         cuChange.setSaveMode(TextFileChange.LEAVE_DIRTY);
        //         change = cuChange;
        //      }
        TextEdit rootEdit = new MultiTextEdit();
        change.setEdit(rootEdit);

        // initialize text change
        Document document = change.getCurrentDocument();
        addEdits(document, rootEdit);
        return change;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.ChangeCorrectionProposal#createChange()
     */
    @Override
    protected final Change createChange() throws CoreException {
        return createTextChange(); // make sure that only text changes are allowed here
    }

    /**
     * Gets the text change that is invoked when the change is applied.
     *
     * @return returns the text change that is invoked when the change is applied.
     * @throws CoreException
     *         throws an exception if accessing the change failed
     */
    public final TextChange getTextChange() throws CoreException {
        return (TextChange)getChange();
    }

    //   /**
    //    * The compilation unit on that the change works.
    //    *
    //    * @return the compilation unit on that the change works.
    //    */
    //   public final ICompilationUnit getCompilationUnit()
    //   {
    //      return fCompilationUnit;
    //   }

    /**
     * Creates a preview of the content of the compilation unit after applying the change.
     *
     * @return returns the preview of the changed compilation unit.
     * @throws CoreException
     *         thrown if the creation of the change failed.
     */
    public String getPreviewContent() throws CoreException {
        return getTextChange().getPreviewContent();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        try {
            return getPreviewContent();
        } catch (CoreException e) {
        }
        return super.toString();
    }
}
