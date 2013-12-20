// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.texteditor.syntaxhighlighter;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.text.store.DocumentModel;
import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.ViewportModel;
import com.codenvy.ide.texteditor.api.parser.Token;
import com.codenvy.ide.texteditor.documentparser.DocumentParser;
import com.codenvy.ide.texteditor.renderer.LineRenderer;
import com.codenvy.ide.texteditor.renderer.Renderer;
import com.codenvy.ide.util.ListenerRegistrar;


/** Syntax highlighter for the Collide editor. */
public class SyntaxHighlighter implements DocumentParser.Listener, Renderer.CompletionListener {

    /**
     * Key for {@link Line#getTag} that stores the parsed tokens for that line. We
     * must cache these because of the asynchronous nature of rendering. Once the
     * rendering pass is complete, we clear this cache. So, this cache gets cleared
     * before the browser event loop is run.
     */
    private static final String LINE_TAG_CACHED_TOKENS = "SyntaxHighlighter.cachedTokens";

    public static SyntaxHighlighter create(DocumentModel document, Renderer renderer, ViewportModel viewport,
                                           com.codenvy.ide.texteditor.api.SelectionModel selection, DocumentParser documentParser,
                                           TextEditorViewImpl.Css editorCss) {
        ListenerRegistrar.RemoverManager removerManager = new ListenerRegistrar.RemoverManager();
        SyntaxHighlighter syntaxHighlighter =
                new SyntaxHighlighter(document, renderer, viewport, selection, documentParser, removerManager, editorCss);
        removerManager.track(documentParser.getListenerRegistrar().add(syntaxHighlighter));
        removerManager.track(renderer.getCompletionListenerRegistrar().add(syntaxHighlighter));

        return syntaxHighlighter;
    }

    private final Renderer editorRenderer;

    private final SyntaxHighlighterRenderer lineRenderer;

    private final ViewportModel viewport;

    private final Array<Line> linesWithCachedTokens;

    private final DocumentParser documentParser;

    private final ListenerRegistrar.RemoverManager removerManager;

    private SyntaxHighlighter(DocumentModel document, Renderer editorRenderer, ViewportModel viewport,
                              com.codenvy.ide.texteditor.api.SelectionModel selection, DocumentParser documentParser,
                              ListenerRegistrar.RemoverManager removerManager,
                              TextEditorViewImpl.Css editorCss) {
        this.editorRenderer = editorRenderer;
        this.viewport = viewport;
        this.documentParser = documentParser;
        this.removerManager = removerManager;
        this.linesWithCachedTokens = Collections.createArray();
        this.lineRenderer = new SyntaxHighlighterRenderer(this, selection, editorCss);
    }

    public LineRenderer getRenderer() {
        return lineRenderer;
    }

    @Override
    public void onIterationStart(int lineNumber) {
        // do nothing
    }

    @Override
    public void onIterationFinish() {
        // do nothing
    }

    @Override
    public void onDocumentLineParsed(Line line, int lineNumber, Array<Token> tokens) {
        if (!viewport.isLineInViewport(line)) {
            return;
        }

        // Save the cached tokens so the async render will have them accessible
        line.putTag(LINE_TAG_CACHED_TOKENS, tokens);
        linesWithCachedTokens.add(line);

        editorRenderer.requestRenderLine(line);
    }

    @Override
    public void onRenderCompleted() {
        // Wipe the cached tokens
        for (int i = 0, n = linesWithCachedTokens.size(); i < n; i++) {
            linesWithCachedTokens.get(i).putTag(LINE_TAG_CACHED_TOKENS, null);
        }

        linesWithCachedTokens.clear();
    }

    public void teardown() {
        removerManager.remove();
    }

    /**
     * Returns the tokens for the given line, or null if the tokens could not be
     * retrieved synchronously
     */
    Array<Token> getTokens(Line line) {
        Array<Token> tokens = line.getTag(LINE_TAG_CACHED_TOKENS);
      /*
       * If we haven't gotten a callback from the parser (hence no cached tokens),
       * try to synchronously parse the line
       */
        return tokens != null ? tokens : documentParser.parseLineSync(line);
    }
}
