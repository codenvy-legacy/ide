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

import org.exoplatform.gwt.commons.smartgwt.component.CheckboxItem;
import org.exoplatform.ideall.client.editor.codemirror.CodeMirrorConfig;
import org.exoplatform.ideall.client.editor.codemirror.SmartGWTCodeMirror;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
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

public class EditorForm extends Layout implements EditorPresenter.Display
{

   private HandlerManager eventBus;

   private EditorPresenter presenter;

   private TabSet tabSet;

   private EditorTab activeTab;

   private CheckboxItem showLineNumbersItemCheckbox;

   /**
    * @param eventBus
    */
   public EditorForm(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;

      tabSet = new TabSet();
      createTabBarControls();
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
   }

   private void createTabBarControls()
   {
      Layout l = new Layout();
      DynamicForm form = new DynamicForm();
      form.setCellPadding(0);

      showLineNumbersItemCheckbox = new CheckboxItem();
      showLineNumbersItemCheckbox.setTitle("Line numbers");
      showLineNumbersItemCheckbox.setValue(true);
      form.setItems(showLineNumbersItemCheckbox);

      form.setAutoHeight();
      form.setAutoWidth();

      l.addMember(form);
      l.setAutoWidth();
      l.setAutoHeight();

      tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, l);
   }

   private TabSelectedHandler tabSelectedHandler = new TabSelectedHandler()
   {
      public void onTabSelected(TabSelectedEvent event)
      {
         try {
            activeTab = (EditorTab)event.getTab();
            String path = activeTab.getFile().getPath(); 
            eventBus.fireEvent(new EditorActiveFileChangedEvent(activeTab.getFile(), hasUndoChanges(path), hasRedoChanges(path)));            
         } catch (Exception exc) {
            exc.printStackTrace();
         }
      }
   };

   private CloseClickHandler closeClickHandler = new CloseClickHandler()
   {
      public void onCloseClick(TabCloseClickEvent event)
      {
         EditorTab tab = (EditorTab)event.getTab();
         eventBus.fireEvent(new EditorCloseFileEvent(tab.getFile()));
         if (tab.getFile().isContentChanged() || tab.getFile().isPropertiesChanged())
         {
            event.cancel();
         }
         else
         {
            if (tabSet.getTabs().length == 1)
            {
               eventBus.fireEvent(new EditorActiveFileChangedEvent(null, false, false));
            }
         }
      }
   };

   public void addTab(File file)
   {
      EditorTab tab = new EditorTab(file);

      // set showLineNumbersItem checkbox
      tab.setShowLineNumbersFlag(true);
      showLineNumbersItemCheckbox.setValue(true);      
      
      tab.setCanClose(true);
      
      CodeMirrorConfig config = new CodeMirrorConfig(file.getContentType());
      SmartGWTCodeMirror codemirror = new SmartGWTCodeMirror(eventBus, config);
      tab.setCodeMirror(codemirror);
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
      try {
         getEditorTab(path).getCodeMirror().setText(text);         
      } catch (Exception exc) {
         exc.printStackTrace();
      }

      //activeTab.getCodeMirror()
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
      return tab.getCodeMirror().getText();
   }

   public String getPathByEditorId(String codeMirrorId)
   {
      for (Tab tab : tabSet.getTabs())
      {
         EditorTab editorTab = (EditorTab)tab;
         if (editorTab.getCodeMirror().getEditorId().equals(codeMirrorId))
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
      getEditorTab(path).getCodeMirror().redo();
   }

   public void undoEditing(String path)
   {
      getEditorTab(path).getCodeMirror().undo();
   }

   public void formatFile(String path)
   {
      getEditorTab(path).getCodeMirror().reindentEditor();
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
            eventBus.fireEvent(new EditorActiveFileChangedEvent(newFile, hasUndoChanges(newFilePath), hasRedoChanges(newFilePath)));
            return;
         }
      }
   }

   public HasValue<Boolean> getShowLineNumbersField()
   {
      return showLineNumbersItemCheckbox;
   }

   public HasValueChangeHandlers<Boolean> getShowLineNumbersChangeable()
   {
      return showLineNumbersItemCheckbox;
   }

   public void disableShowLineNumbers()
   {
      showLineNumbersItemCheckbox.disable();
   }

   public void enableShowLineNumbers()
   {
      showLineNumbersItemCheckbox.enable();
   }

   public void setLineNumbers(String path, boolean lineNumbers)
   {
      EditorTab tab = getEditorTab(path);
      tab.getCodeMirror().setLineNumbers(lineNumbers);
      tab.setShowLineNumbersFlag(lineNumbers);
   }

   public void setCodemirrorFocus(String path)
   {
      EditorTab tab = getEditorTab(path);
      tab.getCodeMirror().setFocus(); // fix bug "Just after switching on a new tab, the cursor is not appeared in the content pane." [WBT-244]
   }

   public void updateShowLineNumbersCheckbox(String path)
   {
      EditorTab tab = getEditorTab(path);
      showLineNumbersItemCheckbox.setValue(tab.isShowLineNumbers());      
   }

   public boolean hasRedoChanges(String path)
   {
      return getEditorTab(path).getCodeMirror().hasRedoChanges();
   }

   public boolean hasUndoChanges(String path)
   {
      return getEditorTab(path).getCodeMirror().hasUndoChanges();
   }

}
