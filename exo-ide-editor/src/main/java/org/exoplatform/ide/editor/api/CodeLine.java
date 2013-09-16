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
package org.exoplatform.ide.editor.api;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class CodeLine {
    CodeType type;

    String lineContent;

    int lineNumber;

    public CodeLine(CodeType type, String lineContent, int lineNumber) {
        this.type = type;
        this.lineContent = lineContent;
        this.lineNumber = lineNumber;
    }

    public CodeType getType() {
        return type;
    }

    public String getLineContent() {
        return lineContent;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public enum CodeType {
        TYPE_ERROR, IMPORT_STATEMENT;
    }

}
