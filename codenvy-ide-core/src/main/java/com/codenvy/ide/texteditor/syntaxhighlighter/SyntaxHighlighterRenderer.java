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
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.TextEditorViewImpl.Css;
import com.codenvy.ide.texteditor.api.parser.Token;
import com.codenvy.ide.texteditor.renderer.LineRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;


/** A {@link LineRenderer} to render the syntax highlighting. */
public class SyntaxHighlighterRenderer implements LineRenderer {

    /** ClientBundle for the syntax highlighter renderer. */
    public interface Resources extends ClientBundle {
        @Source("SyntaxHighlighterRenderer.css")
        CssResource syntaxHighlighterRendererCss();
    }

    static {
        //TODO remove this;
        Resources res = GWT.create(Resources.class);
        res.syntaxHighlighterRendererCss().ensureInjected();
    }

    private final com.codenvy.ide.texteditor.api.SelectionModel selection;

    private final SyntaxHighlighter syntaxHighlighter;

    private Array<Token> tokens;

    private int tokenPos;

    private final Css editorCss;

    SyntaxHighlighterRenderer(SyntaxHighlighter syntaxHighlighter, com.codenvy.ide.texteditor.api.SelectionModel selection,
                              TextEditorViewImpl.Css editorCss) {
        this.syntaxHighlighter = syntaxHighlighter;
        this.selection = selection;
        this.editorCss = editorCss;
    }

    @Override
    public void renderNextChunk(Target target) {
        Token token = tokens.get(tokenPos++);
        Assert.isNotNull(token, "Token was null");

        String tokenValue = token.getValue();

        String style = "";
        switch (token.getType()) {
            case NEWLINE:
                // we special case the NEWLINE token and do not append the default style.
                style = null;
                break;

            case ERROR:
                style = editorCss.lineRendererError() + " ";
                // Fall through to add the external stable class name too (unofficial color API)

            default:
                style += token.getStyle();
        }

        target.render(tokenValue.length(), style);
    }

    @Override
    public boolean resetToBeginningOfLine(Line line, int lineNumber) {

        tokens = syntaxHighlighter.getTokens(line);
        tokenPos = 0;

        // If we failed to get any tokens, don't try to render this line
        return tokens != null;
    }

    @Override
    public boolean shouldLastChunkFillToRight() {
        return false;
    }
}
