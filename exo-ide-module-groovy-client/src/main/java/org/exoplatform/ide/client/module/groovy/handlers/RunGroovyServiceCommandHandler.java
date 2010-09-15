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
package org.exoplatform.ide.client.module.groovy.handlers;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.event.SaveFileEvent;
import org.exoplatform.ide.client.module.groovy.event.DeployGroovyScriptEvent;
import org.exoplatform.ide.client.module.groovy.event.PreviewWadlOutputEvent;
import org.exoplatform.ide.client.module.groovy.event.RunGroovyServiceEvent;
import org.exoplatform.ide.client.module.groovy.event.RunGroovyServiceHandler;
import org.exoplatform.ide.client.module.groovy.event.ValidateGroovyScriptEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedHandler;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyValidateResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyValidateResultReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.File;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RunGroovyServiceCommandHandler implements RegisterEventHandlersHandler, RunGroovyServiceHandler,
   EditorActiveFileChangedHandler, FileSavedHandler, ExceptionThrownHandler, GroovyDeployResultReceivedHandler,
   GroovyValidateResultReceivedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   public RunGroovyServiceCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(RunGroovyServiceEvent.TYPE, this);
      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersHandler#onRegisterEventHandlers(org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersEvent)
    */
   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
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

      if (activeFile.isNewFile())
      {
         Dialogs.getInstance().ask("IDE",
            "File <b>" + activeFile.getName() + "</b> is not saved. Would you like to save it?",
            new BooleanValueReceivedCallback()
            {
               public void execute(Boolean value)
               {
                  if (value == null || value == false)
                  {
                     return;
                  }

                  saveFileAs();
               }
            });

         return;
      }

      if (activeFile.isContentChanged())
      {
         saveFile();
         return;
      }

      validateFile();

   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();
   }

   protected void saveFileAs()
   {
      handlers.addHandler(FileSavedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      eventBus.fireEvent(new SaveFileAsEvent());
   }

   protected void saveFile()
   {
      handlers.addHandler(FileSavedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      eventBus.fireEvent(new SaveFileEvent());
   }

   protected void validateFile()
   {
      handlers.addHandler(GroovyValidateResultReceivedEvent.TYPE, this);
      eventBus.fireEvent(new ValidateGroovyScriptEvent());
   }
   
   protected void reDeployFile()
   {
      handlers.addHandler(GroovyDeployResultReceivedEvent.TYPE, this);
      eventBus.fireEvent(new DeployGroovyScriptEvent());
   }
   
   protected void launchWadl()
   {
      eventBus.fireEvent(new PreviewWadlOutputEvent());
   }

   @Override
   public void onFileSaved(FileSavedEvent event)
   {
      handlers.removeHandler(FileSavedEvent.TYPE);
      validateFile();
   }

   @Override
   public void onGroovyValidateResultReceived(GroovyValidateResultReceivedEvent event)
   {
      handlers.removeHandler(GroovyValidateResultReceivedEvent.TYPE);
      if (event.getException() == null)
      {
         reDeployFile();
      }
   }
   
   @Override
   public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
   {
      handlers.removeHandler(GroovyDeployResultReceivedEvent.TYPE);
      if (event.getException() == null)
      {
         launchWadl();
      }
   }
   
   @Override
   public void onError(ExceptionThrownEvent event)
   {
      //Can occurs while saving file.
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
      //Before saving file, we add FileSavedHandler,
      //remove it
      handlers.removeHandler(FileSavedEvent.TYPE);
   }

}
