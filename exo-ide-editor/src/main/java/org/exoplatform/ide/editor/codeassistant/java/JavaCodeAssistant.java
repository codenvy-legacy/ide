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
package org.exoplatform.ide.editor.codeassistant.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.java.service.CodeAssistantService;
import org.exoplatform.ide.editor.codeassistant.java.service.Types;
import org.exoplatform.ide.editor.codeassistant.java.service.marshal.JavaClass;
import org.exoplatform.ide.editor.codeassistant.util.ModifierHelper;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaCodeAssistant Mar 2, 2011 4:41:28 PM evgen $
 *
 */
public class JavaCodeAssistant extends CodeAssistant implements Comparator<Token>
{
   private enum Action {

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

   private static List<TokenImpl> keywords = new ArrayList<TokenImpl>();

   static
   {
      keywords.add(new TokenImpl("abstract", TokenType.KEYWORD));
      keywords.add(new TokenImpl("as", TokenType.KEYWORD));
      keywords.add(new TokenImpl("assert", TokenType.KEYWORD));
      keywords.add(new TokenImpl("boolean", TokenType.KEYWORD));
      keywords.add(new TokenImpl("break", TokenType.KEYWORD));
      keywords.add(new TokenImpl("byte", TokenType.KEYWORD));
      keywords.add(new TokenImpl("case", TokenType.KEYWORD));
      keywords.add(new TokenImpl("catch", TokenType.KEYWORD));
      keywords.add(new TokenImpl("char", TokenType.KEYWORD));
      keywords.add(new TokenImpl("class", TokenType.KEYWORD));
      keywords.add(new TokenImpl("const", TokenType.KEYWORD));
      keywords.add(new TokenImpl("continue", TokenType.KEYWORD));
      keywords.add(new TokenImpl("def", TokenType.KEYWORD));
      keywords.add(new TokenImpl("default", TokenType.KEYWORD));
      keywords.add(new TokenImpl("do", TokenType.KEYWORD));
      keywords.add(new TokenImpl("double", TokenType.KEYWORD));
      keywords.add(new TokenImpl("else", TokenType.KEYWORD));
      keywords.add(new TokenImpl("enum", TokenType.KEYWORD));
      keywords.add(new TokenImpl("extends", TokenType.KEYWORD));
      keywords.add(new TokenImpl("false", TokenType.KEYWORD));
      keywords.add(new TokenImpl("final", TokenType.KEYWORD));
      keywords.add(new TokenImpl("finally", TokenType.KEYWORD));
      keywords.add(new TokenImpl("float", TokenType.KEYWORD));
      keywords.add(new TokenImpl("for", TokenType.KEYWORD));
      keywords.add(new TokenImpl("goto", TokenType.KEYWORD));
      keywords.add(new TokenImpl("if", TokenType.KEYWORD));
      keywords.add(new TokenImpl("implements", TokenType.KEYWORD));
      keywords.add(new TokenImpl("import", TokenType.KEYWORD));
      keywords.add(new TokenImpl("in", TokenType.KEYWORD));
      keywords.add(new TokenImpl("instanceof", TokenType.KEYWORD));
      keywords.add(new TokenImpl("int", TokenType.KEYWORD));
      keywords.add(new TokenImpl("interface", TokenType.KEYWORD));
      keywords.add(new TokenImpl("long", TokenType.KEYWORD));
      keywords.add(new TokenImpl("native", TokenType.KEYWORD));
      keywords.add(new TokenImpl("new", TokenType.KEYWORD));
      keywords.add(new TokenImpl("null", TokenType.KEYWORD));
      keywords.add(new TokenImpl("package", TokenType.KEYWORD));
      keywords.add(new TokenImpl("private", TokenType.KEYWORD));
      keywords.add(new TokenImpl("protected", TokenType.KEYWORD));
      keywords.add(new TokenImpl("public", TokenType.KEYWORD));
      keywords.add(new TokenImpl("return", TokenType.KEYWORD));
      keywords.add(new TokenImpl("short", TokenType.KEYWORD));
      keywords.add(new TokenImpl("static", TokenType.KEYWORD));
      keywords.add(new TokenImpl("strictfp", TokenType.KEYWORD));
      keywords.add(new TokenImpl("super", TokenType.KEYWORD));
      keywords.add(new TokenImpl("switch", TokenType.KEYWORD));
      keywords.add(new TokenImpl("synchronized", TokenType.KEYWORD));
      keywords.add(new TokenImpl("this", TokenType.KEYWORD));
      keywords.add(new TokenImpl("threadsafe", TokenType.KEYWORD));
      keywords.add(new TokenImpl("throw", TokenType.KEYWORD));
      keywords.add(new TokenImpl("throws", TokenType.KEYWORD));
      keywords.add(new TokenImpl("transient", TokenType.KEYWORD));
      keywords.add(new TokenImpl("true", TokenType.KEYWORD));
      keywords.add(new TokenImpl("try", TokenType.KEYWORD));
      keywords.add(new TokenImpl("void", TokenType.KEYWORD));
      keywords.add(new TokenImpl("volatile", TokenType.KEYWORD));
      keywords.add(new TokenImpl("while", TokenType.KEYWORD));
   }

