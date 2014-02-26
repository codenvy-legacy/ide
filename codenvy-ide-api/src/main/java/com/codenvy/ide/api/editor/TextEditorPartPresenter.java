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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.outline.OutlinePresenter;


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
public interface TextEditorPartPresenter extends EditorPartPresenter {
    /**
     * Returns this text editor's document provider.
     *
     * @return the document provider or <code>null</code> if none, e.g. after closing the editor
     */
    DocumentProvider getDocumentProvider();

    /**
     * Return this text editor document.
     * Note than method return null until <code>PROP_INPUT</code>  property changed.
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

    /**
     * Return Outline presenter.
     * If editor not support Outline return <code>null</code>
     *
     * @return the outline presenter.
     */
    OutlinePresenter getOutline();
}
