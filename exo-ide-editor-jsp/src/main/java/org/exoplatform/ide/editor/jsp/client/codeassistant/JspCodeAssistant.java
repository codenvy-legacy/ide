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
package org.exoplatform.ide.editor.jsp.client.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.util.JSONTokenParser;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.java.client.codeassistant.services.CodeAssistantService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JSPCodeAssistant Apr 15, 2011 12:34:45 PM evgen $
 *
 */
public class JspCodeAssistant extends JavaCodeAssistant
{

   public interface JspBundle extends ClientBundle
   {
      @Source("org/exoplatform/ide/editor/jsp/client/tokens/jsp_tokens.js")
      ExternalTextResource jspImplicitObjects();
   }

   private static Map<String, Token> implicitObjects;

   private JspHtmlCodeAssistant jspHtmlCodeAssistant;

   /**
    * @param service
    * @param factory
    * @param errorHandler
    */
   public JspCodeAssistant(CodeAssistantService service, TokenWidgetFactory factory,
      JavaCodeAssistantErrorHandler errorHandler)
   {
      super(service, factory, errorHandler);
      jspHtmlCodeAssistant = new JspHtmlCodeAssistant();

      if (implicitObjects == null)
      {

         JspBundle bundle = GWT.create(JspBundle.class);
         try
         {
            bundle.jspImplicitObjects().getText(new ResourceCallback<TextResource>()
            {

               @Override
               public void onSuccess(TextResource resource)
               {
                  JSONTokenParser parser = new JSONTokenParser();
                  List<Token> objects = parser.getTokens(new JSONArray(parseJson(resource.getText())));
                  implicitObjects = new HashMap<String, Token>();
                  for (Token t : objects)
                  {
                     implicitObjects.put(t.getName(), t);
                  }
               }

               @Override
               public void onError(ResourceException e)
               {
                  e.printStackTrace();
               }
            });
         }
         catch (ResourceException e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, int cursorOffsetX, int cursorOffsetY, List<Token> tokenList,
      String lineMimeType, Token currentToken)
   {
      if (MimeType.APPLICATION_JAVA.equals(lineMimeType))
      {
         super.autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList, lineMimeType, currentToken);
      }
      else
      {
         jspHtmlCodeAssistant.autocompleteCalled(editor, cursorOffsetX, cursorOffsetY, tokenList, lineMimeType,
            currentToken);
      }
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant#callOpenForm(java.util.List)
    */
   @Override
   protected void callOpenForm(List<Token> tokens)
   {
      if (action == Action.CLASS_NAME_AND_LOCAL_VAR || action == Action.LOCAL_VAR)
         tokens.addAll(implicitObjects.values());
      super.callOpenForm(tokens);
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant#showMethods(org.exoplatform.ide.editor.api.codeassitant.Token, java.lang.String, java.lang.String)
    */
   @Override
   protected void showMethods(Token currentToken, String varToken)
   {
      if (implicitObjects.containsKey(varToken))
      {
         action = Action.PUBLIC;
         curentFqn =
            implicitObjects.get(varToken).getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue();
         getClassDescription();
      }
      else
      {
         super.showMethods(currentToken, varToken);
      }
   }
}
