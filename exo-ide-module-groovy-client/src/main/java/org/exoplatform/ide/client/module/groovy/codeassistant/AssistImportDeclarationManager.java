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
package org.exoplatform.ide.client.module.groovy.codeassistant;

import java.util.List;

import org.exoplatform.gwtframework.editor.api.codeassitant.GroovyToken;
import org.exoplatform.gwtframework.editor.api.codeassitant.GroovyTokenProperties;
import org.exoplatform.gwtframework.editor.event.EditorErrorMarkClickedEvent;
import org.exoplatform.gwtframework.editor.event.EditorErrorMarkClickedHandler;
import org.exoplatform.gwtframework.editor.event.EditorInsertImportStatmentEvent;
import org.exoplatform.gwtframework.ui.client.component.codeassitant.AssistImportDeclarationForm;
import org.exoplatform.gwtframework.ui.client.component.codeassitant.AssistImportDeclarationHandler;
import org.exoplatform.ide.client.framework.codeassistant.ImportDeclarationTokenCollector;
import org.exoplatform.ide.client.framework.codeassistant.ImportDeclarationTokenCollectorCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 22, 2010 2:46:01 PM evgen $
 *
 */
public class AssistImportDeclarationManager implements EditorErrorMarkClickedHandler,
   AssistImportDeclarationHandler, ImportDeclarationTokenCollectorCallback
{

   private HandlerManager eventBus;

   private int left;

   private int top;

   private String editorId;
   

   /**
    * @param eventBus
    */
   public AssistImportDeclarationManager(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(EditorErrorMarkClickedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorErrorMarkClickedHandler#onEditorErrorMarkClicked(org.exoplatform.gwtframework.editor.event.EditorErrorMarkClickedEvent)
    */
   public void onEditorErrorMarkClicked(EditorErrorMarkClickedEvent event)
   {
//      if (!event.getCodeErrorList().isEmpty())
//      {
      
         left = event.getMarkOffsetY(); 
         top = event.getMarkOffsetX();
         System.out.println("left - " + left + " <<<>>> top - " + top);
         editorId = event.getEditorId();
         
         ImportDeclarationTokenCollector collector =
            ImportDeclarationsTokenCollectors.getCollector(eventBus, event.getFileMimeType());
//         collector.getImportDeclarationTokens(event.getCodeErrorList().get(0).getIncorrectToken(), this);
         collector.getImportDeclarationTokens("Boolean", this);
//      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.ImportDeclarationTokenCollectorCallback#tokensCollected(java.util.List)
    */
   public void tokensCollected(List<GroovyToken> tokens)
   {
      new AssistImportDeclarationForm(eventBus, left, top, tokens, AssistImportDeclarationImages.getImages(), this);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.codeassitant.AssistImportDeclarationHandler#onImportTockenSelected(org.exoplatform.gwtframework.editor.api.codeassitant.GroovyToken)
    */
   public void onImportTockenSelected(GroovyToken token)
   {
      eventBus.fireEvent(new EditorInsertImportStatmentEvent(editorId, token.getProperty(GroovyTokenProperties.FQN)));
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.codeassitant.AssistImportDeclarationHandler#onImportCancel()
    */
   public void onImportCancel()
   {
      eventBus.fireEvent(new EditorSetFocusEvent());
   }

}
