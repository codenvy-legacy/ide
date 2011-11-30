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
package org.exoplatform.ide.editor.java.client.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.util.JSONTokenParser;
import org.exoplatform.ide.editor.codeassistant.util.ModifierHelper;
import org.exoplatform.ide.editor.java.client.codeassistant.services.CodeAssistantService;
import org.exoplatform.ide.editor.java.client.codeassistant.services.Types;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.JavaClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaCodeAssistant Mar 2, 2011 4:41:28 PM evgen $
 *
 */
public class JavaCodeAssistant extends CodeAssistant implements Comparator<Token>
{
   public interface JavaBundle extends ClientBundle
   {
      @Source("org/exoplatform/ide/editor/java/client/tokens/java_keywords.js")
      ExternalTextResource javakeyWords();
   }

   protected enum Action {

      /**
       * Get all <b>public</b> methods and fields;
       */
      PUBLIC,

      /**
       * Get all <b>public static</b> methods and fields
       */
      PUBLIC_STATIC,

      /**
       * Get all <b>public</b> constructors 
       */
      PUBLIC_CONSTRUCTORS,

      /**
       * Get class names
       */
      CLASS_NAME,

      /**
       * Get Classes names and local variables
       */
      CLASS_NAME_AND_LOCAL_VAR,

      /**
       * Get local var and parameters
       */
      LOCAL_VAR,

      /**
       *  Get annotations
       */
      ANNOTATION;

   }

   protected List<Token> keywords;

   private String projectId;

   private CodeAssistantService service;

   private TokenWidgetFactory factory;

   private JavaCodeAssistantErrorHandler errorHandler;

   protected String curentFqn;

   protected Action action;

   private List<Token> tokenFromParser;

   private int currentLineNumber;

