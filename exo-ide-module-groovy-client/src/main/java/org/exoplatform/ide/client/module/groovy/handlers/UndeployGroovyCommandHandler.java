/**
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
 *
 */

package org.exoplatform.ide.client.module.groovy.handlers;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.groovy.event.UndeployGroovyScriptEvent;
import org.exoplatform.ide.client.module.groovy.event.UndeployGroovyScriptHandler;
import org.exoplatform.ide.client.module.groovy.event.UndeployGroovyScriptSandboxEvent;
import org.exoplatform.ide.client.module.groovy.event.UndeployGroovyScriptSandboxHandler;
import org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyUndeployResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyUndeployResultReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UndeployGroovyCommandHandler implements EditorActiveFileChangedHandler, UndeployGroovyScriptHandler, UndeployGroovyScriptSandboxHandler, GroovyUndeployResultReceivedHandler
{

   private HandlerManager eventBus;

   private File activeFile;

   public UndeployGroovyCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(UndeployGroovyScriptEvent.TYPE, this);
      eventBus.addHandler(UndeployGroovyScriptSandboxEvent.TYPE, this);
      eventBus.addHandler(GroovyUndeployResultReceivedEvent.TYPE, this);      
   }

   /**
    * @see org.exoplatform.ide.plugin.groovy.event.UndeployGroovyScriptHandler#onUndeployGroovyScript(org.exoplatform.ide.plugin.groovy.event.UndeployGroovyScriptEvent)
    */
   public void onUndeployGroovyScript(UndeployGroovyScriptEvent event)
   {
      GroovyService.getInstance().undeploy(activeFile.getHref());
   }

   /**
    * {@inheritDoc}
    */
   public void onUndeployGroovyScriptSandbox(UndeployGroovyScriptSandboxEvent event)
   {
      GroovyService.getInstance().undeploySandbox(activeFile.getHref());
   }

   /**
    * @see org.exoplatform.ide.groovy.event.GroovyUndeployResultReceivedHandler#onGroovyUndeployResultReceived(org.exoplatform.ide.groovy.event.GroovyUndeployResultReceivedEvent)
    */
   public void onGroovyUndeployResultReceived(GroovyUndeployResultReceivedEvent event)
   {
      if (event.getException() == null)
      {
         /*
          * Undeploy successfully
          */
         String outputContent = "<b>" + event.getPath() + "</b> undeployed successfully.";
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
      }
      else
      {
         /*
          * Undeploy failed
          */
         ServerException exception = (ServerException)event.getException();

         String outputContent = "<b>" + event.getPath() + "</b> undeploy failed.&nbsp;";
         outputContent += "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

}
