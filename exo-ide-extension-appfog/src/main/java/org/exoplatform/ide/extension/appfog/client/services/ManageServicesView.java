/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.appfog.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.shared.AppfogProvisionedService;

/**
 * View for managing Appfog services.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ManageServicesView extends ViewImpl implements ManageServicesPresenter.Display
{

   private static final String ID = "ideManageServicesView";

   private static final int WIDTH = 740;

   private static final int HEIGHT = 300;

   private static final String DELETE_BUTTON_ID = "ideManageServicesViewDeleteButton";

   private static final String ADD_BUTTON_ID = "ideManageServicesViewAddButton";

   private static final String CANCEL_BUTTON_ID = "ideManageServicesViewCancelButton";

   private static BindServiceViewUiBinder uiBinder = GWT.create(BindServiceViewUiBinder.class);

   @UiField
   ImageButton deleteButton;

   @UiField
   ImageButton addButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   ProvisionedServicesGrid servicesGrid;

   @UiField
   BoundedServicesGrid boundedServicesGrid;

   interface BindServiceViewUiBinder extends UiBinder<Widget, ManageServicesView>
   {
   }

   public ManageServicesView()
   {
      super(ID, ViewType.MODAL, AppfogExtension.LOCALIZATION_CONSTANT.bindServiceViewTitle(), null, WIDTH,
         HEIGHT, true);
      add(uiBinder.createAndBindUi(this));

      addButton.setButtonId(ADD_BUTTON_ID);
      deleteButton.setButtonId(DELETE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getRemoveButton()
    */
   @Override
   public HasClickHandlers getAddButton()
   {
      return addButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getDeleteButton()
    */
   @Override
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getProvisionedServicesGrid()
    */
   @Override
   public ListGridItem<AppfogProvisionedService> getProvisionedServicesGrid()
   {
      return servicesGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#enableDeleteButton(boolean)
    */
   @Override
   public void enableDeleteButton(boolean enabled)
   {
      deleteButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getUnbindServiceHandler()
    */
   @Override
   public HasUnbindServiceHandler getUnbindServiceHandler()
   {
      return boundedServicesGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getBoundedServicesGrid()
    */
   @Override
   public ListGridItem<String> getBoundedServicesGrid()
   {
      return boundedServicesGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getBindServiceHandler()
    */
   @Override
   public HasBindServiceHandler getBindServiceHandler()
   {
      return servicesGrid;
   }

}
