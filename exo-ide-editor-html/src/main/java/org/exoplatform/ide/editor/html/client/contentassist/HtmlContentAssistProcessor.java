/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.editor.html.client.contentassist;

import com.google.collide.client.CollabEditor;
import com.google.collide.client.code.autocomplete.AutocompleteProposal;
import com.google.collide.client.code.autocomplete.integration.TaggableLineUtil;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.client.util.collections.StringMultiset;
import com.google.collide.codemirror2.CodeMirror2;
import com.google.collide.codemirror2.Token;
import com.google.collide.codemirror2.TokenType;
import com.google.collide.codemirror2.TokenUtil;
import com.google.collide.json.shared.JsonArray;
import com.google.collide.shared.Pair;
import com.google.collide.shared.TaggableLine;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.Position;
import com.google.collide.shared.document.anchor.Anchor;
import com.google.collide.shared.document.anchor.AnchorManager;
import com.google.collide.shared.document.anchor.AnchorType;

import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.css.client.contentassist.CssContentAssistProcessor;
import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptContentAssistProcessor;

/**
 * A content assist processor proposes completions and
 * computes context information for HTML content.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlContentAssistProcessor.java Feb 7, 2013 11:46:45 AM azatsarynnyy $
 *
 */
public class HtmlContentAssistProcessor implements ContentAssistProcessor
{

   /**
    * Bean that holds {@link #findTag} results.
    */
   private static class FindTagResult
   {
      /**
       * Index of last start-of-TAG token before cursor; -1 => not in this line.
       */
      int startTagIndex = -1;

      /**
       * Index of last end-of-TAG token before cursor; -1 => not in this line.
       */
      int endTagIndex = -1;

      /**
       * Token that "covers" the cursor; left token if cursor touches 2 tokens,
       */
      Token inToken = null;

      /**
       * Number of characters between "inToken" start and the cursor position.
       */
      int cut = 0;

      /**
       * Indicates that cursor is located inside tag.
       */
      boolean inTag;
   }

   private static final HtmlTagsAndAttributes htmlAttributes = HtmlTagsAndAttributes.getInstance();

   private static char[] activationCharacters = new char[]{'<'};

   static final AnchorType MODE_ANCHOR_TYPE = AnchorType.create(HtmlAutocompleter.class, "mode");

   /**
    * Autocompleter for HTML.
    */
   private HtmlAutocompleter htmlAutocompleter;

   /**
    * A {@link ContentAssistProcessor} for CSS.
    */
   private CssContentAssistProcessor cssProcessor;

   /**
    * A {@link ContentAssistProcessor} for JavaScript.
    */
   private JavaScriptContentAssistProcessor jsProcessor;

