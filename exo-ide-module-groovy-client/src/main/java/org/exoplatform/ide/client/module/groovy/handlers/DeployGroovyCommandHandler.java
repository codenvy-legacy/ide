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
import org.exoplatform.ide.client.module.groovy.event.DeployGroovyScriptEvent;
import org.exoplatform.ide.client.module.groovy.event.DeployGroovyScriptHandler;
import org.exoplatform.ide.client.module.groovy.event.DeployGroovyScriptSandboxEvent;
import org.exoplatform.ide.client.module.groovy.event.DeployGroovyScriptSandboxHandler;
import org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DeployGroovyCommandHandler implements DeployGroovyScriptHandler, DeployGroovyScriptSandboxHandler,
   EditorActiveFileChangedHandler, GroovyDeployResultReceivedHandler
{

   private HandlerManager eventBus;

   private File activeFile;

   public DeployGroovyCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(DeployGroovyScriptEvent.TYPE, this);
      eventBus.addHandler(DeployGroovyScriptSandboxEvent.TYPE, this);
      eventBus.addHandler(GroovyDeployResultReceivedEvent.TYPE, this);
   }

   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.plugin.groovy.event.DeployGroovyScriptHandler#onDeployGroovyScript(org.exoplatform.ide.plugin.groovy.event.DeployGroovyScriptEvent)
    */
   public void onDeployGroovyScript(DeployGroovyScriptEvent event)
   {
      GroovyService.getInstance().deploy(activeFile.getHref());
   }

   /**
    * {@inheritDoc}
    */
   public void onDeployGroovyScriptSandbox(DeployGroovyScriptSandboxEvent event)
   {
      GroovyService.getInstance().deploySandbox(activeFile.getHref());
   }

   /**
    * @see org.exoplatform.ide.groovy.event.GroovyDeployResultReceivedHandler#onGroovyDeployResultReceived(org.exoplatform.ide.groovy.event.GroovyDeployResultReceivedEvent)
    */
   public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
   {
      if (event.getException() == null)
      {
         /*
          * Deploying successfully
          */
         String outputContent = "<b>" + event.getPath() + "</b> deployed successfully.";
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
      }
      else
      {
         /*
          * Deploying failed
          */
         ServerException exception = (ServerException)event.getException();

         String outputContent = "<b>" + event.getPath() + "</b> deploy failed.&nbsp;";
         outputContent += "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }

         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }
   }

}
