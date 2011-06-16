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

import com.google.gwt.http.client.URL;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.extension.groovy.client.event.UndeployGroovyScriptEvent;
import org.exoplatform.ide.extension.groovy.client.event.UndeployGroovyScriptHandler;
import org.exoplatform.ide.extension.groovy.client.event.UndeployGroovyScriptSandboxEvent;
import org.exoplatform.ide.extension.groovy.client.event.UndeployGroovyScriptSandboxHandler;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyService;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.GroovyUndeployResultReceivedEvent;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.GroovyUndeployResultReceivedHandler;

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
      GroovyService.getInstance().undeploy(activeFile.getHref(), new AsyncRequestCallback<String>()
      {
         
         @Override
         protected void onSuccess(String result)
         {
            undeploySuccess(result);
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            undeployFail(exception, this.getResult());
         }
      });
   }
   
   private void undeploySuccess(String href)
   {
      
      String outputContent = "<b>" + URL.decodePathSegment(href) + "</b> undeployed successfully.";
      eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
//      eventBus.fireEvent(new GroovyUndeployResultReceivedEvent(href));
   }
   
   private void undeployFail(Throwable exc, String href)
   {
      if (exc instanceof ServerException)
      {
         ServerException exception = (ServerException)exc;

         String outputContent = "<b>" + URL.decodePathSegment(href) + "</b> undeploy failed.&nbsp;";
         outputContent += "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent(exc));
      }
      GroovyUndeployResultReceivedEvent event = new GroovyUndeployResultReceivedEvent(href);
      event.setException(exc);
      eventBus.fireEvent(event);
   }

   /**
    * {@inheritDoc}
    */
   public void onUndeployGroovyScriptSandbox(UndeployGroovyScriptSandboxEvent event)
   {
      GroovyService.getInstance().undeploySandbox(activeFile.getHref(), new AsyncRequestCallback<String>()
      {
         
         @Override
         protected void onSuccess(String result)
         {
            undeploySuccess(result);
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            undeployFail(exception, this.getResult());
         }
      });
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
         String outputContent = "<b>" + URL.decodePathSegment(event.getPath()) + "</b> undeployed successfully.";
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
      }
      else
      {
         /*
          * Undeploy failed
          */
         ServerException exception = (ServerException)event.getException();

         String outputContent = "<b>" + URL.decodePathSegment(event.getPath()) + "</b> undeploy failed.&nbsp;";
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
