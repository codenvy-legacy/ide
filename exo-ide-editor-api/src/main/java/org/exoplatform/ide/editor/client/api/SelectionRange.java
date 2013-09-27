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
package org.exoplatform.ide.editor.client.api;

import org.exoplatform.ide.editor.shared.text.IRegion;

/**
 * Range of the selection in editor.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 6, 2012 3:30:42 PM anya $
 * @deprecated Use {@link IRegion} instead
 */
public class SelectionRange {
    /** Selection's start line. */
    private int startLine;

    /** Selection's start symbol. */
    private int startSymbol;

    /** Selection's end line. */
    private int endLine;

    /** Selection's end symbol. */
    private int endSymbol;

    /**
     * @param startLine
     * @param startSymbol
     * @param endLine
     * @param endSymbol
     */
    public SelectionRange(int startLine, int startSymbol, int endLine, int endSymbol) {
        this.startLine = startLine;
        this.startSymbol = startSymbol;
        this.endLine = endLine;
        this.endSymbol = endSymbol;
    }

    /** @return the startLine */
    public int getStartLine() {
        return startLine;
    }

    /** @return the startSymbol */
    public int getStartSymbol() {
        return startSymbol;
    }

    /** @return the endLine */
    public int getEndLine() {
        return endLine;
    }

    /** @return the endSymbol */
    public int getEndSymbol() {
        return endSymbol;
    }

}
