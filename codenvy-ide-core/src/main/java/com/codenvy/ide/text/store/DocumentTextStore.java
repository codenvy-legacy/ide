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
package com.codenvy.ide.text.store;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.LineTracker;
import com.codenvy.ide.text.TextStore;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DocumentTextStore extends DocumentModel implements TextStore {

    private LineTracker lineTracker;

    /**
     *
     */
    public DocumentTextStore(LineTracker lineTracker) {
        this.lineTracker = lineTracker;
    }

    /** @see com.codenvy.ide.text.TextStore#get(int) */
    @Override
    public char get(int offset) {
        return get(offset, 1).charAt(0);
    }

    /** @see com.codenvy.ide.text.TextStore#get(int, int) */
    @Override
    public String get(int offset, int length) {
        try {
            int lineNumber = lineTracker.getLineNumberOfOffset(offset);
            LineInfo line = getLineFinder().findLine(lineNumber);
            int lineOffset = lineTracker.getLineOffset(lineNumber);
            return getText(line.line(), offset - lineOffset, length);

        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /** @see com.codenvy.ide.text.TextStore#getLength() */
    @Override
    public int getLength() {
        int length = 0;
        for (Line line = getFirstLine(); line != null; line = line.getNextLine()) {
            length += line.getText().length();
        }
        return length;
    }

    /** @see com.codenvy.ide.text.TextStore#replace(int, int, java.lang.String) */
    @Override
    public void replace(int offset, int length, String text) {

        try {
            int lineNumber = lineTracker.getLineNumberOfOffset(offset);
            LineInfo line = getLineFinder().findLine(lineNumber);
            int lineOffset = lineTracker.getLineOffset(lineNumber);
            if (length != 0)
                deleteText(line.line(), lineNumber, offset - lineOffset, length);
            if (text == null)
                return;
            insertText(line.line(), lineNumber, offset - lineOffset, text);
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** @see com.codenvy.ide.text.TextStore#set(java.lang.String) */
    @Override
    public void set(String text) {
        deleteText(getFirstLine(), 0, getLength());
        insertText(getFirstLine(), 0, 0, text);
    }

}
