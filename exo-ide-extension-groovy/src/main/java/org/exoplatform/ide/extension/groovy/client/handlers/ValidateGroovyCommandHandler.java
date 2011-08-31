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

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedHandler;
import org.exoplatform.ide.extension.groovy.client.event.ValidateGroovyScriptEvent;
import org.exoplatform.ide.extension.groovy.client.event.ValidateGroovyScriptHandler;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyService;
import org.exoplatform.ide.extension.groovy.client.service.groovy.event.GroovyValidateResultReceivedEvent;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ValidateGroovyCommandHandler implements ValidateGroovyScriptHandler, EditorActiveFileChangedHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, ExceptionThrownHandler, EditorInitializedHandler
{

   private HandlerManager eventBus;

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   private FileModel activeFile;

   /**
    * Is need to go to position in active file.
    */
   private boolean isGoToPosition;
   
   private boolean goToPositionAfterOpen;

   /**
    * Number of line, where to after,
    * after user click on error message.
    */
   private int lineNumberToGo;

   /**
    * Number of column, where to after,
    * after user click on error message.
    */
   private int columnNumberToGo;

   /**
    * Href of file which contains an exception and in which need to go to position.
    */
   private String errFileHref = "";

   /**
    * Number of line, which extracts from error message and
    * paths as parameter to javascript method.
    */
   private int errLineNumber;

   /**
    * Number of column, which extracts from error message and
    * paths as parameter to javascript method.
    */
   private int errColumnNumber;

   public ValidateGroovyCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ValidateGroovyScriptEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(ExceptionThrownEvent.TYPE, this);
      eventBus.addHandler(EditorInitializedEvent.TYPE, this);

      initGoToErrorFunction();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      if (isGoToPosition)
      {
         isGoToPosition = false;
         new Timer()
         {
            @Override
            public void run()
            {
               eventBus.fireEvent(new EditorGoToLineEvent(lineNumberToGo, columnNumberToGo));
            }

         }.schedule(200);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * {@inheritDoc}
    */
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onValidateGroovyScript(ValidateGroovyScriptEvent event)
   {
      GroovyService.getInstance().validate(activeFile, new AsyncRequestCallback<FileModel>()
      {
         
         @Override
         protected void onSuccess(FileModel result)
         {
            String outputContent = "<b>" + result.getName() + "</b> validated successfully.";
            eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.INFO));
            eventBus.fireEvent(new GroovyValidateResultReceivedEvent(result.getName(), result.getId()));
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            if (exception instanceof ServerException)
            {
               ServerException serverException = (ServerException)exception;

               String outputContent = "<b>" + this.getResult().getName() + "</b> validation failed.&nbsp;";
               outputContent +=
                  "Error (<i>" + serverException.getHTTPStatus() + "</i>: <i>" + serverException.getStatusText()
                     + "</i>)";

               if (!serverException.getMessage().equals(""))
               {
                  outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
               }

               findLineNumberAndColNumberOfError(exception.getMessage());

               outputContent =
                  "<span title=\"Go to error\" onClick=\"window.groovyGoToErrorFunction("
                     + String.valueOf(errLineNumber) + "," + String.valueOf(errColumnNumber) + ", '"
                     + this.getResult().getPath() + "', '" + "');\" style=\"cursor:pointer;\">" + outputContent + "</span>";

               eventBus.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
            }
            else
            {
               eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
            GroovyValidateResultReceivedEvent event =
               new GroovyValidateResultReceivedEvent(this.getResult().getName(), this.getResult().getId());
            event.setException(exception);
            eventBus.fireEvent(event);
         }
      });
   }

   private native void initGoToErrorFunction() /*-{
     var instance = this;       
     var goToErrorFunction = function(lineNumber, columnNumber, fileHref, contentType) {
     instance.@org.exoplatform.ide.extension.groovy.client.handlers.ValidateGroovyCommandHandler::goToError(Ljava/lang/String;II)(
     fileHref, lineNumber, columnNumber);
     };
     
     $wnd.groovyGoToErrorFunction = goToErrorFunction;
     }-*/;

   public void goToError(String fileHref, int lineNumber, int columnNumber)
   {
      if (activeFile != null && fileHref.equals(activeFile.getId()))
      {
         eventBus.fireEvent(new EditorGoToLineEvent(lineNumber, columnNumber));
         return;
      }

      lineNumberToGo = lineNumber;
      columnNumberToGo = columnNumber;

      //TODO:
      //When FileOpenedEvent will be use,
      //remove this additional variable, 
      //and listen to that event
      if (openedFiles != null && openedFiles.containsKey(fileHref))
      {
         isGoToPosition = true;
         eventBus.fireEvent(new OpenFileEvent(openedFiles.get(fileHref)));
      }
      else
      {
         errFileHref = fileHref;
         goToPositionAfterOpen = true;
         eventBus.fireEvent(new OpenFileEvent(fileHref));
      }
   }

   /**
    * Parse text and find number of column and line number of error
    * 
    * @param text validation text, which contains number of column and line number of error
    */
   private void findLineNumberAndColNumberOfError(String text)
   {
      try
      {
         //find line number
         int firstIndex = text.indexOf("@ line") + 7;
         int lastIndex = text.indexOf(", column");
         errLineNumber = Integer.valueOf(text.substring(firstIndex, lastIndex));

         //find column number
         firstIndex = lastIndex + 9;
         lastIndex = text.indexOf(".", firstIndex);
         errColumnNumber = Integer.valueOf(text.substring(firstIndex, lastIndex));

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * {@inheritDoc}
    */
   public void onError(ExceptionThrownEvent event)
   {
      errFileHref = "";
      goToPositionAfterOpen = false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.event.EditorInitializedHandler#onEditorInitialized(org.exoplatform.ide.editor.api.event.EditorInitializedEvent)
    */
   @Override
   public void onEditorInitialized(EditorInitializedEvent event)
   {
      if (goToPositionAfterOpen)
      {
         goToPositionAfterOpen = false;
         eventBus.fireEvent(new EditorGoToLineEvent(lineNumberToGo, columnNumberToGo));
      }
   }

}
