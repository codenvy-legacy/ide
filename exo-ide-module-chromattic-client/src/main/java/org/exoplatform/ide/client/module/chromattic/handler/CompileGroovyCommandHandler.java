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

package org.exoplatform.ide.client.module.chromattic.handler;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.chromattic.event.CompileGroovyEvent;
import org.exoplatform.ide.client.module.chromattic.event.CompileGroovyHandler;
import org.exoplatform.ide.client.module.chromattic.model.ChrommaticService;
import org.exoplatform.ide.client.module.chromattic.model.event.CompileGroovyResultReceivedEvent;
import org.exoplatform.ide.client.module.chromattic.model.event.CompileGroovyResultReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CompileGroovyCommandHandler implements CompileGroovyHandler, CompileGroovyResultReceivedHandler
{

   private HandlerManager eventBus;

   public CompileGroovyCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(CompileGroovyEvent.TYPE, this);
      eventBus.addHandler(CompileGroovyResultReceivedEvent.TYPE, this);
   }

   @Override
   public void onCompileGroovy(CompileGroovyEvent event)
   {
      File file = event.getFile();
      ChrommaticService.getInstance().compile(file);
   }

   @Override
   public void onCompileGroovyResultReceived(CompileGroovyResultReceivedEvent event)
   {
      if (event.getException() == null)
      {
         /*
          * Deploying successfully
          */
         String outputContent = "<b>" + event.getUrl() + "</b> compiled successfully.";
         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
      }
      else
      {
         /*
          * Deploying failed
          */
         ServerException exception = (ServerException)event.getException();

         String outputContent = "<b>" + event.getUrl() + "</b> deploy failed.&nbsp;";
         outputContent += "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }

         eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }
   }

}
