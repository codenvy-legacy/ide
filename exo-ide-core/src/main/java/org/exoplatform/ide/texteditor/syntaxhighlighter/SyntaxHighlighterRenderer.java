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

package org.exoplatform.ide.texteditor.syntaxhighlighter;

import com.google.gwt.core.client.GWT;

import com.google.gwt.resources.client.CssResource;

import com.google.gwt.resources.client.ClientBundle;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.runtime.Assert;
import org.exoplatform.ide.text.store.Line;
import org.exoplatform.ide.texteditor.Editor;
import org.exoplatform.ide.texteditor.Editor.Css;
import org.exoplatform.ide.texteditor.api.parser.Token;
import org.exoplatform.ide.texteditor.renderer.LineRenderer;
import org.exoplatform.ide.texteditor.selection.SelectionModel;

/**
 * A {@link LineRenderer} to render the syntax highlighting.
 *
 */
public class SyntaxHighlighterRenderer implements LineRenderer
{

   /**
    * ClientBundle for the syntax highlighter renderer.
    */
   public interface Resources extends ClientBundle
   {
      @Source("SyntaxHighlighterRenderer.css")
      CssResource syntaxHighlighterRendererCss();
   }
   static{
      //TODO remove this;
      Resources res = GWT.create(Resources.class);
      res.syntaxHighlighterRendererCss().ensureInjected();
   }

   private final SelectionModel selection;

   private final SyntaxHighlighter syntaxHighlighter;

   private JsonArray<Token> tokens;

   private int tokenPos;

   private final Css editorCss;

   SyntaxHighlighterRenderer(SyntaxHighlighter syntaxHighlighter, SelectionModel selection, Editor.Css editorCss)
   {
      this.syntaxHighlighter = syntaxHighlighter;
      this.selection = selection;
      this.editorCss = editorCss;
   }

   @Override
   public void renderNextChunk(Target target)
   {
      Token token = tokens.get(tokenPos++);
      Assert.isNotNull(token, "Token was null");

      String tokenValue = token.getValue();

      String style = "";
      switch (token.getType())
      {
         case NEWLINE :
            // we special case the NEWLINE token and do not append the default style.
            style = null;
            break;

         case ERROR :
            style = editorCss.lineRendererError() + " ";
            // Fall through to add the external stable class name too (unofficial color API)

         default :
            style += token.getStyle();
      }

      target.render(tokenValue.length(), style);
   }

   @Override
   public boolean resetToBeginningOfLine(Line line, int lineNumber)
   {

      tokens = syntaxHighlighter.getTokens(line);
      tokenPos = 0;

      // If we failed to get any tokens, don't try to render this line
      return tokens != null;
   }

   @Override
   public boolean shouldLastChunkFillToRight()
   {
      return false;
   }
}
