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
class AnnotationCode {

    private DocumentPosition start;

    private DocumentPosition end;

    private String decoration;

    /**
     * @param start
     * @param end
     * @param decoration
     */
    public AnnotationCode(DocumentPosition start, DocumentPosition end, String decoration) {
        super();
        this.start = start;
        this.end = end;
        this.decoration = decoration;
    }

    /** @return file position where this Annotation starts */
    public DocumentPosition getStart() {
        return start;
    }

    /** @return file position where this Annotation ends (inclusive) */
    public DocumentPosition getEnd() {
        return end;
    }

    /** @return the decoration */
    public String getDecoration() {
        return decoration;
    }
}
