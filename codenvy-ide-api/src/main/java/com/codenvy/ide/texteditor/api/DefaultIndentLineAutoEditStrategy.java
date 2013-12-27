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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentCommand;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.TextUtilities;

/**
 * This strategy always copies the indentation of the previous line.
 * <p>
 * This class is not intended to be subclassed.</p>
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DefaultIndentLineAutoEditStrategy implements AutoEditStrategy {

    /**
     * Creates a new indent line auto edit strategy which can be installed on
     * text viewers.
     */
    public DefaultIndentLineAutoEditStrategy() {

    }

    /**
     * Returns the first offset greater than <code>offset</code> and smaller than
     * <code>end</code> whose character is not a space or tab character. If no such
     * offset is found, <code>end</code> is returned.
     *
     * @param document
     *         the document to search in
     * @param offset
     *         the offset at which searching start
     * @param end
     *         the offset at which searching stops
     * @return the offset in the specified range whose character is not a space or tab
     * @throws BadLocationException
     *         if position is an invalid range in the given document
     */
    protected int findEndOfWhiteSpace(Document document, int offset, int end) throws BadLocationException {
        while (offset < end) {
            char c = document.getChar(offset);
            if (c != ' ' && c != '\t') {
                return offset;
            }
            offset++;
        }
        return end;
    }

    /**
     * Copies the indentation of the previous line.
     *
     * @param d
     *         the document to work on
     * @param c
     *         the command to deal with
     */
    private void autoIndentAfterNewLine(Document d, DocumentCommand c) {

        if (c.offset == -1 || d.getLength() == 0)
            return;

        try {
            // find start of line
            int p = (c.offset == d.getLength() ? c.offset - 1 : c.offset);
            Region info = d.getLineInformationOfOffset(p);
            int start = info.getOffset();

            // find white spaces
            int end = findEndOfWhiteSpace(d, start, c.offset);

            StringBuffer buf = new StringBuffer(c.text);
            if (end > start) {
                // append to input
                buf.append(d.get(start, end - start));
            }

            c.text = buf.toString();

        } catch (BadLocationException excp) {
            // stop work
        }
    }

    public void customizeDocumentCommand(Document d, DocumentCommand c) {
        if (c.length == 0 && c.text != null && TextUtilities.endsWith(d.getLegalLineDelimiters(), c.text) != -1)
            autoIndentAfterNewLine(d, c);
    }
}
