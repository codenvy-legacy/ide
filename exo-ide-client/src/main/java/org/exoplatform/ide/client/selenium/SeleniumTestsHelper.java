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
package org.exoplatform.ide.client.selenium;

import java.util.List;

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class sets hidden values to DOM which allows to do faster time passing the test.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */


public class SeleniumTestsHelper implements EditorActiveFileChangedHandler, EditorOpenFileHandler, ItemsSelectedHandler
{

   /**
    * Panel where stores the widget with debug information.
    */
   private AbsolutePanel debugPanel;

   /**
    * Widget for storing the URL of active file in editor.
    */
   private Widget editorActiveFile;

   /**
    * Widget for storing the URL of previous active file.
    */
   private Widget editorPreviousActiveFile;

   /**
    * Widget for storing the URL of selected file in Workspace or Search Result views. 
    */
   private Widget selectedFile;

   public SeleniumTestsHelper()
   {
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(EditorOpenFileEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);

      debugPanel = new AbsolutePanel();
      debugPanel.getElement().setId("debug-panel");
      debugPanel.setSize("300px", "150px");
      debugPanel.setVisible(false);
      debugPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
      RootPanel.get().add(debugPanel, -5000, -5000);

      editorActiveFile = createDebugEntry("debug-editor-active-file-url");
      editorActiveFile.addDomHandler(editorActiveFileClickHandler, ClickEvent.getType());
      editorPreviousActiveFile = createDebugEntry("debug-editor-previous-active-file-url");
      selectedFile = createDebugEntry("debug-navigation-selected-file");
   }

   private ClickHandler editorActiveFileClickHandler = new ClickHandler()
   {
      @Override
      public void onClick(ClickEvent event)
      {
         editorActiveFile.getElement().setInnerText("");;
      }
   };

   private Widget createDebugEntry(String id)
   {
      FlowPanel panel = new FlowPanel();
      panel.getElement().setId(id);
      panel.getElement().setInnerText("");
      debugPanel.add(panel);
      return panel;
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      String previousActiveFile = editorActiveFile.getElement().getInnerText();
      editorPreviousActiveFile.getElement().setInnerText(previousActiveFile);

      if (event.getFile() == null)
      {
         editorActiveFile.getElement().setInnerText("");
      }
      else
      {
         editorActiveFile.getElement().setInnerText(event.getFile().getPath());
      }
   }

   @Override
   public void onEditorOpenFile(EditorOpenFileEvent event)
   {
      editorActiveFile.getElement().setInnerText("");
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      List<Item> selectedItems = event.getSelectedItems();
      if (selectedItems.size() == 0)
      {
         selectedFile.getElement().setInnerText("");
      }
      else
      {
         selectedFile.getElement().setInnerText(selectedItems.get(0).getPath());
      }
   }

}
