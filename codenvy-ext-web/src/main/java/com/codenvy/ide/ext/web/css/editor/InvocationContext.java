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
package com.codenvy.ide.ext.web.css.editor;

import com.codenvy.ide.api.texteditor.TextEditorPartView;

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
