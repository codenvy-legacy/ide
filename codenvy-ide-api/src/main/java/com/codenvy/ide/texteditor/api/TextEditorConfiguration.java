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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.outline.OutlineModel;
import com.codenvy.ide.texteditor.api.parser.CmParser;
import com.codenvy.ide.texteditor.api.parser.Parser;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistProcessor;
import com.codenvy.ide.texteditor.api.reconciler.Reconciler;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

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
    public int getTabWidth(@NotNull TextEditorPartView view) {
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
    public UndoManager getUndoManager(@NotNull TextEditorPartView view) {
        return new UndoManager(25);
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
    public ContentFormatter getContentFormatter(@NotNull TextEditorPartView view) {
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
    public StringMap<CodeAssistProcessor> getContentAssistantProcessors(@NotNull TextEditorPartView view) {
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
    public QuickAssistProcessor getQuickAssistAssistant(@NotNull TextEditorPartView view) {
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
    public AutoEditStrategy[] getAutoEditStrategies(@NotNull TextEditorPartView view, @NotNull String contentType) {
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
    @NotNull
    public String[] getConfiguredContentTypes(@NotNull TextEditorPartView view) {
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
    @NotNull
    public String getConfiguredDocumentPartitioning(@NotNull TextEditorPartView view) {
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
    public Parser getParser(@NotNull TextEditorPartView view) {
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
    public Reconciler getReconciler(@NotNull TextEditorPartView view) {
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
    public OutlineModel getOutline(@NotNull TextEditorPartView view) {
        return null;
    }
}
