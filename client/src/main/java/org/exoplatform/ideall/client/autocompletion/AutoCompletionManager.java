/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.autocompletion;

import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledEvent;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledHandler;
import org.exoplatform.ideall.client.autocompletion.groovy.GroovyTokenCollector;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AutoCompletionManager implements EditorAutoCompleteCalledHandler, TokensCollectedCallback
{

   private HashMap<String, TokenCollector> factories = new HashMap<String, TokenCollector>();

   public AutoCompletionManager(HandlerManager eventBus, ApplicationContext context) {
      factories.put(MimeType.SCRIPT_GROOVY, new GroovyTokenCollector(eventBus, context, this));
   }
   
   public void onEditorAutoCompleteCalled(EditorAutoCompleteCalledEvent event)
   {
      
   }

   public void onTokensCollected(List<String> tokens)
   {
      System.out.println("AutoCompletionManager.onTokensCollected()");
      
      
      
   }

}
