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
package com.codenvy.ide.texteditor.renderer;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
class DocumentPosition {

    private int line;

    private int column;

    /**
     * @param line
     * @param column
     */
    public DocumentPosition(int line, int column) {
        super();
        this.line = line;
        this.column = column;
    }

    /** @return line number starting from 0 */
    public int getLineNumber() {
        return line;
    }

    /** @return column number starting from 0 */
    public int getColumn() {
        return column;
    }

}
