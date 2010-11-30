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
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
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
public class GroovyTokenCollector implements TokenCollectorExt, ClassDescriptionReceivedHandler, Comparator<TokenExt>, ExceptionThrownHandler
{

   private static Map<String, GroovyClass> classes = new HashMap<String, GroovyClass>();
   
   private Handlers handlers;

   private TokensCollectedCallback<TokenExt> callback;
   
   private String beforeToken;
   
   private String tokenToComplete;
   
   private String afterToken;
   
   private String curentFqn;
   

   public GroovyTokenCollector(HandlerManager eventBus)
   {
      handlers = new Handlers(eventBus);
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenCollector#getTokens(java.lang.String, java.lang.String, int, int, java.util.List, org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback)
    */
   @Override
   public void getTokens(String line, String fqn, int lineNum, int cursorPos, List<Token> tokenFromParser,
      TokensCollectedCallback<TokenExt> tokensCollectedCallback)
   {
      this.callback = tokensCollectedCallback;
      
      if (line.endsWith(".") && fqn != null)
      {
         beforeToken = line;
         tokenToComplete = "";
         afterToken = "";
         if(classes.containsKey(fqn))
         {
            collectPublicInterface(classes.get(fqn));
            return;
         }
         
         curentFqn = fqn;
         handlers.addHandler(ClassDescriptionReceivedEvent.TYPE, this);
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);
         
         CodeAssistantService.getInstance().getClassDescription(fqn);         
      }
      else
      {
         callback.onTokensCollected(new ArrayList<TokenExt>(), line, "", "");
      }
      
   }

   /**
    * @param groovyClass
    */
   private void collectPublicInterface(GroovyClass groovyClass)
   {
      List<TokenExt> arrayList = new ArrayList<TokenExt>();
      arrayList.addAll(groovyClass.getPublicFields());
      arrayList.addAll(groovyClass.getPublicMethods());
      Collections.sort(arrayList, this);
      callback.onTokensCollected(arrayList, beforeToken, tokenToComplete, afterToken);
      
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassDescriptionReceivedHandler#onClassDecriptionReceived(org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassDescriptionReceivedEvent)
    */
   @Override
   public void onClassDecriptionReceived(ClassDescriptionReceivedEvent event)
   {
      handlers.removeHandlers();
      
      classes.put(curentFqn, event.getClassInfo());
      
      collectPublicInterface(event.getClassInfo());
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
      if(t1.getType().equals(TokenExtType.CLASS) || 
         t1.getType().equals(TokenExtType.CONSTRUCTOR) ||
         t1.getType().equals(TokenExtType.FIELD) ||
         t1.getType().equals(TokenExtType.METHOD) 
      )
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
