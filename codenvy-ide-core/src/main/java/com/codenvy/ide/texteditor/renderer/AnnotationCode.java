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
