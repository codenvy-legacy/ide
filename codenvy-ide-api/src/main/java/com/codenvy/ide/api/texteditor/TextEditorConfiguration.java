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
package com.codenvy.ide.api.texteditor;

import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.api.texteditor.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.api.texteditor.outline.OutlineModel;
import com.codenvy.ide.api.texteditor.parser.CmParser;
import com.codenvy.ide.api.texteditor.parser.Parser;
import com.codenvy.ide.api.texteditor.quickassist.QuickAssistProcessor;
import com.codenvy.ide.api.texteditor.reconciler.Reconciler;
import com.codenvy.ide.collections.StringMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class bundles the configuration space of a editor view. Instances of this class are passed to the <code>configure</code> method of
 * <code>TextEditorPartView</code>.
 * <p>
 * Each method in this class get as argument the source viewer for which it should provide a particular configuration setting such as a
 * presentation reconciler. Based on its specific knowledge about the returned object, the configuration might share such objects or
 * compute them according to some rules.</p>
 * <p>
 * Clients should subclass and override just those methods which must be specific to their needs.</p>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public class TextEditorConfiguration {

    public TextEditorConfiguration() {
    }

    protected static native CmParser getParserForMime(String mime) /*-{
        conf = $wnd.CodeMirror.defaults;
        return $wnd.CodeMirror.getMode(conf, mime);
    }-*/;

    /**
     * Returns the visual width of the tab character. This implementation always returns 3.
     *
     * @param view
     *         the view to be configured by this configuration
     * @return the tab width
     */
    public int getTabWidth(@Nonnull TextEditorPartView view) {
        return 3;
    }

    /**
     * Returns the undo manager for the given text view. This implementation always returns a new instance of
     * <code>DefaultUndoManager</code> whose history length is set to 25.
     *
     * @param view
     *         the text view to be configured by this configuration
     * @return an undo manager or <code>null</code> if no undo/redo should not be supported
     */
    @Nullable
    public UndoManager getUndoManager(@Nonnull TextEditorPartView view) {
        return new UndoManagerImpl(25);
    }

    /**
     * Returns the content formatter ready to be used with the given source viewer.
     * This implementation always returns <code>null</code>.
     *
     * @param view
     *         the source viewer to be configured by this configuration
     * @return a content formatter or <code>null</code> if formatting should not be supported
     */
    @Nullable
    public ContentFormatter getContentFormatter(@Nonnull TextEditorPartView view) {
        return null;
    }

    /**
     * Returns the content assistant processors ready to be used with the given source viewer.
     * This implementation always returns <code>null</code>.
     *
     * @param view
     *         the source viewer to be configured by this configuration
     * @return a content assistant or <code>null</code> if content assist should not be supported
     */
    @Nullable
    public StringMap<CodeAssistProcessor> getContentAssistantProcessors(@Nonnull TextEditorPartView view) {
        return null;
    }

    /**
     * Returns the quick assist assistant ready to be used with the given source viewer.
     * This implementation always returns <code>null</code>.
     *
     * @param view
     *         that ext view to be configured by this configuration
     * @return a quick assist assistant or <code>null</code> if quick assist should not be supported
     */
    @Nullable
    public QuickAssistProcessor getQuickAssistAssistant(@Nonnull TextEditorPartView view) {
        return null;
    }

    /**
     * Returns the auto edit strategies ready to be used with the given text view when manipulating text of the given content type.
     *
     * @param view
     *         the source viewer to be configured by this configuration
     * @param contentType
     *         the content type for which the strategies are applicable
     * @return the auto edit strategies or <code>null</code> if automatic editing is not to be enabled
     */
    @Nullable
    public AutoEditStrategy[] getAutoEditStrategies(@Nonnull TextEditorPartView view, @Nonnull String contentType) {
        return new AutoEditStrategy[]{new DefaultIndentLineAutoEditStrategy()};
    }

    /**
     * Returns all configured content types for the given text view. This list tells the caller which content types must be configured for
     * the given text view, i.e. for which content types the given view functionalities must be specified. This implementation always
     * returns <code>new String[] { Document.DEFAULT_CONTENT_TYPE }</code>.
     *
     * @param view
     *         the source viewer to be configured by this configuration
     * @return the configured content types for the given viewer
     */
    @Nonnull
    public String[] getConfiguredContentTypes(@Nonnull TextEditorPartView view) {
        return new String[]{Document.DEFAULT_CONTENT_TYPE};
    }

    /**
     * Returns the configured partitioning for the given source viewer. The partitioning is used when the querying content types from the
     * source viewer's input document.  This implementation always returns <code>IDocumentExtension3.DEFAULT_PARTITIONING</code>.
     *
     * @param view
     *         the source viewer to be configured by this configuration
     * @return the configured partitioning
     * @see #getConfiguredContentTypes(TextEditorPartView)
     */
    @Nonnull
    public String getConfiguredDocumentPartitioning(@Nonnull TextEditorPartView view) {
        return Document.DEFAULT_PARTITIONING;
    }

    /**
     * Returns parser for syntax highlight.
     * This implementation always returns <code>null</code>.
     *
     * @param view
     *         the source viewer to be configured by this configuration
     * @return the Parser
     */
    @Nullable
    public Parser getParser(@Nonnull TextEditorPartView view) {
        return null;
    }

    /**
     * Returns the reconciler ready to be used with the given source view.
     * This implementation always returns <code>null</code>.
     *
     * @param view
     *         the source view to be configured by this configuration
     * @return a reconciler or <code>null</code> if reconciling should not be supported
     */
    @Nullable
    public Reconciler getReconciler(@Nonnull TextEditorPartView view) {
        return null;
    }

    /**
     * Return the outline model.
     * This implementation always returns <code>null</code>.
     *
     * @param view
     *         the source view to be configured by this configuration.
     * @return a model that used to build outline tree.
     */
    @Nullable
    public OutlineModel getOutline(@Nonnull TextEditorPartView view) {
        return null;
    }
}
