/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.groovy.client.codeassistant.autocompletion;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.Modifier;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ide.client.framework.codeassistant.ModifierHelper;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtProperties;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtType;
import org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback;
import org.exoplatform.ide.client.framework.codeassistant.api.TokenCollectorExt;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.extension.groovy.client.service.codeassistant.CodeAssistantService;
import org.exoplatform.ide.extension.groovy.client.service.codeassistant.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 26, 2010 4:56:13 PM evgen $
 *
 */
public class GroovyTokenCollector implements TokenCollectorExt, Comparator<TokenExt>, EditorActiveFileChangedHandler
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

   private static List<TokenExt> keywords = new ArrayList<TokenExt>();

   static
   {
      keywords.add(new TokenExt("abstract", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("as", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("assert", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("boolean", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("break", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("byte", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("case", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("catch", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("char", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("class", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("const", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("continue", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("def", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("default", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("do", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("double", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("else", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("enum", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("extends", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("false", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("final", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("finally", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("float", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("for", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("goto", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("if", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("implements", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("import", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("in", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("instanceof", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("int", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("interface", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("long", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("native", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("new", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("null", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("package", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("private", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("protected", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("public", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("return", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("short", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("static", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("strictfp", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("super", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("switch", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("synchronized", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("this", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("threadsafe", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("throw", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("throws", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("transient", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("true", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("try", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("void", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("volatile", TokenExtType.KEYWORD));
      keywords.add(new TokenExt("while", TokenExtType.KEYWORD));
   }

   //   private static Map<String, GroovyClass> classes = new HashMap<String, GroovyClass>();

   private TokensCollectedCallback<TokenExt> callback;

   private String beforeToken;

   private String tokenToComplete;

   private String afterToken;

   private String curentFqn;

   private Action action;

   private List<TokenExt> tokenFromParser;

   private int currentLineNumber;

   private File activeFile;

   private HandlerManager eventBus;

   public GroovyTokenCollector(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenCollector#getTokens(java.lang.String, java.lang.String, int, int, java.util.List, org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback)
    */
   @Override
   public void collectTokens(String line, Token currentToken, int lineNum, int cursorPos, List<Token> tokenFromParser,
      TokensCollectedCallback<TokenExt> tokensCollectedCallback)
   {

      this.callback = tokensCollectedCallback;
      if (line == null)
      {

         callback.onTokensCollected(new ArrayList<TokenExt>(), "", "", "");
         return;
      }

      currentLineNumber = lineNum;

      String subToken = line.substring(0, cursorPos - 1);
      afterToken = line.substring(cursorPos - 1);

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
            callback.onTokensCollected(new ArrayList<TokenExt>(), beforeToken, tokenToComplete, afterToken);
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

         curentFqn = currentToken.getFqn();
         //         if (classes.containsKey(curentFqn))
         //         {
         //            filterTokens(classes.get(curentFqn));
         //            return;
         //         }
         if(curentFqn == null)
         {
            callback.onTokensCollected(new ArrayList<TokenExt>(), beforeToken, tokenToComplete, afterToken);
            return;
         }
         
         CodeAssistantService.getInstance().getClassDescription(curentFqn, activeFile.getHref(),
            new AsyncRequestCallback<GroovyClass>()
            {

               @Override
               protected void onSuccess(GroovyClass result)
               {
                  classDescriptionReceived(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleClassDescriptionException(exception);
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
            filterTokens(new ArrayList<TokenExt>());
            return;
         }

         //if annotation
         if (token.startsWith("@"))
         {
            action = Action.ANNOTATION;
            beforeToken += "@";
            tokenToComplete = tokenToComplete.substring(1);
            CodeAssistantService.getInstance().fintType(Types.ANNOTATION, tokenToComplete,
               new AsyncRequestCallback<List<TokenExt>>()
               {

                  @Override
                  protected void onSuccess(List<TokenExt> result)
                  {
                     filterTokens(result);
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     handlerClassesReceivedError(exception);
                  }
               });
            return;
         }

         CodeAssistantService.getInstance().findClassesByPrefix(tokenToComplete, activeFile.getHref(),
            new AsyncRequestCallback<List<TokenExt>>()
            {

               @Override
               protected void onSuccess(List<TokenExt> result)
               {
                  filterTokens(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handlerClassesReceivedError(exception);
               }
            });
      }

   }
   
   private void handlerClassesReceivedError(Throwable exc)
   {
      if (exc instanceof ServerException)
      {
         ServerException exception = (ServerException)exc;
         String outputContent =
            "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }

         //         findLineNumberAndColNumberOfError(exception.getMessage());

         //         outputContent =
         //            "<span title=\"Go to error\" onClick=\"window.groovyGoToErrorFunction(" + String.valueOf(errLineNumber)
         //               + "," + String.valueOf(errColumnNumber) + ", '" + event.getFileHref() + "', '"
         //               + "');\" style=\"cursor:pointer;\">" + outputContent + "</span>";

         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent(exc.getMessage()));
      }
   }

   /**
    * Filter {@link GroovyClass} by specific {@link Action} and 
    * call {@link TokensCollectedCallback#onTokensCollected(List, String, String, String)}
    * to complete collect tokens
    * 
    * @param gClass {@link GroovyClass} that represent current class
    */
   private void filterTokens(GroovyClass gClass)
   {
      List<TokenExt> arrayList = new ArrayList<TokenExt>();
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
      callback.onTokensCollected(arrayList, beforeToken, tokenToComplete, afterToken);
   }

   /**
    * Get all <b><code>public static</code></b> fields and methods of Class
    * 
    * @param groovyClass {@link GroovyClass}
    * @return {@link List} of {@link TokenExt} 
    */
   private List<TokenExt> collectPublicStaticFileldsAndMethods(GroovyClass groovyClass)
   {
      List<TokenExt> staticList = new ArrayList<TokenExt>();
      for (TokenExt t : groovyClass.getPublicFields())
      {
         if (ModifierHelper.isStatic(ModifierHelper.getIntFromString(t.getProperty(TokenExtProperties.MODIFIERS))))
         {
            staticList.add(t);
         }
      }

      for (TokenExt t : groovyClass.getPublicMethods())
      {
         int mod = ModifierHelper.getIntFromString(t.getProperty(TokenExtProperties.MODIFIERS));
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
    * @param groovyClass {@link GroovyClass}
    * @return {@link List} of {@link TokenExt}
    */
   private List<TokenExt> collectPublicFieldsAndMethods(GroovyClass groovyClass)
   {
      List<TokenExt> arrayList = new ArrayList<TokenExt>();
      arrayList.addAll(groovyClass.getPublicFields());
      arrayList.addAll(groovyClass.getPublicMethods());
      return arrayList;
   }

   private void handleClassDescriptionException(Throwable exc)
   {
      if (exc instanceof ServerException)
      {
         ServerException exception = (ServerException)exc;
         String outputContent =
            "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }

         //         findLineNumberAndColNumberOfError(exception.getMessage());

         //         outputContent =
         //            "<span title=\"Go to error\" onClick=\"window.groovyGoToErrorFunction(" + String.valueOf(errLineNumber)
         //               + "," + String.valueOf(errColumnNumber) + ", '" + event.getFileHref() + "', '"
         //               + "');\" style=\"cursor:pointer;\">" + outputContent + "</span>";

         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));

      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent(exc.getMessage()));
      }
   }
   
   private void classDescriptionReceived(GroovyClass gClass)
   {
      //      classes.put(curentFqn, event.getClassInfo());

      filterTokens(gClass);
   }

   //   /**
   //    * Parse text and find number of column and line number of error
   //    * 
   //    * @param text validation text, which contains number of column and line number of error
   //    */
   //   private void findLineNumberAndColNumberOfError(String text)
   //   {
   //      try
   //      {
   //         //find line number
   //         int firstIndex = text.indexOf("@ line") + 7;
   //         int lastIndex = text.indexOf(", column");
   ////         errLineNumber = Integer.valueOf(text.substring(firstIndex, lastIndex));
   //
   //         //find column number
   //         firstIndex = lastIndex + 9;
   //         lastIndex = text.indexOf(".", firstIndex);
   ////         errColumnNumber = Integer.valueOf(text.substring(firstIndex, lastIndex));
   //
   //      }
   //      catch (Exception e)
   //      {
   //         e.printStackTrace();
   //      }
   //   }
   
   /**
    * @param arrayList
    */
   private void filterTokens(List<TokenExt> classNames)
   {
      List<TokenExt> token = new ArrayList<TokenExt>();

      if (action == Action.ANNOTATION)
      {
         for (TokenExt t : classNames)
         {
            if (t.getType() == TokenExtType.ANNOTATION)
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
      callback.onTokensCollected(token, beforeToken, tokenToComplete, afterToken);
   }

   /**
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(TokenExt t1, TokenExt t2)
   {
      if (t1.getType() == t2.getType())
      {
         return t1.getName().compareTo(t2.getName());
      }

      if (t2.getType() == TokenExtType.VARIABLE)
      {
         return 1;
      }
      if (t1.getType() == TokenExtType.VARIABLE)
      {
         return -1;
      }

      if (t1.getType() == TokenExtType.METHOD || t1.getType() == TokenExtType.FIELD)
      {
         return -1;
      }

      if (t2.getType() == TokenExtType.METHOD || t2.getType() == TokenExtType.FIELD)
      {
         return 1;
      }

      return t1.getName().compareTo(t2.getName());
   }

   private List<TokenExt> convertTokens(List<Token> tokensFromParser)
   {
      List<TokenExt> tokens = new ArrayList<TokenExt>();
      for (Token t : tokensFromParser)
      {
         final TokenExt tex;
         int modifires = 0;
         String cl = "";
         switch (t.getType())
         {
            case CLASS :
               tex = new TokenExt(t.getName(), TokenExtType.CLASS);
               tex.setProperty(TokenExtProperties.FQN, t.getName());
               modifires = getModifires(t.getModifiers());
               tex.setProperty(TokenExtProperties.MODIFIERS, String.valueOf(modifires));
               tokens.add(tex);
               break;
            case METHOD :
               tex = new TokenExt(t.getName(), TokenExtType.METHOD);
               tex.setProperty(TokenExtProperties.RETURNTYPE, t.getElementType());
               String param = "(";
               if (t.getParameters() != null)
               {
                  for (Token p : t.getParameters())
                  {
                     param += p.getElementType() + ", ";
                  }
                  if (param.endsWith(", "))
                  {
                     param = param.substring(0, param.lastIndexOf(", "));
                  }
               }
               param += ")";
               tex.setProperty(TokenExtProperties.PARAMETERTYPES, param);
               modifires = getModifires(t.getModifiers());
               tex.setProperty(TokenExtProperties.MODIFIERS, String.valueOf(modifires));
               cl = t.getParentToken().getName();
               tex.setProperty(TokenExtProperties.DECLARINGCLASS, cl);
               tokens.add(tex);

               break;

            case PROPERTY :
               tex = new TokenExt(t.getName(), TokenExtType.FIELD);
               tex.setProperty(TokenExtProperties.FQN, t.getFqn());
               tex.setProperty(TokenExtProperties.TYPE, t.getElementType());
               modifires = getModifires(t.getModifiers());
               tex.setProperty(TokenExtProperties.MODIFIERS, String.valueOf(modifires));
               cl = t.getParentToken().getName();
               tex.setProperty(TokenExtProperties.DECLARINGCLASS, cl);
               tokens.add(tex);
               break;

            case VARIABLE :
            case PARAMETER :
               tex = new TokenExt(t.getName(), TokenExtType.VARIABLE);
               tex.setProperty(TokenExtProperties.FQN, t.getFqn());
               tex.setProperty(TokenExtProperties.TYPE, t.getElementType());
               modifires = t.getModifiers() == null ? 0 : getModifires(t.getModifiers());
               tex.setProperty(TokenExtProperties.MODIFIERS, String.valueOf(modifires));
               tokens.add(tex);
               break;

            default :
               break;
         }
      }

      return tokens;
   }

   List<TokenExt> filterTokenFromParser(List<Token> tokenFromParser, Token currentToken)
   {
      List<Token> tokens = new ArrayList<Token>();
      if (currentToken != null)
      {
         switch (currentToken.getType())
         {
            case METHOD :
               tokens.addAll(getTokensForCurrentMethod(currentToken));
               tokens.add(currentToken.getParentToken());
               tokens.addAll(currentToken.getParentToken().getSubTokenList());
               break;
            case CLASS :
            case PROPERTY :
               tokens.addAll(getAllMethodsFromClass(currentToken));
               tokens.add(currentToken);
               break;
         }
      }
      return convertTokens(tokens);
   }

   /**
    * @param currentClass
    * @return
    */
   private Collection<? extends Token> getAllMethodsFromClass(Token currentClass)
   {
      List<Token> tokens = new ArrayList<Token>();
      if (currentClass.getSubTokenList() != null)
      {
         for (Token t : currentClass.getSubTokenList())
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
      if (currentMethod.getParameters() != null)
      {
         tokens.addAll(currentMethod.getParameters());
      }
      if (currentMethod.getSubTokenList() != null)
      {
         for (Token t : currentMethod.getSubTokenList())
         {
            if (t.getLineNumber() < currentLineNumber)
            {
               tokens.add(t);
            }
         }
      }

      return tokens;
   }

   /**
    * @param currentClass
    * @param tokenFromParser2
    * @return
    */
   private List<Token> getClasses(List<Token> tokens)
   {
      List<Token> classes = new ArrayList<Token>();
      for (Token t : tokens)
      {
         if (t.getType() == TokenType.CLASS)
         {
            classes.add(t);
         }
      }
      return classes;
   }

   /**
    * @param modifiers
    * @return
    */
   private int getModifires(List<Modifier> modifiers)
   {
      int i = 0;
      for (Modifier m : modifiers)
      {
         i = i | m.value();
      }
      return i;
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   //      /**
   //       * Print recursively all tokens
   //       * 
   //       * @param token {@link List} of {@link Token} to print
   //       */
   //      private void printTokens(List<Token> token)
   //      {
   //   
   //         for (Token t : token)
   //         {
   //            System.out.println(t.getName() + " " + t.getType());
   //            System.out.println("FQN - " + t.getFqn());
   //            System.out.println("JAVATYPE - " + t.getElementType());
   //            //         if (t.getSubTokenList() != null)
   //            //         {
   //            //            printTokens(t.getSubTokenList());
   //            //         }
   //            //         if (t.getParameters() != null)
   //            //         {
   //            //            printTokens(t.getParameters());
   //            //         }
   //         }
   //         System.out.println("+++++++++++++++++++++++++");
   //      }
}
