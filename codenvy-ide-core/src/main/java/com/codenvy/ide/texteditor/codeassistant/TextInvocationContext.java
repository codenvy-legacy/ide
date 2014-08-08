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
package com.codenvy.ide.texteditor.codeassistant;

import com.codenvy.ide.api.texteditor.TextEditorPartView;
import com.codenvy.ide.api.texteditor.quickassist.QuickAssistInvocationContext;

/**
 * Text quick assist invocation context.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class TextInvocationContext implements QuickAssistInvocationContext {

    private TextEditorPartView view;

    private int offset;

    private int length;

    /**
     * @param view
     * @param offset
     * @param length
     */
    public TextInvocationContext(TextEditorPartView view, int offset, int length) {
        super();
        this.view = view;
        this.offset = offset;
        this.length = length;
    }

    /** {@inheritDoc} */
    @Override
    public int getOffset() {
        return offset;
    }

    /** {@inheritDoc} */
    @Override
    public int getLength() {
        return length;
    }

    /** {@inheritDoc} */
    @Override
    public TextEditorPartView getTextEditor() {
        return view;
    }

}
