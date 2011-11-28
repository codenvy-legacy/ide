/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.editor.api.codeassitant;

import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.ui.AssistImportDeclarationForm;
import org.exoplatform.ide.editor.api.codeassitant.ui.AssistImportDeclarationHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.AutocompletionForm;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Callback abstract calss for codeaasistant feature.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeAssistant Feb 22, 2011 12:43:13 PM evgen $
 *
 */
public abstract class CodeAssistant implements TokenSelectedHandler, AssistImportDeclarationHandler
{

   protected String beforeToken;

   protected String tokenToComplete;

   protected String afterToken;

   protected Editor editor;

   protected int posX;

   protected int posY;

   /**
    * @param eventBys
    */
   public CodeAssistant()
   {
      super();
   }

   /**
    * If editor support code validation, it's method called when user click on error mark 
    * @param editor instance of current editor 
    * @param codeErrorList list with errors
    * @param markOffsetX coordinate X
    * @param markOffsetY coordinate X
    * @param fileMimeType mime type of current edited file
    */
   public abstract void errorMarkClicked(Editor editor, List<CodeLine> codeErrorList, int markOffsetX, int markOffsetY,
      String fileMimeType);

   /**
    * If editor support autocompletion, he calls this method.  
    * @param editor instance of editor
    * @param cursorOffsetX offset x position of cursor
    * @param cursorOffsetY offset y position of cursor
    * @param tokenList List of parsed tokens
    * @param lineMimeType line mime type
    * @param currentToken if cursor pleased before dot(i.e. autocompletion called for method or property), 
    * contains token with information of type of variable
    */
   public abstract void autocompleteCalled(Editor editor, int cursorOffsetX, int cursorOffsetY,
      List<Token> tokenList, String lineMimeType, Token currentToken);

   /**
    * Create and show {@link AutocompletionForm}
    * @param tokens list of tokens
    * @param factory instance of {@link TokenWidgetFactory}
    * @param handler callback  for autocompletion form
    */
   protected void openForm(List<Token> tokens, TokenWidgetFactory factory, TokenSelectedHandler handler)
   {
      posX = posX - tokenToComplete.length() * 8 + 8;
      posY = posY + 4;
      new AutocompletionForm(posX, posY, tokenToComplete, tokens, factory, handler);
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler#onStringSelected(java.lang.String)
    */
   @Override
   public void onStringSelected(String value)
   {
      editor.replaceTextAtCurrentLine(beforeToken + value + afterToken, beforeToken.length() + value.length());
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler#onTokenSelected(org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget)
    */
   @Override
   public void onTokenSelected(TokenWidget value)
   {
      String tokenValue = value.getTokenValue();
      String tokenToPaste = "";
      int newCursorPos = 1;
      switch (value.getToken().getType())
      {
         case ATTRIBUTE :
            if (!beforeToken.endsWith(" "))
               beforeToken += " ";
            tokenToPaste = beforeToken + tokenValue + afterToken;
            newCursorPos = (beforeToken + tokenValue).lastIndexOf("\"");
            break;

         case TAG :
            if (beforeToken.endsWith("<") || beforeToken.endsWith(" "))
               beforeToken = beforeToken.substring(0, beforeToken.length() - 1);
            tokenToPaste = beforeToken + tokenValue + afterToken;
            if (tokenValue.contains("/"))
               newCursorPos = (beforeToken + tokenValue).indexOf("/", beforeToken.length()) - 1;
            else
               newCursorPos = (beforeToken + tokenValue).length() + 1;
            break;

         case FUNCTION :
         case METHOD :
            newCursorPos = (beforeToken + tokenValue).length() + 1;
            if (tokenValue.contains("("))
            {
               newCursorPos = (beforeToken + tokenValue).lastIndexOf('(') + 1;
            }
            tokenToPaste = beforeToken + tokenValue + afterToken;
            break;

         default :
            tokenToPaste = beforeToken + tokenValue + afterToken;
            newCursorPos = beforeToken.length() + tokenValue.length();
            break;
      }
      editor.replaceTextAtCurrentLine(tokenToPaste, newCursorPos);
      if (value.getToken().hasProperty(TokenProperties.FQN))
      {
         editor.insertImportStatement(value.getToken().getProperty(TokenProperties.FQN).isStringProperty()
            .stringValue());
      }
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler#onCancelAutoComplete()
    */
   @Override
   public void onCancelAutoComplete()
   {
      editor.setFocus();
   }

   /**
    * Create and show {@link AssistImportDeclarationForm}
    * @param left position
    * @param top position
    * @param tokens list of tokens
    * @param factory instance of {@link TokenWidgetFactory}
    * @param handler callback for {@link AssistImportDeclarationForm}
    */
   protected void openImportForm(int left, int top, List<Token> tokens, TokenWidgetFactory factory,
      AssistImportDeclarationHandler handler)
   {
      new AssistImportDeclarationForm(left, top, tokens, factory, handler);
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.AssistImportDeclarationHandler#onImportTokenSelected(org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void onImportTokenSelected(Token token)
   {
      editor.insertImportStatement(token.getProperty(TokenProperties.FQN).isStringProperty().stringValue());
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.AssistImportDeclarationHandler#onImportCancel()
    */
   @Override
   public void onImportCancel()
   {
      editor.setFocus();
   }

   /**
    * Takes in a trusted JSON String and evals it.
    * @param JSON String that you trust
    * @return JavaScriptObject that you can cast to an Overlay Type
    */
   protected native JavaScriptObject parseJson(String json) /*-{
		return eval('(' + json + ')');
   }-*/;

}
