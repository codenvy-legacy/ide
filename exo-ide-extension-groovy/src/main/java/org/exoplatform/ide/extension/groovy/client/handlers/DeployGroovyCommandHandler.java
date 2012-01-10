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

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.groovy.client.event.DeployGroovyScriptEvent;
import org.exoplatform.ide.extension.groovy.client.event.DeployGroovyScriptHandler;
import org.exoplatform.ide.extension.groovy.client.event.DeployGroovyScriptSandboxEvent;
import org.exoplatform.ide.extension.groovy.client.event.DeployGroovyScriptSandboxHandler;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyService;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import com.google.gwt.http.client.URL;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DeployGroovyCommandHandler implements DeployGroovyScriptHandler, DeployGroovyScriptSandboxHandler,
   EditorActiveFileChangedHandler, ProjectOpenedHandler, ProjectClosedHandler
{

   private FileModel activeFile;

   private ProjectModel currentProject;

   public DeployGroovyCommandHandler()
   {
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(DeployGroovyScriptEvent.TYPE, this);
      IDE.addHandler(DeployGroovyScriptSandboxEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.plugin.groovy.event.DeployGroovyScriptHandler#onDeployGroovyScript(org.exoplatform.ide.plugin.groovy.event.DeployGroovyScriptEvent)
    */
   public void onDeployGroovyScript(DeployGroovyScriptEvent event)
   {
      if (activeFile == null || currentProject == null)
      {
         Dialogs.getInstance().showError("Could not deploy Groovy Service.");
         return;
      }

      GroovyService.getInstance().deploy(activeFile.getId(), VirtualFileSystem.getInstance().getInfo().getId(),
         currentProject.getId(), new AsyncRequestCallback<String>()
         {
            @Override
            protected void onSuccess(String result)
            {
               deploySuccess(activeFile.getPath());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               deployFailure(activeFile.getPath(), exception);
            }
         });
   }

   private void deploySuccess(String href)
   {
      String outputContent = "<b>" + URL.decodePathSegment(href) + "</b> deployed successfully.";
      IDE.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
      IDE.fireEvent(new GroovyDeployResultReceivedEvent(href));
   }

   private void deployFailure(String path, Throwable exception)
   {
      if (exception instanceof ServerException)
      {
         ServerException serverException = (ServerException)exception;

         String outputContent = "<b>" + URL.decodePathSegment(path) + "</b> deploy failed.&nbsp;";
         outputContent +=
            "Error (<i>" + serverException.getHTTPStatus() + "</i>: <i>" + serverException.getStatusText() + "</i>)";
         if (!serverException.getMessage().equals(""))
         {
            outputContent += "<br />" + serverException.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }

         IDE.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent(exception));
      }

      GroovyDeployResultReceivedEvent event = new GroovyDeployResultReceivedEvent(path);
      event.setException(exception);
      IDE.fireEvent(event);
   }

   /**
    * {@inheritDoc}
    */
   public void onDeployGroovyScriptSandbox(DeployGroovyScriptSandboxEvent event)
   {
      if (activeFile == null || currentProject == null)
      {
         Dialogs.getInstance().showError("Could not deploy Groovy Service.");
         return;
      }

      GroovyService.getInstance().deploySandbox(activeFile.getId(), VirtualFileSystem.getInstance().getInfo().getId(),
         currentProject.getId(), new AsyncRequestCallback<String>()
         {
            @Override
            protected void onSuccess(String result)
            {
               deploySuccess(activeFile.getPath());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               deployFailure(activeFile.getPath(), exception);
            }
         });
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      currentProject = null;
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      currentProject = event.getProject();
   }

}
