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

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.codeassistant.ImportDeclarationTokenCollector;
import org.exoplatform.ide.client.framework.codeassistant.ImportDeclarationTokenCollectorCallback;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.CodeAssistantService;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassesNamesReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassesNamesReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 22, 2010 2:47:20 PM evgen $
 *
 */
public class GroovyImportDeclarationTokenCollector implements ImportDeclarationTokenCollector, ClassesNamesReceivedHandler, ExceptionThrownHandler
{
   
   private Handlers handlers;
   
   private ImportDeclarationTokenCollectorCallback callback;

   /**
    * @param eventBus
    */
   public GroovyImportDeclarationTokenCollector(HandlerManager eventBus)
   {
      handlers = new Handlers(eventBus);       
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.ImportDeclarationTokenCollector#getImportDeclarationTokens(java.lang.String)
    */
   public void getImportDeclarationTokens(String className, ImportDeclarationTokenCollectorCallback callback)
   {
      this.callback = callback;
      handlers.addHandler(ClassesNamesReceivedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      
      CodeAssistantService.getInstance().findClass(className);
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassesNamesReceivedHandler#onClassesNamesReceived(org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassesNamesReceivedEvent)
    */
   public void onClassesNamesReceived(ClassesNamesReceivedEvent event)
   {
      handlers.removeHandlers();
      callback.tokensCollected(event.getTokens());
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
      
   }

}
