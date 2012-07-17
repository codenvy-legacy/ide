/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.editor.client;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class TestEditor implements EntryPoint
{

   private static List<Editor> editors = new ArrayList<Editor>();
   
   private Editor attachedEditor;
   
   private boolean lineNumbers = false;

   private void fillEditors()
   {
      editors.add(new CodeMirror("text/xml", "XML file editor", "xml", null));
      editors.add(new CodeMirror("text/html", "Editor of HTML pages", "html", null));
      editors.add(new CodeMirror("text/plain", "Editor of text files", "txt", null));
   }

   @Override
   public void onModuleLoad()
   {
      fillEditors();

      final AbsolutePanel editorContainer = new AbsolutePanel();
      RootPanel.get().add(editorContainer, 200, 100);
      editorContainer.setWidth("700px");
      editorContainer.setHeight("400px");
      editorContainer.getElement().getStyle().setBackgroundColor("#FFEEAA");

      Button listEditorsButton = new Button("List");
      RootPanel.get().add(listEditorsButton, 100, 100);
      listEditorsButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            System.out.println(">>> editors:");

            for (Editor editor : editors)
            {
               System.out.println("mime-type: " + editor.getMimeType() + "     " + "extension: "
                  + editor.getFileExtension() + "     " + "description: " + editor.getDescription() + "     " + "id: " + editor.getId());
            }

         }
      });

      Button createEditorButton = new Button("Create");
      RootPanel.get().add(createEditorButton, 100, 140);
      createEditorButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            String mimeType = "text/html";

            for (Editor editor : editors)
            {
               if (editor.getMimeType().equals(mimeType))
               {
                  attachedEditor = editor.newInstance();
                  attachedEditor.asWidget().setWidth("100%");
                  attachedEditor.asWidget().setHeight("100%");
                  editorContainer.add(attachedEditor);
                  
                  Scheduler.get().scheduleDeferred(new ScheduledCommand()
                  {
                     @Override
                     public void execute()
                     {
//                        e.showLineNumbers(false);
//                        e.showLineNumbers(true);
                     }
                  });
                  
               }
            }

         }
      });
      
      Button showHideLineNumbers = new Button("Line Numbers");
      RootPanel.get().add(showHideLineNumbers, 100, 180);
      showHideLineNumbers.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            if (attachedEditor != null) {
               lineNumbers = !lineNumbers;
               attachedEditor.showLineNumbers(lineNumbers);
            }
         }
      });

   }

}
