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
package org.exoplatform.ide.extension.cloudfoundry.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
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

   interface CloudFoundryProjectViewImplUiBinder extends UiBinder<Widget, CloudFoundryProjectViewImpl>
   {
   }

   private CloudFoundryProjectView.ActionDelegate delegate;

   @Inject
   public CloudFoundryProjectViewImpl()
   {
      Widget widget = uiBinder.createAndBindUi(this);

      this.setText("CloudFoundry Project");
      this.setWidget(widget);
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
      this.stack.setText(status);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setStartButtonEnabled(boolean enabled)
   {
      btnStart.setEnabled(enabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setStopButtonEnabled(boolean enabled)
   {
      btnStop.setEnabled(enabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setRestartButtonEnabled(boolean enabled)
   {
      btnRestart.setEnabled(enabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      this.hide();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void showDialog()
   {
      this.center();
      this.show();
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