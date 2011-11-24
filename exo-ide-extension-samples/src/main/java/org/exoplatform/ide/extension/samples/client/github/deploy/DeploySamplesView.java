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
package org.exoplatform.ide.extension.samples.client.github.deploy;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

/**
 * View to deploy samples imported from GitHub.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeploySamplesView.java Nov 22, 2011 10:35:27 AM vereshchaka $
 */
public class DeploySamplesView extends ViewImpl implements DeploySamplesPresenter.Display
{
   private static final String ID = "DeploySamplesView";

   private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.wizardDeploymentDialogTitle();

   private static final int HEIGHT = 345;

   private static final int WIDTH = 550;

   interface DeploySamplesViewUiBinder extends UiBinder<Widget, DeploySamplesView>
   {
   }

   /**
    * UIBinder instance
    */
   private static DeploySamplesViewUiBinder uiBinder = GWT.create(DeploySamplesViewUiBinder.class);

   @UiField
   SelectItem selectPaasField;

   @UiField
   ImageButton cancelButton;

   @UiField
   ImageButton finishButton;

   @UiField
   ImageButton backButton;

   @UiField
   FlowPanel cloudFoundryPanel;

   @UiField
   FlowPanel cloudBeesPanel;

   @UiField
   ComboBoxField cloudFoundryTargetField;

   @UiField
   TextInput cloudFoundryNameField;

   @UiField
   TextInput cloudFoundryUrlField;

   @UiField
   SelectItem selectDomainField;

   @UiField
   TextInput cloudBeesNameField;

   @UiField
   TextInput cloudBeesIdField;
   
   //heroku
   @UiField
   FlowPanel herokuPanel;
   
   @UiField
   TextInput herokuNameField;
   
   @UiField
   TextInput herokuRepositoryNameField;
   
   //openShift
   @UiField
   FlowPanel openShiftPanel;
   
   @UiField
   TextInput openShiftNameField;
   
   @UiField
   SelectItem openShiftTypeField;

   public DeploySamplesView()
   {
      super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getFinishButton()
    */
   @Override
   public HasClickHandlers getFinishButton()
   {
      return finishButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getBackButton()
    */
   @Override
   public HasClickHandlers getBackButton()
   {
      return backButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getSelectPaasField()
    */
   @Override
   public HasValue<String> getSelectPaasField()
   {
      return selectPaasField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#setPaasValueMap(java.lang.String[], java.lang.String)
    */
   @Override
   public void setPaasValueMap(String[] values, String selected)
   {
      selectPaasField.setValueMap(values, selected);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#setVisibleCloudBeesPanel(boolean)
    */
   @Override
   public void setVisibleCloudBeesPanel(boolean visible)
   {
      cloudBeesPanel.setVisible(visible);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#setVisibleCloudFoundryPanel(boolean)
    */
   @Override
   public void setVisibleCloudFoundryPanel(boolean visible)
   {
      cloudFoundryPanel.setVisible(visible);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#enableFinishButton(boolean)
    */
   @Override
   public void enableFinishButton(boolean enable)
   {
      finishButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getSelectCloudBeesDomainField()
    */
   @Override
   public HasValue<String> getSelectCloudBeesDomainField()
   {
      return selectDomainField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getCloudBeesNameField()
    */
   @Override
   public HasValue<String> getCloudBeesNameField()
   {
      return cloudBeesNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getCloudBeesIdField()
    */
   @Override
   public HasValue<String> getCloudBeesIdField()
   {
      return cloudBeesIdField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#setCloudBeesDomainsValueMap(java.lang.String[])
    */
   @Override
   public void setCloudBeesDomainsValueMap(String[] values)
   {
      selectDomainField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getCloudFoundryNameField()
    */
   @Override
   public HasValue<String> getCloudFoundryNameField()
   {
      return cloudFoundryNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getCloudFoundryUrlField()
    */
   @Override
   public HasValue<String> getCloudFoundryUrlField()
   {
      return cloudFoundryUrlField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getCloudFoundryTargetField()
    */
   @Override
   public HasValue<String> getCloudFoundryTargetField()
   {
      return cloudFoundryTargetField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#setCloudFoundryAvailableTargets(java.lang.String[])
    */
   @Override
   public void setCloudFoundryAvailableTargets(String[] targets)
   {
      cloudFoundryTargetField.setValueMap(targets);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#setVisibleHerokuPanel(boolean)
    */
   @Override
   public void setVisibleHerokuPanel(boolean visible)
   {
      herokuPanel.setVisible(visible);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#setVisibleOpenShiftPanel(boolean)
    */
   @Override
   public void setVisibleOpenShiftPanel(boolean visible)
   {
      openShiftPanel.setVisible(visible);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#setOpenShitfTypesValueMap(java.lang.String[])
    */
   @Override
   public void setOpenShitfTypesValueMap(String[] values)
   {
      openShiftTypeField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getHerokuApplicationNameField()
    */
   @Override
   public HasValue<String> getHerokuApplicationNameField()
   {
      return herokuNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getHerokuRepositoryNameField()
    */
   @Override
   public HasValue<String> getHerokuRepositoryNameField()
   {
      return herokuRepositoryNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getOpenShiftNameField()
    */
   @Override
   public HasValue<String> getOpenShiftNameField()
   {
      return openShiftNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.deploy.DeploySamplesPresenter.Display#getOpenShitfTypeSelectionField()
    */
   @Override
   public HasValue<String> getOpenShitfTypeSelectionField()
   {
      return openShiftTypeField;
   }
}
