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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.util.ListenerRegistrar;
import com.google.gwt.user.client.Element;


/**
 * A text display connects a text widget with an
 * {@link Document}. The document is used as the
 * widget's text model.
 * A text viewer supports a set of configuration options and plug-ins defining
 * its behavior:
 * <ul>
 * <li>undo manager</li>
 * <li>explicit configuration</li>
 * </ul>
 * A text view provides several text editing functions, some of them are
 * configurable, through a text operation target interface.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface TextEditorPartView {

    /**
     * Returns this display element
     *
     * @return element
     */
    Element getElement();

    /**
     * Sets this viewer's undo manager.
     *
     * @param undoManager
     *         the new undo manager. <code>null</code> is a valid argument.
     */
    void setUndoManager(UndoManager undoManager);


    /**
     * Configures the source viewer using the given configuration. Prior to 3.0 this
     * method can only be called once.
     *
     * @param configuration
     *         the source viewer configuration to be used
     */
    void configure(TextEditorConfiguration configuration);


    /**
     * Sets the given document as the text display model and updates the
     * presentation accordingly.
     *
     * @param document
     */
    void setDocument(Document document);

    /**
     * Sets the given document as this display's text model and the
     * given annotation model as the model for this display's visual
     * annotations. The presentation is accordingly updated.
     *
     * @param document
     *         the display's new input document
     * @param annotationModel
     *         the model for the display's visual annotations
     */
    void setDocument(Document document, AnnotationModel annotationModel);

    /**
     * Returns the text display input document.
     *
     * @return the document
     */
    public Document getDocument();

    /**
     * Sets the editable state.
     *
     * @param isReadOnly
     *         the read only state
     */

    void setReadOnly(final boolean isReadOnly);

    /**
     * Returns whether the shown text can be manipulated.
     *
     * @return the viewer's readOnly state
     */
    boolean isReadOnly();

    /**
     * Returns whether the operation specified by the given operation code
     * can be performed.
     *
     * @param operation
     *         the operation code
     * @return <code>true</code> if the specified operation can be performed
     */
    boolean canDoOperation(int operation);

    /**
     * Performs the operation specified by the operation code on the target.
     * <code>doOperation</code> must only be called if <code>canDoOperation</code>
     * returns <code>true</code>.
     *
     * @param operation
     *         the operation code
     */
    void doOperation(int operation);

    /**
     * Adds a text input listener to this display. If the listener is already registered
     * with this display, this call has no effect.
     *
     * @param listener
     *         the listener to be added
     */
    void addTextInputListener(TextInputListener listener);

    /**
     * Removes the given listener from this display's set of text input listeners.
     * If the listener is not registered with this display, this call has
     * no effect.
     *
     * @param listener
     *         the listener to be removed
     */
    void removeTextInputListener(TextInputListener listener);

    /**
     * Get FocusManager used this text editor view.
     *
     * @return the focus manager
     */
    FocusManager getFocusManager();

    /**
     * Get ListenerRegistrar for editor key listeners.
     *
     * @return the key listener registrar.
     */
    ListenerRegistrar<KeyListener> getKeyListenerRegistrar();

    /**
     * Get selection model used this editor view.
     *
     * @return the selection model
     */
    SelectionModel getSelection();

}