   /**
    * Constructs new {@link HtmlContentAssistProcessor} instance.
    * 
    * @param htmlAutocompleter {@link HtmlAutocompleter}
    * @param cssProcessor {@link CssContentAssistProcessor}
    * @param jsProcessor {@link JavaScriptContentAssistProcessor}
    */
   public HtmlContentAssistProcessor(HtmlAutocompleter htmlAutocompleter, CssContentAssistProcessor cssProcessor,
      JavaScriptContentAssistProcessor jsProcessor)
   {
      this.htmlAutocompleter = htmlAutocompleter;
      this.cssProcessor = cssProcessor;
      this.jsProcessor = jsProcessor;
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeCompletionProposals(org.exoplatform.ide.editor.client.api.Editor, int)
    */
   @Override
   public CompletionProposal[] computeCompletionProposals(Editor viewer, int offset)
   {
      SelectionModel selection = ((CollabEditor)viewer).getEditor().getSelection();

      Position cursor = selection.getCursorPosition();
      final Line line = cursor.getLine();
      final int column = cursor.getColumn();

      DocumentParser parser = htmlAutocompleter.getParser();
      JsonArray<Token> tokens = parser.parseLineSync(line);
      if (tokens == null)
      {
         // This line has never been parsed yet. No variants.
         return null;
      }

      // We do not ruin parse results for "clean" lines.
      if (parser.isLineDirty(cursor.getLineNumber()))
      {
         // But "processing" of "dirty" line is harmless.
         XmlCodeAnalyzer.processLine(TaggableLineUtil.getPreviousLine(line), line, tokens);
      }
      String initialMode = parser.getInitialMode(line);
      JsonArray<Pair<Integer, String>> modes = TokenUtil.buildModes(initialMode, tokens);
      putModeAnchors(line, modes);
      String mode = TokenUtil.findModeForColumn(initialMode, modes, column);

      if (cssProcessor != null && CodeMirror2.CSS.equals(mode))
      {
         return cssProcessor.computeCompletionProposals(viewer, offset);
      }
      else if (jsProcessor != null && CodeMirror2.JAVASCRIPT.equals(mode))
      {
         return jsProcessor.computeCompletionProposals(viewer, offset);
      }

      if (selection.hasSelection())
      {
         // Do not autocomplete in HTML when something is selected.
         return null;
      }

      HtmlTagWithAttributes tag = line.getTag(XmlCodeAnalyzer.TAG_START_TAG);
      boolean inTag = tag != null;

      if (column == 0)
      {
         // On first column we either add attribute or do nothing.
         if (inTag)
         {
            JsonArray<AutocompleteProposal> proposals =
               htmlAttributes.searchAttributes(tag.getTagName(), tag.getAttributes(), "");

            CompletionProposal[] proposalArray = new CompletionProposal[proposals.size()];

            for (int i = 0; i < proposals.size(); i++)
            {
               AutocompleteProposal proposal = proposals.get(i);
               proposalArray[i] =
                  new HtmlProposal(proposal.getName(), CompletionType.ATTRIBUTE, "", offset, htmlAttributes);
            }
            return proposalArray;
         }
         return null;
      }

      FindTagResult findTagResult = findTag(tokens, inTag, column);

      if (!findTagResult.inTag || findTagResult.inToken == null)
      {
         // Ooops =(
         return null;
      }

      // If not unfinished tag at the beginning of line surrounds cursor...
      if (findTagResult.startTagIndex >= 0)
      {
         // Unfinished tag at he end of line may be used...
         if (findTagResult.endTagIndex == -1)
         {
            tag = line.getTag(XmlCodeAnalyzer.TAG_END_TAG);
            if (tag == null)
            {
               // Ooops =(
               return null;
            }
         }
         else
         {
            // Or new (temporary) object constructed.
            tag = buildTag(findTagResult, tokens);
         }
      }

      TokenType type = findTagResult.inToken.getType();
      String value = findTagResult.inToken.getValue();
      value = value.substring(0, value.length() - findTagResult.cut);
      if (TokenType.TAG == type)
      {
         value = value.substring(1).trim();

         JsonArray<AutocompleteProposal> searchTags = htmlAttributes.searchTags(value.toLowerCase());

         CompletionProposal[] proposalArray = new CompletionProposal[searchTags.size()];

         for (int i = 0; i < searchTags.size(); i++)
         {
            AutocompleteProposal proposal = searchTags.get(i);
            proposalArray[i] =
               new HtmlProposal(proposal.getName(), CompletionType.ELEMENT, value, offset, htmlAttributes);
         }
         return proposalArray;
      }
      if (TokenType.WHITESPACE == type || TokenType.ATTRIBUTE == type)
      {
         value = (TokenType.ATTRIBUTE == type) ? value : "";
         JsonArray<AutocompleteProposal> proposals =
            htmlAttributes.searchAttributes(tag.getTagName(), tag.getAttributes(), value);
         //         dirtyScope = tag;
         //         dirtyScope.setDelegate(dirtyScopeDelegate);
         //         if (tag.isDirty())
         //         {
         //            return AutocompleteProposals.PARSING;
         //         }

         CompletionProposal[] proposalArray = new CompletionProposal[proposals.size()];

         for (int i = 0; i < proposals.size(); i++)
         {
            AutocompleteProposal proposal = proposals.get(i);
            proposalArray[i] =
               new HtmlProposal(proposal.getName(), CompletionType.ATTRIBUTE, value, offset, htmlAttributes);
         }
         return proposalArray;
      }

      return null;
   }

   void putModeAnchors(TaggableLine currentLine, JsonArray<Pair<Integer, String>> modes)
   {
      if (!(currentLine instanceof Line))
      {
         throw new IllegalStateException();
      }
      // TODO: pull AnchorManager.getAnchorsByTypeOrNull to
      // TaggableLine interface (for decoupling).
      Line line = (Line)currentLine;
      AnchorManager anchorManager = line.getDocument().getAnchorManager();
      if (anchorManager == null)
      {
         throw new NullPointerException();
      }
      JsonArray<Anchor> oldAnchors = AnchorManager.getAnchorsByTypeOrNull(line, MODE_ANCHOR_TYPE);
      if (oldAnchors != null)
      {
         for (Anchor anchor : oldAnchors.asIterable())
         {
            anchorManager.removeAnchor(anchor);
         }
      }
      for (Pair<Integer, String> pair : modes.asIterable())
      {
         Anchor anchor =
            anchorManager.createAnchor(MODE_ANCHOR_TYPE, line, AnchorManager.IGNORE_LINE_NUMBER, pair.first);
         anchor.setRemovalStrategy(Anchor.RemovalStrategy.SHIFT);
         anchor.setValue(pair.second);
      }
   }

   /**
    * Builds {@link HtmlTagWithAttributes} from {@link FindTagResult} and tokens.
    *
    * <p>Scanning is similar to scanning in {@link XmlCodeAnalyzer}.
    */
   private static HtmlTagWithAttributes buildTag(FindTagResult findTagResult, JsonArray<Token> tokens)
   {
      int index = findTagResult.startTagIndex;
      Token token = tokens.get(index);
      index++;
      String tagName = token.getValue().substring(1).trim();

      HtmlTagWithAttributes result = new HtmlTagWithAttributes(tagName);

      StringMultiset tagAttributes = result.getAttributes();
      while (index < findTagResult.endTagIndex)
      {
         token = tokens.get(index);
         index++;
         TokenType tokenType = token.getType();
         if (TokenType.ATTRIBUTE == tokenType)
         {
            tagAttributes.add(token.getValue().toLowerCase());
         }
      }

      result.setDirty(false);
      return result;
   }

   /**
    * Finds token at cursor position and computes first and last token indexes
    * of surrounding tag.
    */
   private static FindTagResult findTag(JsonArray<Token> tokens, boolean startsInTag, int column)
   {
      FindTagResult result = new FindTagResult();
      result.inTag = startsInTag;

      // Number of tokens in line.
      final int size = tokens.size();

      // Sum of lengths of processed tokens.
      int colCount = 0;

      // Index of next token.
      int index = 0;

      while (index < size)
      {
         Token token = tokens.get(index);
         colCount += token.getValue().length();
         TokenType type = token.getType();
         index++;
         if (TokenType.TAG == type)
         {
            // Toggle "inTag" flag and update tag bounds.
            if (result.inTag)
            {
               // Refer to XmlCodeAnalyzer parsing code notes.
               if (">".equals(token.getValue()) || "/>".equals(token.getValue()))
               {
                  result.endTagIndex = index - 1;
                  // Exit the loop if cursor is inside a closed tag.
                  if (result.inToken != null)
                  {
                     return result;
                  }
                  result.inTag = false;
               }
            }
            else
            {
               if (CodeMirror2.HTML.equals(token.getMode()))
               {
                  result.startTagIndex = index - 1;
                  result.endTagIndex = -1;
                  result.inTag = true;
               }
            }
         }
         // If token at cursor position is not found yet...
         if (result.inToken == null)
         {
            if (colCount >= column)
            {
               // We've found it at last!
               result.inToken = token;
               result.cut = colCount - column;
               if (!result.inTag)
               {
                  // No proposals for text content.
                  return result;
               }
            }
         }
      }

      return result;
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeContextInformation(org.exoplatform.ide.editor.client.api.Editor, int)
    */
   @Override
   public ContextInformation[] computeContextInformation(Editor viewer, int offset)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
    */
   @Override
   public char[] getCompletionProposalAutoActivationCharacters()
   {
      return activationCharacters;
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getContextInformationAutoActivationCharacters()
    */
   @Override
   public char[] getContextInformationAutoActivationCharacters()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getErrorMessage()
    */
   @Override
   public String getErrorMessage()
   {
      return null;
   }

}
