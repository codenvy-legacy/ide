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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.Version;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog window with list of versions.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class ViewVersionsForm extends DialogWindow implements ViewVersionsPresenter.Display
{

   public static final int WIDTH = 460;

   public static final int HEIGHT = 250;

   private final int BUTTON_HEIGHT = 22;

   private static final String ID = "ideViewVersionsForm";

   private final String ID_OPEN_VERSION_BUTTON = "ideViewVersionsFormOpenVersionButton";

   private final String ID_CLOSE_BUTTON = "ideViewVersionsFormCloseButton";

   private final String TITLE = IDE.VERSIONS_CONSTANT.viewVersionsTitle();
   
   private static final String OPEN = IDE.IDE_LOCALIZATION_CONSTANT.openButton();
   
   private static final String CLOSE = IDE.IDE_LOCALIZATION_CONSTANT.closeButton();

   private ImageButton openVersionButton;

   private ImageButton closeButton;

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
      super(WIDTH, HEIGHT, ID);

      String title =
         ((item != null) && (item.getName() != null) && (item.getName().length() > 0)) ? TITLE + " " 
            + IDE.VERSIONS_CONSTANT.viewVersionsFor() + " "
            + item.getName() : TITLE;

      setTitle(title);
      //TODO setCanDragResize(true);

      VerticalPanel vPanel = new VerticalPanel();
      vPanel.setHeight("100%");
      vPanel.setWidth("100%");
      vPanel.setSpacing(10);
      vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      versionsGrid = new VersionsGrid();
      versionsGrid.setWidth(420);
      versionsGrid.setHeight(200);
      versionsGrid.setValue(versions);

      vPanel.add(versionsGrid);
      vPanel.add(createButtonsLayout());
      setWidget(vPanel);

      presenter = new ViewVersionsPresenter(eventBus, versions);
      presenter.bindDisplay(this);

      show();
   }

   private HorizontalPanel createButtonsLayout()
   {
      HorizontalPanel hLayout = new HorizontalPanel();
      hLayout.setHeight(BUTTON_HEIGHT + "px");
      hLayout.setSpacing(5);

      openVersionButton = new ImageButton(OPEN, "ok");
      openVersionButton.setId(ID_OPEN_VERSION_BUTTON);

      closeButton = new ImageButton(CLOSE, "cancel");
      closeButton.setId(ID_CLOSE_BUTTON);

      hLayout.add(openVersionButton);
      hLayout.add(closeButton);

      return hLayout;
   }

   /**
    * @see org.exoplatform.ide.client.versioning.ViewVersionsPresenter.Display#getOpenVersionButton()
    */
   public HasClickHandlers getOpenVersionButton()
   {
      return openVersionButton;
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
    * @see org.exoplatform.gwtframework.ui.client.window.Window#destroy()
    */
   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
   }

   /**
    * @see org.exoplatform.ide.client.versioning.ViewVersionsPresenter.Display#getVersionsGrid()
    */
   public ListGridItem<Version> getVersionsGrid()
   {
      return versionsGrid;
   }

   /**
    * @see org.exoplatform.ide.client.versioning.ViewVersionsPresenter.Display#getSelectedVersion()
    */
   public Version getSelectedVersion()
   {
      return versionsGrid.getSelectedVersion();
   }

   /**
    * @see org.exoplatform.ide.client.versioning.ViewVersionsPresenter.Display#enableOpenVersionButton(boolean)
    */
   public void enableOpenVersionButton(boolean enable)
   {
      if (enable)
      {
         openVersionButton.setEnabled(true);
      }
      else
      {
         openVersionButton.setEnabled(false);
      }

   }
}
