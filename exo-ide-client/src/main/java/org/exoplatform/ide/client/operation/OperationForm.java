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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.event.perspective.OperationPanelRestoredEvent;
import org.exoplatform.ide.client.event.perspective.OperationPanelRestoredHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.ui.LockableView;
import org.exoplatform.ide.client.framework.ui.ViewType;
import org.exoplatform.ide.client.framework.ui.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.gadget.service.GadgetMetadata;
import org.exoplatform.ide.client.module.gadget.ui.GadgetPreviewPane;
import org.exoplatform.ide.client.operation.output.OutputForm;
import org.exoplatform.ide.client.operation.preview.PreviewForm;
import org.exoplatform.ide.client.operation.properties.PropertiesForm;
import org.exoplatform.ide.client.panel.Panel;

/**
 * Form, displayed in bottom side of IDE.
 * 
 * Can contain tabs: Output, Preview (HTML, Google Gadget), Properties.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OperationForm extends Panel implements OperationPresenter.Display, OperationPanelRestoredHandler
{
   public static final String ID = "ideOperationPanel";

   private static final int INITIAL_HEIGHT = 200;

   private OperationPresenter presenter;

   private PropertiesForm propertiesForm;

   private OutputForm outputForm;

   private PreviewForm previewForm;

   private GadgetPreviewPane gadgetPreviewPane;

   public OperationForm(HandlerManager eventBus)
   {
      super(eventBus, ID);
      setHeight(INITIAL_HEIGHT);
      setCanFocus(Boolean.TRUE);

      createButtons();
      setCanFocus(true);
      getViewTypes().add(ViewType.PREVIEW);
      getViewTypes().add(ViewType.PROPERTIES);
      getViewTypes().add(ViewType.OUTPUT);

      propertiesForm = new PropertiesForm(eventBus);
      outputForm = new OutputForm(eventBus);
      previewForm = new PreviewForm(eventBus);

      openView(outputForm, outputForm.getTitle(), outputForm.getImage(), false);

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

      addTabSelectedHandler(tabSelectedHandler);
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

      Tab previewTab = getTab(previewForm.getViewId());
      if (previewTab != null)
      {
         previewForm.showPreview(file.getHref());
      }

      Tab gadgetPreviewTab = getTab(GadgetPreviewPane.ID);
      if (gadgetPreviewTab != null)
      {
         removeTab(GadgetPreviewPane.ID);
      }
   }

   public void closeGadgetPreviewTab()
   {
      Tab gadgetPreviewTab = getTab(GadgetPreviewPane.ID);
      if (gadgetPreviewTab != null)
      {
         removeTab(GadgetPreviewPane.ID);
      }
   }

   private TabSelectedHandler tabSelectedHandler = new TabSelectedHandler()
   {
      public void onTabSelected(TabSelectedEvent event)
      {

      }
   };

   /*
    * Show Output
    */
   public void showOutput()
   {
      show();
      Tab outputTab = getTab(outputForm.getViewId());
      selectTab(outputTab);
   }

   /*
    * Show Properties
    */
   public void showProperties(File file)
   {
      show();

      // if properties already opened
      Tab propertiesTab = getTab(propertiesForm.getViewId());
      if (propertiesTab == null)
      {
         openView(propertiesForm, propertiesForm.getTitle(), propertiesForm.getImage(), true);
         propertiesTab = getTab(propertiesForm.getViewId());
      }
      DeferredCommand.addCommand(new Command()
      {
         public void execute()
         {
            selectTab(propertiesForm.getViewId());
            eventBus.fireEvent(new ViewOpenedEvent(propertiesForm.getViewId()));
         }
      });
    

      propertiesForm.refreshProperties(file);

   }
   
   public void closePropertiesTab()
   {
      if (getTab(propertiesForm.getViewId()) != null)
      {
         closeView(propertiesForm.getViewId());
      }
   }

   public void showPreview(final String path)
   {
      show();

      // if preview already opened
      Tab previewTab = getTab(previewForm.getViewId());
      if (previewTab == null)
      {
         openView(previewForm, previewForm.getTitle(), previewForm.getImage(), true);
      }
      
      DeferredCommand.addCommand(new Command()
      {
         public void execute()
         {
            selectTab(previewForm.getViewId());
            eventBus.fireEvent(new ViewOpenedEvent(previewForm.getViewId()));
            previewForm.showPreview(path);
         }
      });
   }

   public void closePreviewTab()
   {
      Tab previewTab = getTab(previewForm.getViewId());
      if (previewTab != null)
      {
         removeTab(previewTab);
         ((LockableView)previewTab.getPane()).onCloseTab();
      }
   }

   public void showGadget(GadgetMetadata metadata, IDEConfiguration applicationConfiguration)
   {
      show();
      gadgetPreviewPane = new GadgetPreviewPane(eventBus, applicationConfiguration, metadata);
      // if preview already opened
      Tab gadgetPreviewTab = getTab(gadgetPreviewPane.getViewId());
      if (gadgetPreviewTab == null)
      {
         openView(gadgetPreviewPane, gadgetPreviewPane.getTitle(), new Image(IDEImageBundle.INSTANCE.preview()), true);
      }
      DeferredCommand.addCommand(new Command()
      {
         public void execute()
         {
            selectTab(gadgetPreviewPane.getViewId());
            eventBus.fireEvent(new ViewOpenedEvent(gadgetPreviewPane.getViewId()));
            gadgetPreviewPane.onOpenTab();
         }
      });
      
      
   }

   public void onOperationPanelRestored(OperationPanelRestoredEvent event)
   {
      minMaxControlButton.setMaximize(true);
   }

}
