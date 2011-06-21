/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.editor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.editor.api.Editor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorView Mar 21, 2011 4:33:38 PM evgen $
 *
 */
public class EditorView extends ViewImpl
{

   private static int i = 0;
   
   private static final String EDITOR_SWITCHER_BACKGROUND = Images.Editor.EDITOR_SWITCHER_BACKGROUND;
   
   private Map<EditorType, Editor> editors = new HashMap<EditorType, Editor>();
   
   private Map<EditorType, ToggleButton> buttons = new HashMap<EditorType, ToggleButton>();
   
   private EditorType currentEditorType = EditorType.DEFAULT;

   List<Editor> supportedEditors;
   
   File file;
   
   HandlerManager eventBus;

   VerticalPanel editorArea;
   
   int lastEditorHeight = 0;
   
   private static final String FILE_IS_READ_ONLY = org.exoplatform.ide.client.IDE.EDITOR_CONSTANT.editorControllerFileIsReadOnly();
   
   private final int BUTTON_WIDTH = 55;

   private final int BUTTON_HEIGHT = 22;
   
   private final int EDITOR_SWITCHER_OFFSET = Math.round(BUTTON_HEIGHT / 4);
   
   private final String BUTTON_LABEL_FONT_SIZE = "11px";
   
   /**
    * @param title
    * @param supportedEditors
    */
   public EditorView(File file, boolean isFileReadOnly, HandlerManager eventBus, List<Editor> supportedEditors, int currentEditorIndex)
   {
      super("editor-" + i++, "editor", getFileTitle(file, isFileReadOnly), new Image(file.getIcon()));
      
      this.file = file;
      this.eventBus = eventBus;
      this.supportedEditors = supportedEditors;
      
      if (this.supportedEditors == null)
         return;
      
      AbsolutePanel editorSwitcherContainer = new AbsolutePanel();
      DOM.setStyleAttribute(editorSwitcherContainer.getElement(), "background", "#FFFFFF url(" + EDITOR_SWITCHER_BACKGROUND + ") repeat-x");
      
      HorizontalPanel editorSwitcher = new HorizontalPanel();

      this.editorArea = new VerticalPanel();

      // to respect of button position
      EditorType[] editorSequence = new EditorType[this.supportedEditors.size()]; 
      
      for (Editor editor : this.supportedEditors)
      {
         EditorType editorType = EditorType.getType(editor.getClass().getName());
         editors.put(editorType, editor);
      
         editorSequence[editorType.getPosition() - 1] = editorType; 
         
         // add editor switcher only if there are several supported editors
         if (this.supportedEditors.size() > 1)
         {                     
            ToggleButton button = createButton(editorType.getLabel(), editorType.getIcon(), editorType.getLabel() + "ButtonID");
            buttons.put(editorType, button);
            
            if (editor == this.supportedEditors.get(currentEditorIndex))
            {
               currentEditorType = editorType;
               showEditor(editor);
               downButton(button);
            }
            else
            {
               hideEditor(editor);               
            }
            
            editor.setHeight("100%");
            editorArea.add(editor);            
         }
         else
         {
            add(this.supportedEditors.get(currentEditorIndex));
            currentEditorType = editorType;
            return;
         }
      }

      // to respect of button sequence
      for (int i = 0; i < editorSequence.length; i++)
      {
         editorSwitcher.add(buttons.get(editorSequence[i])); 
      }
      
      editorSwitcherContainer.add(editorSwitcher);
      editorSwitcherContainer.setHeight("" + BUTTON_HEIGHT);
               
      editorArea.add(editorSwitcherContainer);
      editorArea.setCellHeight(editorSwitcherContainer, "" + BUTTON_HEIGHT);
      
      add(editorArea); 
   }

   private void showEditor(Editor editor)
   {
      DOM.setStyleAttribute(editor.getElement(), "display", "block");
      editor.setFocus();
   }

   private void hideEditor(Editor editor)
   {
      DOM.setStyleAttribute(editor.getElement(), "display", "none");
   }
   
   /**
    * @return the editor
    */
   public Editor getEditor()
   {
      return editors.get(currentEditorType);
   }
   
   /**
    * @return the editor on editorId
    */
   public Editor getEditor(String editorId)
   {
      Collection<Editor> editorList = editors.values();
      
      for (Editor editor : editorList)
      {
         if (editor.getEditorId().equals(editorId))
         {
            return editor;
         }
      }
      
      return null;
   }
   
   public void setContent(String content)
   {
      editors.get(currentEditorType).setText(content);
   }
   
