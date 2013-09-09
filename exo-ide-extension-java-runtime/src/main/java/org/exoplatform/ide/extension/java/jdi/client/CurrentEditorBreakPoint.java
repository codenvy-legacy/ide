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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.ide.editor.java.Breakpoint;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:39:59 AM Mar 28, 2012 evgen $
 */
public class CurrentEditorBreakPoint extends Breakpoint {

    private String filePath;


    /**
     * @param lineNumber
     * @param message
     */
    public CurrentEditorBreakPoint(int line, String message, String filePath) {
        super(Type.CURRENT, line, message);
        this.filePath = filePath;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CurrentEditorBreakPoint)
            return getLineNumber() == ((CurrentEditorBreakPoint)obj).getLineNumber();
        return false;
    }


    /** @param lineNumber */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

}
