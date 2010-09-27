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
package org.exoplatform.ide.client.versioning;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.smartgwt.client.widgets.layout.HLayout;

import com.smartgwt.client.widgets.layout.VLayout;

import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.Version;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class ViewVersionsForm extends DialogWindow implements ViewVersionsPresenter.Display
{

   public static final int WIDTH = 400;

   public static final int HEIGHT = 300;

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   private static final String ID = "ideViewVersionsForm";

   private final String ID_OPEN_VERSION_BUTTON = "ideViewVersionsFormOpenVersionButton";

   private final String ID_RESTORE_BUTTON = "ideViewVersionsFormRestoreButton";

   private final String ID_CLOSE_BUTTON = "ideViewVersionsFormCloseButton";

   private final String TITLE = "Version history";

   private IButton openVersionButton;

   private IButton closeButton;

   private IButton restoreButton;
   
   private VersionsGrid versionsGrid;

   private ViewVersionsPresenter presenter;

   /**
    * @param eventBus
    * @param width
    * @param height
    * @param id
    */
   public ViewVersionsForm(HandlerManager eventBus, Item item, List<Version> versions)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      setTitle(TITLE);
      setCanDragResize(true);

      VLayout mainLayout = new VLayout();
      mainLayout.setWidth100();
      mainLayout.setHeight100();
      mainLayout.setPadding(10);
      mainLayout.setMembersMargin(15);

      versionsGrid = new VersionsGrid();
      versionsGrid.setWidth100();
      versionsGrid.setHeight100();
      versionsGrid.setValue(versions);
      mainLayout.addMember(versionsGrid);
      
      mainLayout.addMember(createButtonsLayout());

      addItem(mainLayout);
      
      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

      presenter = new ViewVersionsPresenter(eventBus);
      presenter.bindDisplay(this);

      show();
   }

   private HLayout createButtonsLayout()
   {
      HLayout hLayout = new HLayout();
      hLayout.setWidth100();
      hLayout.setHeight(BUTTON_HEIGHT);
      hLayout.setMembersMargin(10);

      openVersionButton = createButton("Open", ID_OPEN_VERSION_BUTTON, "");
      restoreButton = createButton("Restore", ID_RESTORE_BUTTON, "");
      closeButton = createButton("Close", ID_CLOSE_BUTTON, "");

      hLayout.addMember(restoreButton);

      HLayout delimeter = new HLayout();
      delimeter.setWidth100();
      delimeter.setHeight100();
      hLayout.addMember(delimeter);

      hLayout.addMember(openVersionButton);
      hLayout.addMember(closeButton);

      return hLayout;
   }

   private IButton createButton(String title, String id, String icon)
   {
      IButton button = new IButton(title);
      button.setHeight(BUTTON_HEIGHT);
      button.setWidth(BUTTON_WIDTH);
      button.setIcon(icon);
      return button;
   }

   /**
    * @see org.exoplatform.ide.client.versioning.ViewVersionsPresenter.Display#getOpenVersionButton()
    */
   public HasClickHandlers getOpenVersionButton()
   {
      return openVersionButton;
   }

   /**
    * @see org.exoplatform.ide.client.versioning.ViewVersionsPresenter.Display#getRestoreButton()
    */
   public HasClickHandlers getRestoreButton()
   {
      return restoreButton;
   }

   /**
    * @see org.exoplatform.ide.client.versioning.ViewVersionsPresenter.Display#getCloseButton()
    */
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.client.versioning.ViewVersionsPresenter.Display#closeForm()
    */
   public void closeForm()
   {
      destroy();
   }

   /**
    * @see com.smartgwt.client.widgets.BaseWidget#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }

   /**
    * @see org.exoplatform.ide.client.versioning.ViewVersionsPresenter.Display#getVersionsGrid()
    */
   public ListGridItem<Version> getVersionsGrid()
   {
      return versionsGrid;
   }
}
