/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.cloudfoundry.client.project;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link CloudFoundryProjectView}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudFoundryProjectViewImpl extends DialogBox implements CloudFoundryProjectView
{
   private static CloudFoundryProjectViewImplUiBinder uiBinder = GWT.create(CloudFoundryProjectViewImplUiBinder.class);

   @UiField
   TextBox applicationName;

   @UiField
   Button btnInfo;

   @UiField
   Anchor url;

   @UiField
   Button btnEditUrl;

   @UiField
   TextBox memory;

   @UiField
   Button btnEditMemory;

   @UiField
   TextBox instances;

   @UiField
   Button btnEditInstances;

   @UiField
   Label stack;

   @UiField
   Label model;

   @UiField
   Label status;

   @UiField
   Button btnStart;

   @UiField
   Button btnStop;

   @UiField
   Button btnRestart;

   @UiField
   Button btnUpdate;

   @UiField
   Button btnDelete;

   @UiField
   Button btnServices;

   @UiField
   Button btnLogs;

   @UiField
   Button btnClose;

   @UiField
   Label applicationLabel;

   @UiField
   Label urlLabel;

   @UiField
   Label memoryLabel;

   @UiField
   Label instanceLabel;

   @UiField
   Label stackLabel;

   @UiField
   Label modelLabel;

   @UiField
   Label statusLabel;

   @UiField
   Label actionLabel;

   interface CloudFoundryProjectViewImplUiBinder extends UiBinder<Widget, CloudFoundryProjectViewImpl>
   {
   }

   private CloudFoundryProjectView.ActionDelegate delegate;

   private boolean isDisplayed;

   /**
    * Create view.
    * 
    * @param resources
    * @param constants
    */
   @Inject
   protected CloudFoundryProjectViewImpl(CloudFoundryResources resources, CloudFoundryLocalizationConstant constants)
   {
      Widget widget = uiBinder.createAndBindUi(this);

      this.setText("CloudFoundry Project");
      this.setWidget(widget);

      this.addStyleName(resources.cloudFoundryCss().project());
      applicationLabel.addStyleName(resources.cloudFoundryCss().labelH());
      urlLabel.addStyleName(resources.cloudFoundryCss().labelH());
      memoryLabel.addStyleName(resources.cloudFoundryCss().labelH());
      instanceLabel.addStyleName(resources.cloudFoundryCss().labelH());
      stackLabel.addStyleName(resources.cloudFoundryCss().labelH());
      modelLabel.addStyleName(resources.cloudFoundryCss().labelH());
      statusLabel.addStyleName(resources.cloudFoundryCss().labelH());
      actionLabel.addStyleName(resources.cloudFoundryCss().labelH());
      url.addStyleName(resources.cloudFoundryCss().link());
      applicationName.addStyleName(resources.cloudFoundryCss().textinput());
      memory.addStyleName(resources.cloudFoundryCss().textinput());
      instances.addStyleName(resources.cloudFoundryCss().textinput());

      btnInfo.setHTML(new Image(resources.propertiesButton()).toString());
      btnEditMemory.setHTML(new Image(resources.editButton()).toString());
      btnEditInstances.setHTML(new Image(resources.editButton()).toString());
      btnStart.setHTML(new Image(resources.startApp()).toString());
      btnStop.setHTML(new Image(resources.stopApp()).toString());
      btnRestart.setHTML(new Image(resources.restartApp()).toString());
      btnClose.setHTML(new Image(resources.cancelButton()) + " " + constants.closeButton());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getApplicationName()
   {
      return applicationName.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationName(String name)
   {
      applicationName.setText(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getApplicationModel()
   {
      return model.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationModel(String model)
   {
      this.model.setText(model);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getApplicationStack()
   {
      return stack.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationStack(String stack)
   {
      this.stack.setText(stack);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getApplicationInstances()
   {
      return instances.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationInstances(String instances)
   {
      this.instances.setText(instances);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getApplicationMemory()
   {
      return memory.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationMemory(String memory)
   {
      this.memory.setText(memory);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getApplicationStatus()
   {
      return status.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationStatus(String status)
   {
      this.status.setText(status);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setEnabledStartButton(boolean enabled)
   {
      btnStart.setEnabled(enabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setEnabledStopButton(boolean enabled)
   {
      btnStop.setEnabled(enabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setEnabledRestartButton(boolean enabled)
   {
      btnRestart.setEnabled(enabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getApplicationUrl()
   {
      return url.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationUrl(String url)
   {
      this.url.setText(url);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      this.isDisplayed = false;
      this.hide();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void showDialog()
   {
      this.isDisplayed = true;
      this.center();
      this.show();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isShown()
   {
      return isDisplayed;
   }

   @UiHandler("btnInfo")
   void onBtnInfoClick(ClickEvent event)
   {
      delegate.onInfoClicked();
   }

   @UiHandler("btnEditUrl")
   void onBtnEditUrlClick(ClickEvent event)
   {
      delegate.onEditUrlClicked();
   }

   @UiHandler("btnEditMemory")
   void onBtnEditMemoryClick(ClickEvent event)
   {
      delegate.onEditMemoryClicked();
   }

   @UiHandler("btnEditInstances")
   void onBtnEditInstancesClick(ClickEvent event)
   {
      delegate.onEditInstancesClicked();
   }

   @UiHandler("btnStart")
   void onBtnStartClick(ClickEvent event)
   {
      delegate.onStartClicked();
   }

   @UiHandler("btnStop")
   void onBtnStopClick(ClickEvent event)
   {
      delegate.onStopClicked();
   }

   @UiHandler("btnRestart")
   void onBtnRestartClick(ClickEvent event)
   {
      delegate.onRestartClicked();
   }

   @UiHandler("btnUpdate")
   void onBtnUpdateClick(ClickEvent event)
   {
      delegate.onUpdateClicked();
   }

   @UiHandler("btnDelete")
   void onBtnDeleteClick(ClickEvent event)
   {
      delegate.onDeleteClicked();
   }

   @UiHandler("btnServices")
   void onBtnServicesClick(ClickEvent event)
   {
      delegate.onServicesClicked();
   }

   @UiHandler("btnLogs")
   void onBtnLogsClick(ClickEvent event)
   {
      delegate.onLogsClicked();
   }

   @UiHandler("btnClose")
   void onBtnCloseClick(ClickEvent event)
   {
      delegate.onCloseClicked();
   }
}