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
package org.exoplatform.ide.extension.cloudfoundry.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * Application information view.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ApplicationInfoView.java Jul 13, 2011 3:00:15 PM vereshchaka $
 *
 */
public class ApplicationInfoView extends ViewImpl implements ApplicationInfoPresenter.Display
{
   public static final String ID = "ideCloudFoundryApplicationInfoView";
   
   public static final String APPLICATION_URIS_ID = "ideCloudFoundryAppUrisGridView";
   
   public static final String APPLICATION_SERVICES_ID = "ideCloudFoundryAppServicesGridView";
   
   public static final String APPLICATION_ENVIRONMENTS_ID = "ideCloudFoundryAppEnvironmentsGridView";

   private static final int HEIGHT = 345;

   private static final int WIDTH = 500;

   private static ApplicationInfoViewUiBinder uiBinder = GWT.create(ApplicationInfoViewUiBinder.class);

   /**
    * Ok button.
    */
   @UiField
   ImageButton okButton;
   
   @UiField
   Label nameLabel;
   
   @UiField
   Label stateLabel;
   
   @UiField
   Label instancesLabel;
   
   @UiField
   Label versionLabel;
   
   @UiField
   ApplicationStringGrid applicationUrisGrid;
   
   @UiField
   ApplicationStringGrid applicationServicesGrid;
   
   @UiField
   ApplicationStringGrid applicationEnvironmentsGrid;
   
   @UiField
   Label diskLabel;
   
   @UiField
   Label memoryLabel;
   
   @UiField
   Label stackLabel;
   
   @UiField
   Label modelLabel;
   
   interface ApplicationInfoViewUiBinder extends UiBinder<Widget, ApplicationInfoView>
   {
   }

   public ApplicationInfoView()
   {
      super(ID, ViewType.MODAL, CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationInfoTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      applicationUrisGrid.setID(APPLICATION_URIS_ID);
      applicationUrisGrid.addColumn(CloudFoundryExtension.LOCALIZATION_CONSTANT.appInfoUris());
      applicationServicesGrid.setID(APPLICATION_SERVICES_ID);
      applicationServicesGrid.addColumn(CloudFoundryExtension.LOCALIZATION_CONSTANT.appInfoServices());
      applicationEnvironmentsGrid.setID(APPLICATION_ENVIRONMENTS_ID);
      applicationEnvironmentsGrid.addColumn(CloudFoundryExtension.LOCALIZATION_CONSTANT.appInfoEnvironments());
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.info.ApplicationInfoPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   @Override
   public void setName(String text)
   {
      nameLabel.setText(text);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setState(java.lang.String)
    */
   @Override
   public void setState(String text)
   {
      stateLabel.setText(text);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setInstances(java.lang.String)
    */
   @Override
   public void setInstances(String text)
   {
      instancesLabel.setText(text);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setVersion(java.lang.String)
    */
   @Override
   public void setVersion(String text)
   {
      versionLabel.setText(text);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#getApplicationUrisGrid()
    */
   @Override
   public ListGridItem<String> getApplicationUrisGrid()
   {
      return applicationUrisGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#getApplicationServicesGrid()
    */
   @Override
   public ListGridItem<String> getApplicationServicesGrid()
   {
      return applicationServicesGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#getApplicationEnvironmentsGrid()
    */
   @Override
   public ListGridItem<String> getApplicationEnvironmentsGrid()
   {
      return applicationEnvironmentsGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setDisk(java.lang.String)
    */
   @Override
   public void setDisk(String text)
   {
      diskLabel.setText(text);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setMemory(java.lang.String)
    */
   @Override
   public void setMemory(String text)
   {
      memoryLabel.setText(text);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setStack(java.lang.String)
    */
   @Override
   public void setStack(String text)
   {
      stackLabel.setText(text);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setModel(java.lang.String)
    */
   @Override
   public void setModel(String text)
   {
      modelLabel.setText(text);
   }
}
