/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.jseditor.client.quickfix;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.che.ide.jseditor.client.texteditor.TextEditor;

/**
 * Context information for quick fix and quick assist processors.
 * <p>
 * This interface can be implemented by clients.</p>
 */
public final class QuickAssistInvocationContext {

    private final int line;
    private final TextEditor textEditor;

    public QuickAssistInvocationContext(@Nullable final Integer line,
                                        @Nonnull final TextEditor textEditor) {
        if (textEditor == null) {
            throw new IllegalArgumentException("editor handle cannot be null");
        }
        this.line = line;
        this.textEditor = textEditor;
    }

    /**
     * Returns the line where quick assist was invoked.
     *
     * @return the invocation line or <code>-1</code> if unknown
     */
    @Nullable
    public Integer getLine() {
        return this.line;
    }

    /**
     * Returns the editor handle for this context.
     *
     * @return the editor handle
     */
    @Nonnull
    public TextEditor getTextEditor() {
        return this.textEditor;
    }
}
