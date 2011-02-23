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
package org.exoplatform.ide.extension.chromattic.client.handler;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.extension.chromattic.client.event.CompileGroovyEvent;
import org.exoplatform.ide.extension.chromattic.client.event.CompileGroovyHandler;
import org.exoplatform.ide.extension.chromattic.client.model.service.ChrommaticService;

/**
 * 
 * Handler for compile groovy command.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CompileGroovyCommandHandler implements CompileGroovyHandler
{

   /**
    * Event Bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus Event Bus
    */
   public CompileGroovyCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(CompileGroovyEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.event.CompileGroovyHandler#onCompileGroovy(org.exoplatform.ide.client.module.chromattic.event.CompileGroovyEvent)
    */
   public void onCompileGroovy(CompileGroovyEvent event)
   {
      File file = event.getFile();
      ChrommaticService.getInstance().compile(file, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            String outputContent = "<b>" + result + "</b> compiled successfully.";
            eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exception));

            ServerException serverException = (ServerException)exception;

            String outputContent = "<b>" + this.getResult() + "</b> deploy failed.&nbsp;";
            outputContent +=
               "Error (<i>" + serverException.getHTTPStatus() + "</i>: <i>" + serverException.getStatusText() + "</i>)";
            if (!serverException.getMessage().equals(""))
            {
               outputContent += "<br />" + serverException.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
            }

            eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
         }
      });
   }

}
