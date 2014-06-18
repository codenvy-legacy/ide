/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