   /**
    * @param factory
    */
   public JavaCodeAssistant(CodeAssistantService service, TokenWidgetFactory factory,
      JavaCodeAssistantErrorHandler errorHandler)
   {
      super();
      this.factory = factory;
      this.errorHandler = errorHandler;
      this.service = service;
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarkClicked(org.exoplatform.ide.editor.api.Editor, java.util.List, int, int, java.lang.String)
    */
   @Override
   public void errorMarkClicked(Editor editor, List<CodeLine> codeErrorList, final int markOffsetX,
      final int markOffsetY, String fileMimeType)
   {
      this.editor = editor;
      try
      {
         service.findClassesByPrefix(codeErrorList.get(0).getLineContent(), projectId,
            new AsyncRequestCallback<List<Token>>()
            {

               @Override
               protected void onSuccess(List<Token> result)
               {
                  openImportForm(markOffsetY, markOffsetX, result, factory, JavaCodeAssistant.this);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  exception.printStackTrace();
                  errorHandler.handleError(exception);
               }
            });
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, int cursorOffsetX, int cursorOffsetY, List<Token> tokenList,
      String lineMimeType, Token currentToken)
   {
      this.editor = editor;
      this.posX = cursorOffsetX;
      this.posY = cursorOffsetY;
      //      printTokens(tokenList, 1);
      try
      {
         currentLineNumber = editor.getCursorRow();
         String lineContent = editor.getLineContent(currentLineNumber);
         if (lineContent == null)
         {
            beforeToken = "";
            tokenToComplete = "";
            afterToken = "";
            openForm(new ArrayList<Token>(), factory, this);
            return;
         }

         String subToken = lineContent.substring(0, editor.getCursorCol() - 1);
         afterToken = lineContent.substring(editor.getCursorCol() - 1);

         String token = "";
         if (!subToken.endsWith(" "))
         {
            String[] split = subToken.split("[ /+=!<>(){}\\[\\]?|&:\",'\\-;]+");

            if (split.length != 0)
            {
               token = split[split.length - 1];
            }
         }

         if (token.contains("."))
         {
            String varToken = token.substring(0, token.lastIndexOf('.'));
            tokenToComplete = token.substring(token.lastIndexOf('.') + 1);
            beforeToken = subToken.substring(0, subToken.lastIndexOf(varToken) + varToken.length() + 1);
            showMethods(currentToken, varToken);
         }
         else
         {

            beforeToken = subToken.substring(0, subToken.lastIndexOf(token));
            tokenToComplete = token;
            this.tokenFromParser = filterTokenFromParser(tokenFromParser, currentToken);
            action = Action.CLASS_NAME_AND_LOCAL_VAR;
            //if token to complete is only whitespace string
            if (tokenToComplete.matches("^[ ]+&") || "".equals(tokenToComplete))
            {
               action = Action.LOCAL_VAR;
               filterTokens(new ArrayList<Token>());
               return;
            }

            //if annotation
            if (token.startsWith("@"))
            {
               action = Action.ANNOTATION;
               beforeToken += "@";
               tokenToComplete = tokenToComplete.substring(1);
               service.findType(Types.ANNOTATION, tokenToComplete, projectId, new AsyncRequestCallback<List<Token>>()
               {

                  @Override
                  protected void onSuccess(List<Token> result)
                  {
                     filterTokens(result);
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     errorHandler.handleError(exception);
                  }
               });
               return;
            }

            service.findClassesByPrefix(tokenToComplete, projectId, new AsyncRequestCallback<List<Token>>()
            {

               @Override
               protected void onSuccess(List<Token> result)
               {
                  filterTokens(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  errorHandler.handleError(exception);
               }
            });
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @param currentToken
    * @param subToken
    * @param token
    */
   protected void showMethods(Token currentToken, String varToken)
   {

      if (currentToken == null)
      {
         openForm(new ArrayList<Token>(), factory, this);
         return;
      }

      if (currentToken.getType() != null && currentToken.getType() == TokenType.TYPE)
      {
         action = Action.PUBLIC_STATIC;
      }
      else
      {
         action = Action.PUBLIC;
      }

      if (!currentToken.hasProperty(TokenProperties.FQN))
      {
         openForm(new ArrayList<Token>(), factory, this);
         return;
      }

      curentFqn = currentToken.getProperty(TokenProperties.FQN).isStringProperty().stringValue();
      if (curentFqn == null)
      {
         openForm(new ArrayList<Token>(), factory, this);
         return;
      }
      getClassDescription();
   }

   /**
    * Send request for class description(methods, fields, constructors ...)
    */
   protected void getClassDescription()
   {
      service.getClassDescription(curentFqn, projectId, new AsyncRequestCallback<JavaClass>()
      {

         @Override
         protected void onSuccess(JavaClass result)
         {
            classDescriptionReceived(result);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            errorHandler.handleError(exception);
         }
      });
   }

   private List<Token> filterTokenFromParser(List<Token> tokenFromParser, Token currentToken)
   {
      List<Token> tokens = new ArrayList<Token>();
      if (currentToken != null)
      {
         switch (currentToken.getType())
         {
            case METHOD :
               tokens.addAll(getTokensForCurrentMethod(currentToken));
               Token parent =
                  (Token)currentToken.getProperty(TokenProperties.PARENT_TOKEN).isObjectProperty().objectValue();
               tokens.add(parent);
               tokens.addAll(parent.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue());
               break;
            case CLASS :
            case PROPERTY :
               tokens.addAll(getAllMethodsFromClass(currentToken));
               tokens.add(currentToken);
               break;
         }
      }
      return tokens;
   }

   /**
    * Filter {@link JavaClass} by specific {@link Action} and 
    * call {@link TokensCollectedCallback#onTokensCollected(List, String, String, String)}
    * to complete collect tokens
    * 
    * @param gClass {@link JavaClass} that represent current class
    */
   private void filterTokens(JavaClass gClass)
   {
      List<Token> arrayList = new ArrayList<Token>();
      switch (action)
      {
         case PUBLIC_STATIC :
            arrayList.addAll(collectPublicStaticFileldsAndMethods(gClass));
            break;

         case PUBLIC :
         default :
            arrayList.addAll(collectPublicFieldsAndMethods(gClass));
            break;
      }

      Collections.sort(arrayList, this);
      openForm(arrayList, factory, this);
   }

   /**
    * Get all <b><code>public static</code></b> fields and methods of Class
    * 
    * @param JavaClass {@link JavaClass}
    * @return {@link List} of {@link Token} 
    */
   private List<Token> collectPublicStaticFileldsAndMethods(JavaClass JavaClass)
   {
      List<Token> staticList = new ArrayList<Token>();
      for (Token t : JavaClass.getPublicFields())
      {
         if (ModifierHelper.isStatic((t.getProperty(TokenProperties.MODIFIERS).isNumericProperty().numericValue()
            .intValue())))
         {
            staticList.add(t);
         }
      }

      for (Token t : JavaClass.getPublicMethods())
      {
         int mod = (t.getProperty(TokenProperties.MODIFIERS).isNumericProperty().numericValue().intValue());
         if (ModifierHelper.isStatic(mod) || ModifierHelper.isAbstract(mod))
         {
            staticList.add(t);
         }
      }

      return staticList;
   }

   /**
    * Get all <b><code>public</code></b> and <b><code>public static</code></b>
    * fields and methods of class
    * 
    * @param JavaClass {@link JavaClass}
    * @return {@link List} of {@link Token}
    */
   private List<Token> collectPublicFieldsAndMethods(JavaClass JavaClass)
   {
      List<Token> arrayList = new ArrayList<Token>();
      arrayList.addAll(JavaClass.getPublicFields());
      arrayList.addAll(JavaClass.getPublicMethods());
      return arrayList;
   }

   private void classDescriptionReceived(JavaClass gClass)
   {
      //      classes.put(curentFqn, event.getClassInfo());

      filterTokens(gClass);
   }

   /**
    * @param arrayList
    */
   private void filterTokens(List<Token> classNames)
   {
      final List<Token> token = new ArrayList<Token>();

      if (action == Action.ANNOTATION)
      {
         for (Token t : classNames)
         {
            if (t.getType() == TokenType.ANNOTATION)
            {
               token.add(t);
            }
         }
      }
      else
      {
         for (Token t : tokenFromParser)
         {
            token.add(t);
            switch (t.getType())
            {
               case METHOD :
                  String param = "(";
                  if (t.hasProperty(TokenProperties.PARAMETERS)
                     && t.getProperty(TokenProperties.PARAMETERS).isArrayProperty().arrayValue() != null)
                  {
                     for (Token p : t.getProperty(TokenProperties.PARAMETERS).isArrayProperty().arrayValue())
                     {
                        param += p.getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue() + ", ";
                     }
                     if (param.endsWith(", "))
                     {
                        param = param.substring(0, param.lastIndexOf(", "));
                     }
                  }
                  param += ")";
                  t.setProperty(TokenProperties.PARAMETER_TYPES, new StringProperty(param));
               case PROPERTY :
               case FIELD :
                  t.setProperty(TokenProperties.DECLARING_CLASS, getDecalringClassName(t));
                  break;
               case CLASS :
                  t.setProperty(TokenProperties.FQN, new StringProperty(t.getName()));
                  break;

            }

         }

         token.addAll(classNames);
         if (!tokenToComplete.isEmpty())
         {
            if (keywords == null)
            {
               JavaBundle bundle = GWT.create(JavaBundle.class);
               try
               {
                  bundle.javakeyWords().getText(new ResourceCallback<TextResource>()
                  {

                     @Override
                     public void onSuccess(TextResource resource)
                     {
                        parseKeyWords(resource);
                        token.addAll(keywords);
                        callOpenForm(token);
                     }

                     @Override
                     public void onError(ResourceException e)
                     {
                        errorHandler.handleError(e);
                     }
                  });

                  return;
               }
               catch (ResourceException e)
               {
                  e.printStackTrace();
               }
            }

            token.addAll(keywords);

         }
      }
      callOpenForm(token);
   }

   protected void callOpenForm(List<Token> tokens)
   {
      Collections.sort(tokens, this);
      openForm(tokens, factory, this);
   }

   private StringProperty getDecalringClassName(Token token)
   {
      String name = "";
      if (token.hasProperty(TokenProperties.PARENT_TOKEN))
      {
         Token parent = (Token)token.getProperty(TokenProperties.PARENT_TOKEN).isObjectProperty().objectValue();
         name = parent.getName();
      }
      return new StringProperty(name);
   }

   /**
    * Parse JSON token to {@link Token} beens
    * @param resource JSON
    */
   protected void parseKeyWords(TextResource resource)
   {
      JSONTokenParser parser = new JSONTokenParser();
      keywords = parser.getTokens(new JSONArray(parseJson(resource.getText())));
   }

   /**
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(Token t1, Token t2)
   {
      if (t1.getType() == t2.getType())
      {
         return t1.getName().compareTo(t2.getName());
      }

      if ((t1.getType() == TokenType.PARAMETER && t2.getType() == TokenType.VARIABLE)
         || (t1.getType() == TokenType.VARIABLE && t2.getType() == TokenType.PARAMETER))
      {
         return t1.getName().compareTo(t2.getName());
      }

      if (t2.getType() == TokenType.PARAMETER)
      {
         return 1;
      }
      if (t1.getType() == TokenType.PARAMETER)
      {
         return -1;
      }

      if (t2.getType() == TokenType.VARIABLE)
      {
         return 1;
      }
      if (t1.getType() == TokenType.VARIABLE)
      {
         return -1;
      }
      if (t1.getType() == TokenType.FIELD)
      {
         return -1;
      }

      if (t2.getType() == TokenType.FIELD)
      {
         return 1;
      }

      if (t1.getType() == TokenType.METHOD || t1.getType() == TokenType.PROPERTY)
      {
         return -1;
      }

      if (t2.getType() == TokenType.METHOD || t2.getType() == TokenType.PROPERTY)
      {
         return 1;
      }

      return t1.getName().compareTo(t2.getName());
   }

   /**
    * @param currentClass
    * @return
    */
   private Collection<? extends Token> getAllMethodsFromClass(Token currentClass)
   {
      List<Token> tokens = new ArrayList<Token>();
      if (currentClass.hasProperty(TokenProperties.SUB_TOKEN_LIST))
      {
         for (Token t : currentClass.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue())
         {
            if (t.getType() == TokenType.METHOD || t.getType() == TokenType.CLASS)
            {
               tokens.add(t);
            }
         }
      }
      return tokens;
   }

   /**
    * @param currentMethod
    * @return
    */
   private Collection<? extends Token> getTokensForCurrentMethod(Token currentMethod)
   {
      List<Token> tokens = new ArrayList<Token>();
      if (currentMethod.hasProperty(TokenProperties.PARAMETERS)
         && currentMethod.getProperty(TokenProperties.PARAMETERS).isArrayProperty().arrayValue() != null)
      {
         tokens.addAll(currentMethod.getProperty(TokenProperties.PARAMETERS).isArrayProperty().arrayValue());
      }
      if (currentMethod.hasProperty(TokenProperties.SUB_TOKEN_LIST))
      {
         for (Token t : currentMethod.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue())
         {
            if (t.getProperty(TokenProperties.LINE_NUMBER).isNumericProperty().numericValue().intValue() < currentLineNumber)
            {
               tokens.add(t);
            }
         }
      }

      return tokens;
   }

   public void setActiveProjectId(String projectId)
   {
      this.projectId = projectId;
   }

}
