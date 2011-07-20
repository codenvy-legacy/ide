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
package org.exoplatform.ide.extension.cloudfoundry.client.url;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * Unmap URL view.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UnmapUrlsView.java Jul 19, 2011 2:43:28 PM vereshchaka $
 *
 */
public class UnmapUrlView extends ViewImpl implements UnmapUrlPresenter.Display
{
   public static final String ID = "ideCloudFoundryApplicationInfoView";
   
   public static final String LISTGRID_ID = "ideCloudFoundryRegisteredUrlsGridView";
   
   private static final int HEIGHT = 345;

   private static final int WIDTH = 500;

   private static UnmapUrlViewUiBinder uiBinder = GWT.create(UnmapUrlViewUiBinder.class);

   @UiField
   ImageButton deleteButton;
   
   @UiField
   ImageButton closeButton;
   
   @UiField
   RegisteredUrlsGrid registeredUrlsGrid;
   
   interface UnmapUrlViewUiBinder extends UiBinder<Widget, UnmapUrlView>
   {
   }

   public UnmapUrlView()
   {
      super(ID, ViewType.MODAL, CloudFoundryExtension.LOCALIZATION_CONSTANT.unmapUrlViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      registeredUrlsGrid.setID(LISTGRID_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter.Display#getDeleteButton()
    */
   @Override
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter.Display#getRegisteredUrlsGrid()
    */
   @Override
   public ListGridItem<UrlData> getRegisteredUrlsGrid()
   {
      return registeredUrlsGrid;
   }

}
