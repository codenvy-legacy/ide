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
package com.codenvy.ide.texteditor.api.quickassist;

import com.codenvy.ide.texteditor.api.TextEditorPartView;

/**
 * An <code>QuickAssistAssistant</code> provides support for quick fixes and quick
 * assists.
 * The quick assist assistant is a {@link TextEditorPartView} add-on. Its
 * purpose is to propose, display, and insert quick assists and quick fixes
 * available at the current source viewer's quick assist invocation context.
 * <p>
 * The quick assist assistant can be configured with a {@link QuickAssistProcessor}
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