   /**
    * Create button with label and icon
    * @param label
    * @param iconUrl
    * @param id
    * @return {@link ToggleButton}
    */
   private ToggleButton createButton(String label, String iconUrl, String id)
   {
      ToggleButton button = new ToggleButton(new Image(iconUrl));

      // set button's image + label
      String buttonFace = "<div id='" + id + "' title='" + label + "' style='width: 100%; height: 100%; text-align: center;'><img src='" + iconUrl + "' style='margin-right: 3px; margin-top: -2px;'/><span style='vertical-align: top; font-size: " + BUTTON_LABEL_FONT_SIZE + "'>" + label + "</span></div>";
      button.setHTML(buttonFace);    
      button.setWidth("" + BUTTON_WIDTH);
      button.setHeight("" + BUTTON_HEIGHT);
      
      button.addClickHandler(buttonClickHandler);
      
      return button;
   }

   ClickHandler buttonClickHandler = new ClickHandler() {

      public void onClick(ClickEvent event)
      {
         EditorType nextEditorType = getFirstKeyByValue(buttons, (ToggleButton) event.getSource());
         if (nextEditorType == null)
         {
            return;
         }

         if (nextEditorType == currentEditorType)
         {
            downButton(buttons.get(currentEditorType));
            return;
         }
         
         switchToEditor(nextEditorType);
      }
      
   };
   
   private void upButton(ToggleButton button)
   {
      button.setValue(false);
   }
   
   
   protected void downButton(ToggleButton button)
   {
      button.setValue(true);      
   }
   
   /**
    * Return first editor type key for button value
    * @param buttons
    * @param button
    * @return
    */
   public static EditorType getFirstKeyByValue(Map<EditorType, ToggleButton> buttons, ToggleButton button) {
      for (Entry<EditorType, ToggleButton> entry : buttons.entrySet()) {
          if (entry.getValue().equals(button)) {
              return entry.getKey();
          }
      }
      return null;
   }

   private static String getFileTitle(File file, boolean isReadOnly)
   {
      boolean fileChanged = file.isContentChanged() || file.isPropertiesChanged();

      String fileName = Utils.unescape(fileChanged ? file.getName() + "&nbsp;*" : file.getName());

      String mainHint = file.getHref();

      String readonlyImage =
         (isReadOnly)
            ? "<img id=\"fileReadonly\"  style=\"margin-left:-4px; margin-bottom: -4px;\" border=\"0\" suppress=\"true\" src=\""
               + Images.Editor.READONLY_FILE + "\" />" : "";

      mainHint = (isReadOnly) ? FILE_IS_READ_ONLY : mainHint;
      String title = "<span title=\"" + mainHint + "\">" + readonlyImage + "&nbsp;" + fileName + "</span>";

      return title;
   }
 
   public void onViewInnerEditorSwitched(Editor editor)
   {
      this.eventBus.fireEvent(new EditorActiveFileChangedEvent(this.file, editor));
   }
   
   public File getFile()
   {
      return this.file;
   }

   public void setTitle(File file, boolean isFileReadOnly)
   {
      super.setTitle(getFileTitle(file, isFileReadOnly));
   }
   
   private int getEditorAreaHeight()
   {
      return editorArea.getParent().getParent().getOffsetHeight();
   }

   /**
    * Switch to editor on its index within the supported editors list of opened file
    * @param indexOfEditorToShow
    */
   public void switchToEditor(int indexOfEditorToShow)
   {      
      EditorType nextEditorType = EditorType.getType(this.supportedEditors.get(indexOfEditorToShow).getClass().getName());
      switchToEditor(nextEditorType);
   }

   /**
    * Switch to editor on its type
    * @param nextEditorType
    */
   public void switchToEditor(EditorType nextEditorType)
   {      
      Editor currentEditor = editors.get(currentEditorType);
      Editor nextEditor = editors.get(nextEditorType);
    
      // actualize the text of next
      if (!currentEditor.getText().equals(nextEditor.getText()))
      {
         nextEditor.setText(currentEditor.getText());
      }
      
      // show next editor
      hideEditor(editors.get(currentEditorType));
      showEditor(editors.get(nextEditorType));
      
      // up current editor button
      upButton(buttons.get(currentEditorType));
      
      // down next editor button (in case of calling this method from editorController)
      downButton(buttons.get(nextEditorType));
      
      currentEditorType = nextEditorType;
      
      onViewInnerEditorSwitched(nextEditor);
   }
   
   @Override
   public void resize(int width, int height)
   {
      super.resize(width, height);

      // restore CKEditor height on resize
      if (currentEditorType == EditorType.DESIGN
               && getEditorAreaHeight() != lastEditorHeight)
      {      
         if (this.supportedEditors.size() == 1)
         {
            lastEditorHeight = editorArea.getOffsetHeight();       
            getEditor().setHeight("" + lastEditorHeight);
         }
         else
         {
            lastEditorHeight = getEditorAreaHeight();
            getEditor().setHeight("" + (lastEditorHeight - BUTTON_HEIGHT - EDITOR_SWITCHER_OFFSET));
         }
      }      
   }

   public void setFile(File newFile)
   {
      this.file = newFile;
   }
}