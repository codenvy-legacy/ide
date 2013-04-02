/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
