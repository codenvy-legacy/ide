/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.text.edits;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates the reverse changes of an executed text edit tree. To apply an undo memento to a document use method
 * <code>apply(IDocument)</code>.
 * <p/>
 * Clients can't add additional children to an undo edit nor can they add an undo edit as a child to another edit. Doing so
 * results in both cases in a <code>MalformedTreeException<code>.
 *
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class UndoEdit extends TextEdit {

    UndoEdit() {
        super(0, Integer.MAX_VALUE);
    }

    private UndoEdit(UndoEdit other) {
        super(other);
    }

    /*
     * @see org.eclipse.text.edits.TextEdit#internalAdd(org.eclipse.text.edits.TextEdit )
     */
    void internalAdd(TextEdit child) throws MalformedTreeException {
        throw new MalformedTreeException(null, this, "Cannot add children to an undo edit"); //$NON-NLS-1$
    }

    /*
     * @see org.eclipse.text.edits.MultiTextEdit#aboutToBeAdded(org.eclipse.text.edits .TextEdit)
     */
    void aboutToBeAdded(TextEdit parent) {
        throw new MalformedTreeException(parent, this, "Cannot add an undo edit to another edit"); //$NON-NLS-1$
    }

    UndoEdit dispatchPerformEdits(TextEditProcessor processor) throws BadLocationException {
        return processor.executeUndo();
    }

    void dispatchCheckIntegrity(TextEditProcessor processor) throws MalformedTreeException {
        processor.checkIntegrityUndo();
    }

    /* @see org.eclipse.text.edits.TextEdit#doCopy() */
    protected TextEdit doCopy() {
        return new UndoEdit(this);
    }

    /* @see TextEdit#accept0 */
    protected void accept0(TextEditVisitor visitor) {
        boolean visitChildren = visitor.visit(this);
        if (visitChildren) {
            acceptChildren(visitor);
        }
    }

    /* @see TextEdit#performDocumentUpdating */
    int performDocumentUpdating(Document document) throws BadLocationException {
        fDelta = 0;
        return fDelta;
    }

    void add(ReplaceEdit edit) {
        List children = internalGetChildren();
        if (children == null) {
            children = new ArrayList(2);
            internalSetChildren(children);
        }
        children.add(edit);
    }

    void defineRegion(int offset, int length) {
        internalSetOffset(offset);
        internalSetLength(length);
    }

    boolean deleteChildren() {
        return false;
    }
}
