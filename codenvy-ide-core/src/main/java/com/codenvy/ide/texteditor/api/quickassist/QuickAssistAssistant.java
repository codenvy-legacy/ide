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
package com.codenvy.ide.texteditor.api.quickassist;

import com.codenvy.ide.api.texteditor.TextEditorPartView;
import com.codenvy.ide.api.texteditor.quickassist.QuickAssistProcessor;

/**
 * An <code>QuickAssistAssistant</code> provides support for quick fixes and quick
 * assists.
 * The quick assist assistant is a {@link TextEditorPartView} add-on. Its
 * purpose is to propose, display, and insert quick assists and quick fixes
 * available at the current source viewer's quick assist invocation context.
 * <p>
 * The quick assist assistant can be configured with a {@link com.codenvy.ide.api.texteditor.quickassist.QuickAssistProcessor}
 * which provides the possible quick assist and quick fix completions.
 * </p>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface QuickAssistAssistant {
    /**
     * Installs quick assist support on the given source viewer.
     *
     * @param sourceViewer
     *         the source viewer on which quick assist will work
     */
    void install(TextEditorPartView textEditor);

    /**
     * Uninstalls quick assist support from the source viewer it has
     * previously be installed on.
     */
    void uninstall();

    /**
     * Shows all possible quick fixes and quick assists at the viewer's cursor position.
     *
     * @return an optional error message if no proposals can be computed
     */
    String showPossibleQuickAssists();

    /**
     * Registers a given quick assist processor for a particular content type. If there is already
     * a processor registered, the new processor is registered instead of the old one.
     *
     * @param processor
     *         the quick assist processor to register, or <code>null</code> to remove
     *         an existing one
     */
    void setQuickAssistProcessor(QuickAssistProcessor processor);

    /**
     * Returns the quick assist processor to be used for the given content type.
     *
     * @return the quick assist processor or <code>null</code> if none exists
     */
    QuickAssistProcessor getQuickAssistProcessor();
}
