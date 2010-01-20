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
package org.exoplatform.ideall.client.operation;

import java.util.HashMap;
import java.util.List;

import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.editor.MinMaxControlButton;
import org.exoplatform.ideall.client.event.layout.MaximizeOperationPanelEvent;
import org.exoplatform.ideall.client.event.layout.OperationPanelRestoredEvent;
import org.exoplatform.ideall.client.event.layout.OperationPanelRestoredHandler;
import org.exoplatform.ideall.client.event.layout.RestoreOperationPanelEvent;
import org.exoplatform.ideall.client.gadgets.GadgetPreviewPane;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.gadget.GadgetMetadata;
import org.exoplatform.ideall.client.operation.output.OutputForm;
import org.exoplatform.ideall.client.operation.preview.PreviewForm;
import org.exoplatform.ideall.client.operation.properties.PropertiesForm;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.Canvas;
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

public class OperationForm extends Layout implements OperationPresenter.Display, OperationPanelRestoredHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private ApplicationContext context;

   private static final int INITIAL_HEIGHT = 200;

   private OperationPresenter presenter;

   private TabSet tabSet;

   private PropertiesForm propertiesForm;

   private OutputForm outputForm;

   private PreviewForm previewForm;

   private GadgetPreviewPane gadgetPreviewPane;

   private Layout tabBarColtrols;

   private HashMap<String, List<Canvas>> tabColtrolButtons = new HashMap<String, List<Canvas>>();

   protected String previousTab = null;

   protected MinMaxControlButton minMaxControlButton;

   public OperationForm(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      setHeight(INITIAL_HEIGHT);

      tabSet = new TabSet();
      createButtons();
      addMember(tabSet);

      propertiesForm = new PropertiesForm(eventBus);
      outputForm = new OutputForm(eventBus);
      previewForm = new PreviewForm(eventBus, context);

      addTab(outputForm, false);

      /*
       * 
       */
      addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            event.cancel();
         }
      });

      presenter = new OperationPresenter(eventBus, context);
      presenter.bindDisplay(this);

      tabSet.addTabSelectedHandler(tabSelectedHandler);
      tabSet.addCloseClickHandler(closeClickhandler);

      handlers.addHandler(OperationPanelRestoredEvent.TYPE, this);
   }

   @Override
   public void destroy()
   {
      presenter.destroy();
      handlers.removeHandlers();
      super.destroy();
   }

   private void createButtons()
   {
      tabBarColtrols = new Layout();
      tabBarColtrols.setHeight(18);
      tabBarColtrols.setAutoWidth();

      minMaxControlButton =
         new MinMaxControlButton(eventBus, true, new MaximizeOperationPanelEvent(), new RestoreOperationPanelEvent());
      tabBarColtrols.addMember(minMaxControlButton);

      tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, tabBarColtrols);
   }

   /**
    * Add new Tab to TabSet
    * 
    * @param tabPanel
    * @param canClose
    * @return
    */
   private Tab addTab(TabPanel tabPanel, boolean canClose)
   {
      /*
       * disable all control buttons
       */
      for (String tabTitle : tabColtrolButtons.keySet())
      {
         List<Canvas> buttons = tabColtrolButtons.get(tabTitle);
         for (Canvas button : buttons)
         {
            button.hide();
         }
      }

      tabColtrolButtons.put(tabPanel.getTitle(), tabPanel.getColtrolButtons());
      int position = 0;
      for (Canvas button : tabColtrolButtons.get(tabPanel.getTitle()))
      {
         tabBarColtrols.addMember(button, position);
         position++;
      }

      Tab tab = new Tab(tabPanel.getTitle());
      tab.setID(tabPanel.getId());
      tab.setPane(tabPanel);
      tab.setCanClose(canClose);
      tabSet.addTab(tab);

      tabPanel.onOpenTab();

      return tab;
   }

   /*
    * Switching
    */
   public void changeActiveFile(File file)
   {
      Tab propertiesTab = tabSet.getTab(propertiesForm.getId());
      if (propertiesTab != null)
      {
         propertiesForm.refreshProperties(file);
      }

      Tab previewTab = tabSet.getTab(previewForm.getId());
      if (previewTab != null)
      {
         previewForm.showPreview(file.getPath());
      }

      Tab gadgetPreviewTab = tabSet.getTab(GadgetPreviewPane.ID);
      if (gadgetPreviewTab != null)
      {
         tabSet.removeTab(GadgetPreviewPane.ID);
      }
   }

   private TabSelectedHandler tabSelectedHandler = new TabSelectedHandler()
   {
      public void onTabSelected(TabSelectedEvent event)
      {
         if (previousTab != null)
         {
            List<Canvas> buttons = tabColtrolButtons.get(previousTab);
            for (Canvas button : buttons)
            {
               button.hide();
            }
         }

         int buttonsWidth = 20;
         List<Canvas> buttonsToShow = tabColtrolButtons.get(event.getTab().getTitle());
         for (Canvas button : buttonsToShow)
         {
            button.show();
            buttonsWidth += button.getWidth();
         }

         previousTab = event.getTab().getTitle();

         for (Canvas c : tabSet.getChildren())
         {
            if (c.getID().equals(tabSet.getID() + "_tabBar"))
            {
               if (c.getWidth() > tabSet.getWidth() - buttonsWidth)
               {
                  c.setWidth(tabSet.getWidth() - buttonsWidth);
               }
            }
         }

      }
   };

   /**
    * Closing tab click handler
    */
   private CloseClickHandler closeClickhandler = new CloseClickHandler()
   {
      public void onCloseClick(TabCloseClickEvent event)
      {
         /*
          * delete all buttons from control bar 
          */
         List<Canvas> buttons = tabColtrolButtons.get(event.getTab().getTitle());
         for (Canvas button : buttons)
         {
            tabBarColtrols.removeMember(button);
         }

         ((TabPanel)event.getTab().getPane()).onCloseTab();
      }
   };

   /*
    * Show Output
    */

   public void showOutput()
   {
      show();
      Tab outputTab = tabSet.getTab(outputForm.getId());
      tabSet.selectTab(outputTab);
   }

   /*
    * Show Properties
    */

   public void showProperties(File file)
   {
      show();

      // if properties already opened
      Tab propertiesTab = tabSet.getTab(propertiesForm.getId());
      if (propertiesTab == null)
      {
         propertiesTab = addTab(propertiesForm, true);
      }

      tabSet.selectTab(propertiesTab);

      propertiesForm.refreshProperties(file);

   }

   public void closePropertiesTab()
   {
      if (tabSet.getTab(propertiesForm.getId()) != null)
      {
         tabSet.removeTab(propertiesForm.getId());
      }
   }

   public void showPreview(String path)
   {
      show();

      // if preview already opened
      Tab previewTab = tabSet.getTab(previewForm.getId());
      if (previewTab == null)
      {
         previewTab = addTab(previewForm, true);
      }

      tabSet.selectTab(previewTab);
      previewForm.showPreview(path);
   }

   public void closePreviewTab()
   {
      Tab previewTab = tabSet.getTab(previewForm.getId());
      if (previewTab != null)
      {
         tabSet.removeTab(previewTab);
         ((TabPanel)previewTab.getPane()).onCloseTab();
      }
   }

   protected Tab gadgetPreviewTab;

   public void showGadget(GadgetMetadata metadata)
   {
      show();
      gadgetPreviewPane = new GadgetPreviewPane(eventBus, metadata);
      // if preview already opened
      gadgetPreviewTab = tabSet.getTab(gadgetPreviewPane.getId());
      if (gadgetPreviewTab == null)
      {
         gadgetPreviewTab = addTab(gadgetPreviewPane, true);
      }
      tabSet.selectTab(gadgetPreviewTab);
   }

   public void onOperationPanelRestored(OperationPanelRestoredEvent event)
   {
      minMaxControlButton.setMaximize(true);
   }

}
