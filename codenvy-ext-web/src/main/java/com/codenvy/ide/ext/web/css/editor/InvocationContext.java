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
package com.codenvy.ide.ext.web.css.editor;

import com.codenvy.ide.texteditor.api.TextEditorPartView;

/**
 * Invocation context for Css code assistant, it's hold prefix, offset and resources of currant code assistant session.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class InvocationContext {
    private final String prefix;

    private final int offset;

    private final CssResources resources;

    private final TextEditorPartView editor;

    /**
     * @param prefix
     * @param offset
     */
    public InvocationContext(String prefix, int offset, CssResources resources, TextEditorPartView editor) {
        super();
        this.prefix = prefix;
        this.offset = offset;
        this.resources = resources;
        this.editor = editor;
    }

    /** @return the prefix */
    public String getPrefix() {
        return prefix;
    }

    /** @return the offset */
    public int getOffset() {
        return offset;
    }

    /** @return the resourcess */
    public CssResources getResources() {
        return resources;
    }

    /** @return the editor */
    public TextEditorPartView getEditor() {
        return editor;
    }
}
