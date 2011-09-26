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
package org.exoplatform.ide.extension.cloudfoundry.client.apps;

import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 18, 2011 evgen $
 *
 */
public class ApplicationsView extends ViewImpl implements ApplicationsPresenter.Display
{

   private static ApplicationsViewUiBinder uiBinder = GWT.create(ApplicationsViewUiBinder.class);

   interface ApplicationsViewUiBinder extends UiBinder<Widget, ApplicationsView>
   {
   }

   private static final int HEIGHT = 300;

   private static final int WIDTH = 820;

   /**
    * Close button.
    */
   @UiField
   ImageButton closeButton;
   
   @UiField
   ImageButton showButton;

   @UiField
   ApplicationsListGrid applicationsGrid;
   
   @UiField
   ComboBoxField serverField;

   public ApplicationsView()
   {
      super(ID, ViewType.MODAL, CloudFoundryExtension.LOCALIZATION_CONSTANT.appsViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#getAppsGrid()
    */
   @Override
   public ListGridItem<CloudfoundryApplication> getAppsGrid()
   {
      return applicationsGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#getActions()
    */
   @Override
   public HasApplicationsActions getActions()
   {
      return applicationsGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#getServerSelectField()
    */
   @Override
   public HasValue<String> getServerSelectField()
   {
      return serverField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#setServerValues(java.lang.String[])
    */
   @Override
   public void setServerValues(String[] servers)
   {
      serverField.setValueMap(servers);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#getShowButton()
    */
   @Override
   public HasClickHandlers getShowButton()
   {
      return showButton;
   }

}
