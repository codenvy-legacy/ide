//TODO: need rework according new VFS
///*
// * Copyright (C) 2010 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.exoplatform.ide.client.versioning;
//
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.event.dom.client.HasClickHandlers;
//import com.google.gwt.uibinder.client.UiBinder;
//import com.google.gwt.uibinder.client.UiField;
//import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.user.client.ui.Widget;
//
//import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
//import org.exoplatform.gwtframework.ui.client.component.ImageButton;
//import org.exoplatform.ide.client.IDE;
//import org.exoplatform.ide.client.IDEImageBundle;
//import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
//import org.exoplatform.ide.client.framework.vfs.Version;
//
///**
// * Dialog window with list of versions.
// * 
// * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
// * @version $Id: Sep 27, 2010 $
// *
// */
//public class VersionsListView extends ViewImpl implements VersionsListPresenter.Display
//{
//
//   public static final int WIDTH = 460;
//
//   public static final int HEIGHT = 250;
//
//   private static final String ID = "ideViewVersionsForm";
//
//   private final String OPEN_VERSION_BUTTON_ID = "ideViewVersionsFormOpenVersionButton";
//
//   private final String CLOSE_BUTTON_ID = "ideViewVersionsFormCloseButton";
//
//   private static final String TITLE = IDE.VERSIONS_CONSTANT.viewVersionsTitle();
//   
//   @UiField
//   ImageButton openVersionButton;
//
//   @UiField
//   ImageButton closeButton;
//
//   @UiField
//   VersionsGrid versionsGrid;
//
//   interface VersionsListViewUiBinder extends UiBinder<Widget, VersionsListView>
//   {
//   }
//   
//   private static VersionsListViewUiBinder uiBinder = GWT.create(VersionsListViewUiBinder.class);
//
//   /**
//    * @param eventBus
//    * @param width
//    * @param height
//    * @param id
//    */
//   public VersionsListView()
//   {
//      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT);
//      add(uiBinder.createAndBindUi(this));
//      
//      openVersionButton.setButtonId(OPEN_VERSION_BUTTON_ID);
//      closeButton.setButtonId(CLOSE_BUTTON_ID);
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.VersionsListPresenter.Display#getOpenVersionButton()
//    */
//   public HasClickHandlers getOpenVersionButton()
//   {
//      return openVersionButton;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.VersionsListPresenter.Display#getCloseButton()
//    */
//   public HasClickHandlers getCloseButton()
//   {
//      return closeButton;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.VersionsListPresenter.Display#getVersionsGrid()
//    */
//   public ListGridItem<Version> getVersionsGrid()
//   {
//      return versionsGrid;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.VersionsListPresenter.Display#getSelectedVersion()
//    */
//   public Version getSelectedVersion()
//   {
//      return versionsGrid.getSelectedVersion();
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.VersionsListPresenter.Display#enableOpenVersionButton(boolean)
//    */
//   public void enableOpenVersionButton(boolean enable)
//   {
//      if (enable)
//      {
//         openVersionButton.setEnabled(true);
//      }
//      else
//      {
//         openVersionButton.setEnabled(false);
//      }
//
//   }
// }
