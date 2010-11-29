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
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.codeassistant.TokenCollector;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtType;
import org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback;
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
public class GroovyTokenCollector implements TokenCollector<TokenExt>, ClassDescriptionReceivedHandler, Comparator<TokenExt>
{

   private Handlers handlers;

   private TokensCollectedCallback<TokenExt> tokensCollectedCallback;

   public GroovyTokenCollector(HandlerManager eventBus)
   {
      handlers = new Handlers(eventBus);
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenCollector#getTokens(java.lang.String, java.lang.String, int, int, java.util.List, org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback)
    */
   @Override
   public void getTokens(String line, String lineMimeType, int lineNum, int cursorPos, List<TokenExt> tokenFromParser,
      TokensCollectedCallback<TokenExt> tokensCollectedCallback)
   {
      handlers.addHandler(ClassDescriptionReceivedEvent.TYPE, this);
      this.tokensCollectedCallback = tokensCollectedCallback;
      String fqn = "java.util.ArrayList";
      CodeAssistantService.getInstance().getClassDescription(fqn);
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassDescriptionReceivedHandler#onClassDecriptionReceived(org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassDescriptionReceivedEvent)
    */
   @Override
   public void onClassDecriptionReceived(ClassDescriptionReceivedEvent event)
   {
      handlers.removeHandlers();
      List<TokenExt> arrayList = new ArrayList<TokenExt>();
      arrayList.addAll(event.getClassInfo().getPublicConstructors());
      arrayList.addAll(event.getClassInfo().getPublicFields());
      arrayList.addAll(event.getClassInfo().getPublicMethods());
      Collections.sort(arrayList, this);
      tokensCollectedCallback.onTokensCollected(arrayList, "", "", "");

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

}