   private String activeFileHref;

   private TokenWidgetFactory factory;

   private JavaCodeAssistantErrorHandler errorHandler;

   private String curentFqn;

   private Action action;

   private List<Token> tokenFromParser;

   private int currentLineNumber;

   /**
    * @param factory
    */
   public JavaCodeAssistant(TokenWidgetFactory factory, JavaCodeAssistantErrorHandler errorHandler)
   {
      super();
      this.factory = factory;
      this.errorHandler = errorHandler;
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarckClicked(org.exoplatform.ide.editor.api.Editor, java.util.List, int, int, java.lang.String)
    */
   @Override
   public void errorMarckClicked(Editor editor, List<CodeLine> codeErrorList, final int markOffsetX,
      final int markOffsetY, String fileMimeType)
   {
      this.editor = editor;
      try
      {
         System.out.println("JavaCodeAssistant.errorMarckClicked()");
         System.out.println(codeErrorList.get(0).getLineContent());
         CodeAssistantService.getInstance().findClass(codeErrorList.get(0).getLineContent(), activeFileHref,
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
   public void autocompleteCalled(Editor editor, String mimeType, int cursorOffsetX, int cursorOffsetY,
      String lineContent, int cursorPositionX, int cursorPositionY, List<Token> tokenList, String lineMimeType,
      Token currentToken)
   {
      this.editor = editor;
      this.posX = cursorOffsetX;
      this.posY = cursorOffsetY;
      try
      {
         if (lineContent == null)
         {
            beforeToken = "";
            tokenToComplete = "";
            afterToken = "";
            openForm(new ArrayList<Token>(), factory, this);
            return;
         }

         currentLineNumber = cursorPositionX;

         String subToken = lineContent.substring(0, cursorPositionX - 1);
         afterToken = lineContent.substring(cursorPositionX - 1);

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

            CodeAssistantService.getInstance().getClassDescription(curentFqn, activeFileHref,
               new AsyncRequestCallback<JavaClass>()
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
               CodeAssistantService.getInstance().fintType(Types.ANNOTATION, tokenToComplete,
                  new AsyncRequestCallback<List<Token>>()
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

            CodeAssistantService.getInstance().findClassesByPrefix(tokenToComplete, activeFileHref,
               new AsyncRequestCallback<List<Token>>()
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
         if (ModifierHelper.isStatic((t.getProperty(TokenProperties.MODIFIERS).isNumericProperty().numberValue()
            .intValue())))
         {
            staticList.add(t);
         }
      }

      for (Token t : JavaClass.getPublicMethods())
      {
         int mod = (t.getProperty(TokenProperties.MODIFIERS).isNumericProperty().numberValue().intValue());
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
      List<Token> token = new ArrayList<Token>();

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
         token.addAll(tokenFromParser);
         token.addAll(classNames);
         if (!tokenToComplete.isEmpty())
         {
            token.addAll(keywords);
         }
      }
      Collections.sort(token, this);
      openForm(token, factory, this);
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

      if (t2.getType() == TokenType.VARIABLE)
      {
         return 1;
      }
      if (t1.getType() == TokenType.VARIABLE)
      {
         return -1;
      }

      if (t1.getType() == TokenType.METHOD || t1.getType() == TokenType.FIELD)
      {
         return -1;
      }

      if (t2.getType() == TokenType.METHOD || t2.getType() == TokenType.FIELD)
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
      if (currentMethod.hasProperty(TokenProperties.PARAMETERS))
      {
         tokens.addAll(currentMethod.getProperty(TokenProperties.PARAMETERS).isArrayProperty().arrayValue());
      }
      if (currentMethod.hasProperty(TokenProperties.SUB_TOKEN_LIST))
      {
         for (Token t : currentMethod.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue())
         {
            if (t.getProperty(TokenProperties.LINE_NUMBER).isNumericProperty().numberValue().intValue() < currentLineNumber)
            {
               tokens.add(t);
            }
         }
      }

      return tokens;
   }


   public void setactiveFileHref(String href)
   {
      activeFileHref = href;
   }

}
