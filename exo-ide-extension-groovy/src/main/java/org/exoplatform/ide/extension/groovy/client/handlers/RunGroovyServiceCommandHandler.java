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
package org.exoplatform.ide.extension.groovy.client.handlers;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.event.SaveFileEvent;
import org.exoplatform.ide.extension.groovy.client.event.DeployGroovyScriptSandboxEvent;
import org.exoplatform.ide.extension.groovy.client.event.RunGroovyServiceEvent;
import org.exoplatform.ide.extension.groovy.client.event.RunGroovyServiceHandler;
import org.exoplatform.ide.extension.groovy.client.event.UndeployGroovyScriptSandboxEvent;
import org.exoplatform.ide.extension.groovy.client.event.ValidateGroovyScriptEvent;
import org.exoplatform.ide.extension.groovy.client.launch_service.PreviewWadlOutputEvent;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.GroovyDeployResultReceivedHandler;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.GroovyValidateResultReceivedEvent;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.GroovyValidateResultReceivedHandler;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.RestServiceOutputReceivedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RunGroovyServiceCommandHandler implements RunGroovyServiceHandler, EditorActiveFileChangedHandler,
   FileSavedHandler, ExceptionThrownHandler, GroovyDeployResultReceivedHandler, GroovyValidateResultReceivedHandler,
   RestServiceOutputReceivedHandler
{

   private HandlerManager eventBus;

   /**
    * Used to remove handlers when they are no longer needed.
    */
   private Map<GwtEvent.Type<?>, HandlerRegistration> handlerRegistrations =
      new HashMap<GwtEvent.Type<?>, HandlerRegistration>();

   private FileModel activeFile;

   public RunGroovyServiceCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);      
      eventBus.addHandler(RunGroovyServiceEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.event.RunGroovyServiceHandler#onRunGroovyService(org.exoplatform.ide.client.module.groovy.event.RunGroovyServiceEvent)
    */
   public void onRunGroovyService(RunGroovyServiceEvent event)
   {      
      if (activeFile == null)
      {
         return;
      }
      
      if (activeFile.isContentChanged())
      {
         handlerRegistrations.put(FileSavedEvent.TYPE, eventBus.addHandler(FileSavedEvent.TYPE, this));
         handlerRegistrations.put(ExceptionThrownEvent.TYPE, eventBus.addHandler(ExceptionThrownEvent.TYPE, this));
         eventBus.fireEvent(new SaveFileEvent());
         return;
      }

      validateFile();

   }

   public void onFileSaved(FileSavedEvent event)
   {
      handlerRegistrations.get(FileSavedEvent.TYPE).removeHandler();
      validateFile();
   }

   protected void validateFile()
   {
      handlerRegistrations.put(GroovyValidateResultReceivedEvent.TYPE, eventBus.addHandler(GroovyValidateResultReceivedEvent.TYPE, this));
      eventBus.fireEvent(new ValidateGroovyScriptEvent());
   }

   public void onGroovyValidateResultReceived(GroovyValidateResultReceivedEvent event)
   {
      handlerRegistrations.get(GroovyValidateResultReceivedEvent.TYPE).removeHandler();
      if (event.getException() == null)
      {
         reDeployFile();
      }
   }

   protected void reDeployFile()
   {
      handlerRegistrations.put(GroovyDeployResultReceivedEvent.TYPE, eventBus.addHandler(GroovyDeployResultReceivedEvent.TYPE, this));
      eventBus.fireEvent(new DeployGroovyScriptSandboxEvent());
   }

   public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
   {
      handlerRegistrations.get(GroovyDeployResultReceivedEvent.TYPE).removeHandler();
      if (event.getException() == null)
      {
         handlerRegistrations.put(RestServiceOutputReceivedEvent.TYPE, eventBus.addHandler(RestServiceOutputReceivedEvent.TYPE, this));
         eventBus.fireEvent(new PreviewWadlOutputEvent(true));
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.groovy.event.RestServiceOutputReceivedHandler#onRestServiceOutputReceived(org.exoplatform.ide.client.module.groovy.service.groovy.event.RestServiceOutputReceivedEvent)
    */
   public void onRestServiceOutputReceived(RestServiceOutputReceivedEvent event)
   {
      removeHandlers();
   
      eventBus.fireEvent(new UndeployGroovyScriptSandboxEvent());
   }

   public void onError(ExceptionThrownEvent event)
   {
      removeHandlers();
   }
   
   /**
    * Remove handlers, that are no longer needed.
    */
   private void removeHandlers()
   {
      //TODO: such method is not very convenient.
      //If gwt mvp framework will be used , it will be good to use
      //ResettableEventBus class
      for (HandlerRegistration h : handlerRegistrations.values())
      {
         h.removeHandler();
      }
      handlerRegistrations.clear();
   }

}
