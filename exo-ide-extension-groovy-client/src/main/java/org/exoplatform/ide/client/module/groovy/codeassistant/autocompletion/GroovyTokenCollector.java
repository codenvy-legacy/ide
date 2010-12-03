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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.editor.api.Token;
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

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 26, 2010 4:56:13 PM evgen $
 *
 */
public class GroovyTokenCollector implements TokenCollectorExt, ClassDescriptionReceivedHandler, Comparator<TokenExt>,
   ExceptionThrownHandler
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
      PUBLIC_CONSTRUCTORS
   }

   private static Map<String, GroovyClass> classes = new HashMap<String, GroovyClass>();

   private Handlers handlers;

   private TokensCollectedCallback<TokenExt> callback;

   private String beforeToken;

   private String tokenToComplete;

   private String afterToken;

   private String curentFqn;

   private Action action;

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

      this.callback = tokensCollectedCallback;

      String subToken = line.substring(0, cursorPos - 1);
      afterToken = line.substring(cursorPos - 1);

      String[] split = subToken.split("[ /+=!<>(){}\\[\\]?|&:\",'\\-;]+");
      
      String token = "";
      if(split.length != 0)
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
         System.out.println(curentFqn);
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

         callback.onTokensCollected(new ArrayList<TokenExt>(), line, "", "");
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
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(TokenExt t1, TokenExt t2)
   {
      if (t1.getType().equals(t1.getType()))
      {
         return t1.getName().compareTo(t2.getName());
      }
      if (t1.getType().equals(TokenExtType.CLASS) || t1.getType().equals(TokenExtType.CONSTRUCTOR)
         || t1.getType().equals(TokenExtType.FIELD) || t1.getType().equals(TokenExtType.METHOD))
      {
         return 1;
      }

      return -1;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   @Override
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

}
