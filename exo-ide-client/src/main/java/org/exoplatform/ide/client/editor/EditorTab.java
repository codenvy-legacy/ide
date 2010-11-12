/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.editor;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.event.EditorCursorActivityEvent;
import org.exoplatform.gwtframework.editor.event.EditorCursorActivityHandler;
import org.exoplatform.gwtframework.editor.event.EditorFocusReceivedEvent;
import org.exoplatform.gwtframework.editor.event.EditorFocusReceivedHandler;
import org.exoplatform.gwtframework.ui.client.smartgwteditor.SmartGWTTextEditor;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.ViewHighlightManager;
import org.exoplatform.ide.client.framework.vfs.File;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorTab extends Tab implements EditorFocusReceivedHandler, EditorCursorActivityHandler
{

   /**
    * 
    */
   private View viewPane;

   /**
    * Smart GWT Text Editor
    */
   private SmartGWTTextEditor textEditor;

   /**
    * File
    */
   private File file;

   /**
    * Layout for storing SmartWGT editor 
    */
   private VLayout editorLayout;

   /**
    * Editor read only status
    */
   private boolean readOnly = false;

   /**
    * ID uses for identification View, contains 
    */
   private static int ID;

   /**
    * Handlers for storing working event handlers 
    */
   private Handlers handlers;

   /**
    * @param file - File to be edited
    * @param eventBus - Event Bus
    * @param textEditor SmartGWT Text Editor
    */
   public EditorTab(File file, HandlerManager eventBus, SmartGWTTextEditor textEditor)
   {
      this.file = file;

      handlers = new Handlers(eventBus);
      handlers.addHandler(EditorFocusReceivedEvent.TYPE, this);
      handlers.addHandler(EditorCursorActivityEvent.TYPE, this);

      setTitle(getTabTitle());
      viewPane = new EditorView("ideEditorTab-" + ID++, eventBus);

      setPane(viewPane);

      editorLayout = new VLayout();
      this.textEditor = textEditor;
      editorLayout.addMember(textEditor);
      viewPane.addMember(editorLayout);
   }

   /**
    *  Set read only status for Tab title.
    */
   public void setTitleStatusReadOnly(boolean readOnly)
   {
      this.readOnly = readOnly;
      setTitle(getTabTitle());
   }

   /**
    * @return SmartGWT Text Editor
    */
   public SmartGWTTextEditor getTextEditor()
   {
      return textEditor;
   }

   /**
    * Set text editor. If text editor already present, it will be replaced by new text editor.
    * 
    * @param textEditor
    */
   public void setTextEditor(final SmartGWTTextEditor textEditor)
   {
      if (this.textEditor != null)
      {
         editorLayout.removeMember(this.textEditor);
      }

      this.textEditor = textEditor;

      editorLayout.addMember(textEditor);
   }

   /**
    * @return File
    */
   public File getFile()
   {
      return file;
   }

   /**
    * Set new file
    * 
    * @param file
    */
   public void setFile(File file)
   {
      this.file = file;
   }

   /**
    * @return Title for tab is depends of file is new, file's MimeType and content changing state.
    */
   public String getTabTitle()
   {
      boolean fileChanged = file.isContentChanged() || file.isPropertiesChanged();

      String fileName = Utils.unescape(fileChanged ? file.getName() + "&nbsp;*" : file.getName());

      String mainHint = file.getHref();

      String readonlyImage =
         (readOnly)
            ? "<img id=\"fileReadonly\"  style=\"margin-left:-4px; margin-bottom: -4px;\" border=\"0\" suppress=\"true\" src=\""
               + Images.Editor.READONLY_FILE + "\" />" : "";

      mainHint = (readOnly) ? "File opened in read only mode. Use SaveAs command." : mainHint;
      String title =
         "<span title=\"" + mainHint + "\">" + Canvas.imgHTML(file.getIcon()) + readonlyImage + "&nbsp;" + fileName
            + "</span>";

      return title;
   }

   /**
    * Highlight File Tab after Editor had received the focus
    * @see org.exoplatform.gwtframework.editor.event.EditorFocusRecievedHandler#onEditorFocusRecieved(org.exoplatform.gwtframework.editor.event.EditorFocusRecievedEvent)
    */
   public void onEditorFocusReceived(EditorFocusReceivedEvent event)
   {
      if (textEditor.getEditorId().equals(event.getEditorId()))
      {
         ViewHighlightManager.getInstance().selectView(viewPane);
      }
   }

   public void onEditorCursorActivity(EditorCursorActivityEvent event)
   {
      if (textEditor.getEditorId().equals(event.getEditorId()))
      {
         ViewHighlightManager.getInstance().selectView(viewPane);
      }
   }

   protected class EditorView extends View
   {

      public EditorView(String id, HandlerManager eventBus)
      {
         super(id, eventBus);
      }

      @Override
      public void destroy()
      {
         System.out.println("EditorTab.EditorView.destroy()");
         handlers.removeHandlers();
         super.destroy();
      }

   }

}
