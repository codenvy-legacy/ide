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
package org.exoplatform.ideall.client.editor;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorConfiguration;
import org.exoplatform.gwtframework.editor.api.GWTTextEditor;
import org.exoplatform.gwtframework.ui.client.smartgwteditor.SmartGWTTextEditor;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileEvent;
import org.exoplatform.ideall.client.event.perspective.EditorPanelRestoredEvent;
import org.exoplatform.ideall.client.event.perspective.EditorPanelRestoredHandler;
import org.exoplatform.ideall.client.event.perspective.MaximizeEditorPanelEvent;
import org.exoplatform.ideall.client.event.perspective.RestoreEditorPanelEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.events.TabCloseClickEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorForm extends Layout implements EditorPresenter.Display, EditorPanelRestoredHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private EditorPresenter presenter;

   private TabSet tabSet;

   private EditorTab activeTab;

   private MinMaxControlButton minMaxControlButton;

   public EditorForm(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;

      handlers = new Handlers(eventBus);

      tabSet = new TabSet();
      createControlButtons();
      addMember(tabSet);

      presenter = new EditorPresenter(eventBus, context);
      presenter.bindDisplay(this);

      /*
       * canceling mouse down events for components are placed below
       */
      addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            event.cancel();
         }
      });

      tabSet.addTabSelectedHandler(tabSelectedHandler);
      tabSet.addCloseClickHandler(closeClickHandler);

      handlers.addHandler(EditorPanelRestoredEvent.TYPE, this);
   }

   @Override
   protected void onDestroy()
   {
      handlers.removeHandlers();
      presenter.destroy();
      super.onDestroy();
   }

   private void createControlButtons()
   {
      Layout controlButtons = new Layout();
      controlButtons.setWidth(20);
      controlButtons.setHeight(18);
      //controlButtons.setBackgroundColor("#FFAAEE");

      minMaxControlButton =
         new MinMaxControlButton(eventBus, true, new MaximizeEditorPanelEvent(), new RestoreEditorPanelEvent());
      controlButtons.addMember(minMaxControlButton);

      tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, controlButtons);
   }

   private TabSelectedHandler tabSelectedHandler = new TabSelectedHandler()
   {
      public void onTabSelected(TabSelectedEvent event)
      {
         try
         {
            activeTab = (EditorTab)event.getTab();
            String path = activeTab.getFile().getPath();
            eventBus.fireEvent(new EditorActiveFileChangedEvent(activeTab.getFile(), hasUndoChanges(path),
               hasRedoChanges(path)));
         }
         catch (Exception exc)
         {
            exc.printStackTrace();
         }
      }
   };

   private CloseClickHandler closeClickHandler = new CloseClickHandler()
   {
      public void onCloseClick(TabCloseClickEvent event)
      {
         event.cancel();
         EditorTab tab = (EditorTab)event.getTab();
         eventBus.fireEvent(new EditorCloseFileEvent(tab.getFile()));         
         
//         EditorTab tab = (EditorTab)event.getTab();
//         eventBus.fireEvent(new EditorCloseFileEvent(tab.getFile()));
//         if (tab.getFile().isContentChanged() || tab.getFile().isPropertiesChanged())
//         {
//            event.cancel();
//         }
//         else
//         {
//            if (tabSet.getTabs().length == 1)
//            {
//               eventBus.fireEvent(new EditorActiveFileChangedEvent(null, false, false));
//            }
//         }
      }
   };

   public void addTab(File file, boolean lineNumbers, Editor editor)
   {
      EditorTab tab = new EditorTab(file);
      tab.setCanClose(true);

      EditorConfiguration configuration = new EditorConfiguration(file.getContentType());
      configuration.setLineNumbers(lineNumbers);

      GWTTextEditor textEditor = editor.createTextEditor(eventBus, configuration);
      SmartGWTTextEditor smartGwtTextEditor = new SmartGWTTextEditor(eventBus, textEditor);

      tab.setTextEditor(smartGwtTextEditor);
      redraw();

      tabSet.addTab(tab);
      redraw();
   }

   public void selectTab(String path)
   {
      EditorTab editorTab = getEditorTab(path);
      if (editorTab == null)
      {
         return;
      }
      activeTab = editorTab;
      tabSet.selectTab(editorTab);
   }

   public void setTabContent(String path, String text)
   {
      try
      {
         getEditorTab(path).getTextEditor().setText(text);
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
      }
   }

   public void closeTab(String path)
   {
      EditorTab tab = getEditorTab(path);

      if (tab == null)
      {
         return;

      }
      tabSet.removeTab(tab);
      if (tabSet.getTabs().length == 0)
      {
         eventBus.fireEvent(new EditorActiveFileChangedEvent(null, false, false));
      }
   }

   private EditorTab getEditorTab(String path)
   {
      for (Tab tab : tabSet.getTabs())
      {
         EditorTab editorTab = (EditorTab)tab;
         if (editorTab.getFile().getPath().equals(path))
         {
            return editorTab;
         }
      }

      return null;
   }

   public String getTabContent(String path)
   {
      EditorTab tab = getEditorTab(path);
      return tab.getTextEditor().getText();
   }

   public String getPathByEditorId(String editorId)
   {
      for (Tab tab : tabSet.getTabs())
      {
         EditorTab editorTab = (EditorTab)tab;
         if (editorTab.getTextEditor().getEditorId().equals(editorId))
         {
            return editorTab.getFile().getPath();
         }
      }

      return null;
   }

   public void updateTabTitle(String path)
   {
      EditorTab tab = getEditorTab(path);
      tabSet.setTabTitle((Tab)tab, tab.getTabTitle());
   }

   public void redoEditing(String path)
   {
      getEditorTab(path).getTextEditor().redo();
   }

   public void undoEditing(String path)
   {
      getEditorTab(path).getTextEditor().undo();
   }

   public void formatFile(String path)
   {
      getEditorTab(path).getTextEditor().formatSource();
   }

   public void relocateFile(File oldFile, File newFile)
   {
      for (Tab tab : tabSet.getTabs())
      {
         EditorTab editorTab = (EditorTab)tab;
         if (editorTab.getFile().equals(oldFile))
         {
            editorTab.setFile(newFile);
            String newFilePath = newFile.getPath();
            eventBus.fireEvent(new EditorActiveFileChangedEvent(newFile, hasUndoChanges(newFilePath),
               hasRedoChanges(newFilePath)));
            return;
         }
      }
   }

   public void setLineNumbers(String path, boolean lineNumbers)
   {
      EditorTab tab = getEditorTab(path);
      tab.getTextEditor().setLineNumbers(lineNumbers);
   }

   public void setEditorFocus(String path)
   {
      EditorTab tab = getEditorTab(path);
      if (tab == null)
      {
         return;
      }
      tab.getTextEditor().setFocus(); // fix bug "Just after switching on a new tab, the cursor is not appeared in the content pane." [WBT-244]
   }

   public boolean hasRedoChanges(String path)
   {
      EditorTab editorTab = getEditorTab(path);
      if (editorTab == null)
      {
         return false;
      }
      return getEditorTab(path).getTextEditor().hasRedoChanges();
   }

   public boolean hasUndoChanges(String path)
   {
      EditorTab editorTab = getEditorTab(path);
      if (editorTab == null)
      {
         return false;
      }
      return getEditorTab(path).getTextEditor().hasUndoChanges();
   }

   public void onEditorPanelRestored(EditorPanelRestoredEvent event)
   {
      minMaxControlButton.setMaximize(true);
   }

}
