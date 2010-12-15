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
package org.exoplatform.ide.client.module.groovy.codeassistant.autocompletion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.Modifier;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ide.client.framework.codeassistant.ModifierHelper;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtProperties;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtType;
import org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback;
import org.exoplatform.ide.client.framework.codeassistant.api.TokenCollectorExt;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.CodeAssistantService;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassDescriptionReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassDescriptionReceivedHandler;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassesNamesReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassesNamesReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 26, 2010 4:56:13 PM evgen $
 *
 */
public class GroovyTokenCollector implements TokenCollectorExt, ClassDescriptionReceivedHandler, Comparator<TokenExt>,
   ExceptionThrownHandler, ClassesNamesReceivedHandler
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
      LOCAL_VAR;

   }

   private static Map<String, GroovyClass> classes = new HashMap<String, GroovyClass>();

   private Handlers handlers;

   private TokensCollectedCallback<TokenExt> callback;

   private String beforeToken;

   private String tokenToComplete;

   private String afterToken;

   private String curentFqn;

   private Action action;

   private List<TokenExt> tokenFromParser;

   private int currentLineNumber;

   public GroovyTokenCollector(HandlerManager eventBus)
   {
      handlers = new Handlers(eventBus);
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenCollector#getTokens(java.lang.String, java.lang.String, int, int, java.util.List, org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback)
    */
   @Override
   public void collectTokens(String line, Token currentToken, int lineNum, int cursorPos, List<Token> tokenFromParser,
      TokensCollectedCallback<TokenExt> tokensCollectedCallback)
   {

      if (line == null || line.isEmpty())
      {
         callback.onTokensCollected(new ArrayList<TokenExt>(), "", "", "");
         return;
      }

      printTokens(tokenFromParser);

      this.callback = tokensCollectedCallback;
      currentLineNumber = lineNum;

      String subToken = line.substring(0, cursorPos - 1);
      afterToken = line.substring(cursorPos - 1);

      String[] split = subToken.split("[ /+=!<>(){}\\[\\]?|&:\",'\\-;]+");

      String token = "";
      if (split.length != 0)
      {
         token = split[split.length - 1];
      }

      if (token.contains("."))
      {

         String varToken = token.substring(0, token.lastIndexOf('.'));
         tokenToComplete = token.substring(token.lastIndexOf('.') + 1);
         beforeToken = subToken.substring(0, subToken.indexOf(varToken) + varToken.length() + 1);

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
         if (classes.containsKey(curentFqn))
         {
            filterTokens(classes.get(curentFqn));
            return;
         }

         handlers.addHandler(ClassDescriptionReceivedEvent.TYPE, this);
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);

         CodeAssistantService.getInstance().getClassDescription(curentFqn);
      }
      else
      {

         beforeToken = subToken.substring(0, subToken.lastIndexOf(token));
         tokenToComplete = token;
         this.tokenFromParser = filterTokenFromParser(tokenFromParser);
         action = Action.CLASS_NAME_AND_LOCAL_VAR;
         //if token to complete is only whitespace string
         if (tokenToComplete.matches("^[ ]+&") || "".equals(tokenToComplete))
         {
            action = Action.LOCAL_VAR;
            filterTokens(new ArrayList<TokenExt>());
            return;
         }

         handlers.addHandler(ExceptionThrownEvent.TYPE, this);
         handlers.addHandler(ClassesNamesReceivedEvent.TYPE, this);
         CodeAssistantService.getInstance().findClassesByPrefix(tokenToComplete);
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

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassDescriptionReceivedHandler#onClassDecriptionReceived(org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassDescriptionReceivedEvent)
    */
   @Override
   public void onClassDecriptionReceived(ClassDescriptionReceivedEvent event)
   {
      handlers.removeHandlers();

      classes.put(curentFqn, event.getClassInfo());

      filterTokens(event.getClassInfo());
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassesNamesReceivedHandler#onClassesNamesReceived(org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassesNamesReceivedEvent)
    */
   @Override
   public void onClassesNamesReceived(ClassesNamesReceivedEvent event)
   {
      handlers.removeHandlers();
      filterTokens(event.getTokens());
   }

   /**
    * @param arrayList
    */
   private void filterTokens(List<TokenExt> classNames)
   {
      List<TokenExt> token = new ArrayList<TokenExt>();

      token.addAll(tokenFromParser);
      if (action == Action.CLASS_NAME_AND_LOCAL_VAR)
      {
         for (TokenExt t : classNames)
         {
            if (t.getType() != TokenExtType.ANNOTATION)
            {
               token.add(t);
            }
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

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   @Override
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
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
               tex.setProperty(TokenExtProperties.RETURNTYPE, t.getJavaType());
               String param = "(";
               if (t.getParameters() != null)
               {
                  for (Token p : t.getParameters())
                  {
                     param += p.getJavaType() + ", ";
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
               tex.setProperty(TokenExtProperties.TYPE, t.getJavaType());
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
               tex.setProperty(TokenExtProperties.TYPE, t.getJavaType());
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

   List<TokenExt> filterTokenFromParser(List<Token> tokenFromParser)
   {
      List<Token> tokens = new ArrayList<Token>();

      Token currentClass = getCurrentClassToken(tokenFromParser);
      if (currentClass != null)
      {
         tokens.add(currentClass);
         tokens.addAll(getOtherClasses(currentClass, tokenFromParser));
         Token currentToken = getCurrentTokenInClass(currentClass);
         if (currentToken != null)
         {
            switch (currentToken.getType())
            {
               case METHOD :
                  tokens.addAll(getTokensForCurrentMethod(currentToken));
                  tokens.addAll(currentClass.getSubTokenList());
                  break;
               case CLASS :
               case PROPERTY :
                  tokens.addAll(getAllMethodsFromClass(currentClass));
                  break;
               default :
                  break;
            }
         }
         else
         {
            if(currentClass.getSubTokenList() != null)
            {
               tokens.addAll(getAllMethodsFromClass(currentClass));
            }
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
      if(currentClass.getSubTokenList() != null)
      {
         for(Token t : currentClass.getSubTokenList())
         {
            if(t.getType() == TokenType.METHOD || t.getType() == TokenType.CLASS)
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
    * @return
    */
   private Token getCurrentTokenInClass(Token currentClass)
   {
      Token currentToken = null;
      if (currentClass.getSubTokenList() != null)
      {
         for (Token t : currentClass.getSubTokenList())
         {

            if (t.getLineNumber() < currentLineNumber)
            {
               if (currentToken == null)
               {
                  currentToken = t;
                  continue;
               }
               if (t.getLineNumber() > currentToken.getLineNumber())
               {
                  currentToken = t;
               }
            }

         }
      }
      return currentToken;
   }

   /**
    * @param currentClass
    * @param tokenFromParser2
    * @return
    */
   private List<Token> getOtherClasses(Token currentClass, List<Token> tokens)
   {
      List<Token> classes = new ArrayList<Token>();
      for (Token t : tokens)
      {
         if (t.getType() == TokenType.CLASS)
         {
            if (!t.equals(currentClass))
            {
               classes.add(t);
            }
         }
      }
      return classes;
   }

   /**
    * @param t
    * @param tokenFromParser2
    * @return
    */
   private Token getCurrentClassToken(List<Token> tokens)
   {
      Token current = null;

      for (Token tt : tokens)
      {
         if (tt.getType() == TokenType.CLASS)
         {
            if (tt.getLineNumber() < currentLineNumber)
            {
               if (current == null)
               {
                  current = tt;
                  continue;
               }
               if (tt.getLineNumber() > current.getLineNumber())
               {
                  current = tt;
               }
            }
         }
      }

      return current;
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
    * Print recursively all tokens
    * 
    * @param token {@link List} of {@link Token} to print
    */
   private void printTokens(List<Token> token)
   {

      for (Token t : token)
      {
         System.out.println(t.getName() + " " + t.getType());
         System.out.println("FQN - " + t.getFqn());
         System.out.println("JAVATYPE - " + t.getJavaType());
//         if (t.getSubTokenList() != null)
//         {
//            printTokens(t.getSubTokenList());
//         }
//         if (t.getParameters() != null)
//         {
//            printTokens(t.getParameters());
//         }
      }
      System.out.println("+++++++++++++++++++++++++");
   }
}
