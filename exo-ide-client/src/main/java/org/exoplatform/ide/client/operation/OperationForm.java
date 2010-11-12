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
package org.exoplatform.ide.client.operation;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.event.perspective.OperationPanelRestoredEvent;
import org.exoplatform.ide.client.event.perspective.OperationPanelRestoredHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.ui.LockableView;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.gadget.service.GadgetMetadata;
import org.exoplatform.ide.client.module.gadget.ui.GadgetPreviewPane;
import org.exoplatform.ide.client.operation.output.OutputForm;
import org.exoplatform.ide.client.operation.preview.PreviewForm;
import org.exoplatform.ide.client.operation.properties.PropertiesForm;
import org.exoplatform.ide.client.panel.Panel;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.Tab;
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
   private final String ID = "ideOperationPanel";

   private final String TABSET_ID = "ideOperationFormTabSet";

   private HandlerManager eventBus;

   private Handlers handlers;

   private static final int INITIAL_HEIGHT = 200;

   private OperationPresenter presenter;

   private Panel tabSet;

   private PropertiesForm propertiesForm;

   private OutputForm outputForm;

   private PreviewForm previewForm;

   private GadgetPreviewPane gadgetPreviewPane;



   public OperationForm(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      
      setID(ID);

      setHeight(INITIAL_HEIGHT);

      tabSet = new Panel(eventBus,TABSET_ID);
//      tabSet.setID(TABSET_ID);
      tabSet.createButtons();
      addMember(tabSet);

      propertiesForm = new PropertiesForm(eventBus);
      outputForm = new OutputForm(eventBus);
      previewForm = new PreviewForm(eventBus);

      tabSet.openView(outputForm, outputForm.getTitle(), outputForm.getImage(), false);

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

      presenter = new OperationPresenter(eventBus);
      presenter.bindDisplay(this);

      tabSet.addTabSelectedHandler(tabSelectedHandler);
//      tabSet.addCloseClickHandler(closeClickhandler);

      handlers.addHandler(OperationPanelRestoredEvent.TYPE, this);
   }

   @Override
   public void destroy()
   {
      presenter.destroy();
      handlers.removeHandlers();
      super.destroy();
   }



//   /**
//    * Add new Tab to TabSet
//    * 
//    * @param tabPanel
//    * @param canClose
//    * @return
//    */
//   private Tab addTab(View tabPanel, boolean canClose)
//   {
//      /*
//       * disable all control buttons
//       */
//     
//
//      Tab tab = new Tab(tabPanel.getTitle());
//      tab.setID(tabPanel.getViewId());
//      tab.setPane(tabPanel);
//      tab.setCanClose(canClose);
//      tabSet.addTab(tab);
//
//      tabPanel.onOpenTab();
//
//      return tab;
//   }

   /*
    * Switching
    */
   public void changeActiveFile(File file)
   {
      //      TODO
      //      Tab propertiesTab = tabSet.getTab(propertiesForm.getId());
      //      if (propertiesTab != null)
      //      {
      //         propertiesForm.refreshProperties(file);
      //      }

      Tab previewTab = tabSet.getTab(previewForm.getViewId());
      if (previewTab != null)
      {
         previewForm.showPreview(file.getHref());
      }

      Tab gadgetPreviewTab = tabSet.getTab(GadgetPreviewPane.ID);
      if (gadgetPreviewTab != null)
      {
         tabSet.removeTab(GadgetPreviewPane.ID);
      }
   }

   public void closeGadgetPreviewTab()
   {
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
        

      }
   };

//   /**
//    * Closing tab click handler
//    */
//   private CloseClickHandler closeClickhandler = new CloseClickHandler()
//   {
//      public void onCloseClick(TabCloseClickEvent event)
//      {
//
//
//         ((TabPanel)event.getTab().getPane()).onCloseTab();
//      }
//   };

   /*
    * Show Output
    */
   public void showOutput()
   {
      show();
      Tab outputTab = tabSet.getTab(outputForm.getViewId());
      tabSet.selectTab(outputTab);
   }

   /*
    * Show Properties
    */
   public void showProperties(File file)
   {
      show();

      // if properties already opened
      Tab propertiesTab = tabSet.getTab(propertiesForm.getViewId());
      if (propertiesTab == null)
      {
         tabSet.openView(propertiesForm, propertiesForm.getTitle(), propertiesForm.getImage(), true);
         propertiesTab = tabSet.getTab(propertiesForm.getViewId());
      }

      tabSet.selectTab(propertiesTab.getID());

      propertiesForm.refreshProperties(file);

   }

   public void closePropertiesTab()
   {
      if (tabSet.getTab(propertiesForm.getViewId()) != null)
      {
         tabSet.closeView(propertiesForm.getViewId());
      }
   }

   public void showPreview(String path)
   {
      show();

      // if preview already opened
      Tab previewTab = tabSet.getTab(previewForm.getViewId());
      if (previewTab == null)
      {
         tabSet.openView(previewForm, previewForm.getTitle(), previewForm.getImage(), true);
      }

      tabSet.selectTab(previewForm.getViewId());
      previewForm.showPreview(path);
   }

   public void closePreviewTab()
   {
      Tab previewTab = tabSet.getTab(previewForm.getViewId());
      if (previewTab != null)
      {
         tabSet.removeTab(previewTab);
         ((LockableView)previewTab.getPane()).onCloseTab();
      }
   }

   public void showGadget(GadgetMetadata metadata, IDEConfiguration applicationConfiguration)
   {
      show();
      gadgetPreviewPane = new GadgetPreviewPane(eventBus, applicationConfiguration, metadata);
      // if preview already opened
      Tab gadgetPreviewTab = tabSet.getTab(gadgetPreviewPane.getViewId());
      if (gadgetPreviewTab == null)
      {
         tabSet.openView(gadgetPreviewPane, gadgetPreviewPane.getTitle(), new Image(IDEImageBundle.INSTANCE.preview()), true);
      }
      tabSet.selectTab(gadgetPreviewPane.getViewId());
      gadgetPreviewPane.onOpenTab();
   }

   public void onOperationPanelRestored(OperationPanelRestoredEvent event)
   {
//      minMaxControlButton.setMaximize(true);
   }

}
