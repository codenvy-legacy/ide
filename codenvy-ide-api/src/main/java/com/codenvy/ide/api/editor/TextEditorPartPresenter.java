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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.api.texteditor.outline.HasOutline;


/**
 * Interface to a text editor. This interface defines functional extensions to
 * <code>EditorPartPresenter</code> as well as the configuration capabilities of a text editor.
 * <p>
 * Text editors are configured with an <code>DocumentProvider</code> which delivers a textual
 * presentation (<code>Document</code>) of the editor's input. The editor works on the document and
 * forwards all input element related calls, such as <code>save</code>, to the document provider.
 * The provider also delivers the input's annotation model which is used by the editor's vertical
 * ruler.
 * </p>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface TextEditorPartPresenter extends EditorPartPresenter, HasOutline {
    /**
     * Returns this text editor's document provider.
     *
     * @return the document provider or <code>null</code> if none, e.g. after closing the editor
     */
    DocumentProvider getDocumentProvider();

    /**
     * Return this text editor document.
     * Note than method return null until <code>PROP_INPUT</code>  property changed.
     *
     * @return the document of this editor or null if editor input not set.
     */
    Document getDocument();

    /**
     * Closes this text editor after optionally saving changes.
     *
     * @param save
     *         <code>true</code> if unsaved changed should be saved, and
     *         <code>false</code> if unsaved changed should be discarded
     */
    void close(boolean save);

    /**
     * Returns whether the text in this text editor can be changed by the user.
     *
     * @return <code>true</code> if it can be edited, and <code>false</code> if it is read-only
     */
    boolean isEditable();

    /**
     * Abandons all modifications applied to this text editor's input element's
     * textual presentation since the last save operation.
     */
    void doRevertToSaved();

    /**
     * Returns this text editor's selection provider. Repeated calls to this
     * method return the same selection provider.
     *
     * @return the selection provider
     */
    SelectionProvider getSelectionProvider();
}
