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
package com.codenvy.ide.extension.cloudfoundry.client.info;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

/**
 * The implementation of {@link ApplicationInfoView}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ApplicationInfoViewImpl extends DialogBox implements ApplicationInfoView
{
   private static ApplicationInfoViewImplUiBinder uiBinder = GWT.create(ApplicationInfoViewImplUiBinder.class);

   @UiField
   Button btnOk;

   @UiField(provided = true)
   CellTable<String> urisTable = new CellTable<String>();

   @UiField(provided = true)
   CellTable<String> environmentsTable = new CellTable<String>();

   @UiField(provided = true)
   CellTable<String> servicesTable = new CellTable<String>();

   @UiField
   Label name;

   @UiField
   Label state;

   @UiField
   Label instances;

   @UiField
   Label version;

   @UiField
   Label resourceDisk;

   @UiField
   Label memory;

   @UiField
   Label model;

   @UiField
   Label stack;

   @UiField
   DockLayoutPanel statePanel;

   @UiField
   DockLayoutPanel versionPanel;

   @UiField
   DockLayoutPanel memoryPanel;

   @UiField
   DockLayoutPanel stackPanel;

   interface ApplicationInfoViewImplUiBinder extends UiBinder<Widget, ApplicationInfoViewImpl>
   {
   }

   private ApplicationInfoView.ActionDelegate delegate;

   /**
    * Create view.
    * 
    * @param constants
    * @param resources
    */
   @Inject
   protected ApplicationInfoViewImpl(CloudFoundryLocalizationConstant constants, CloudFoundryResources resources)
   {
      createCellTable(urisTable, "URIs");
      createCellTable(servicesTable, "Services");
      createCellTable(environmentsTable, "Environments");

      Widget widget = uiBinder.createAndBindUi(this);

      this.setText("Application Info");
      this.setWidget(widget);

      this.addStyleName(resources.cloudFoundryCss().appInfo());
      statePanel.addStyleName(resources.cloudFoundryCss().event());
      versionPanel.addStyleName(resources.cloudFoundryCss().event());
      memoryPanel.addStyleName(resources.cloudFoundryCss().event());
      stackPanel.addStyleName(resources.cloudFoundryCss().event());

      btnOk.setHTML(new Image(resources.okButton()) + " " + constants.okButton());
   }

   /**
    * Creates table.
    * 
    * @param table
    * @param header
    */
   private void createCellTable(CellTable<String> table, String header)
   {
      Column<String, String> column = new Column<String, String>(new TextCell())
      {
         @Override
         public String getValue(String object)
         {
            return object;
         }
      };

      table.addColumn(column, header);
      table.setColumnWidth(column, "100%");

      // don't show loading indicator
      table.setLoadingIndicator(null);
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
   public void setName(String name)
   {
      this.name.setText(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setState(String state)
   {
      this.state.setText(state);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setInstances(String instances)
   {
      this.instances.setText(instances);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setVersion(String version)
   {
      this.version.setText(version);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDisk(String disk)
   {
      this.resourceDisk.setText(disk);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setMemory(String memory)
   {
      this.memory.setText(memory);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setStack(String stack)
   {
      this.stack.setText(stack);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setModel(String model)
   {
      this.model.setText(model);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationUris(List<String> applications)
   {
      setItemsIntoCellTable(applications, urisTable);
   }

   /**
    * Sets items into selected table.
    * 
    * @param items
    * @param table
    */
   private void setItemsIntoCellTable(List<String> items, CellTable<String> table)
   {
      table.setRowData(items);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationServices(List<String> services)
   {
      setItemsIntoCellTable(services, servicesTable);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setApplicationEnvironments(List<String> environments)
   {
      setItemsIntoCellTable(environments, environmentsTable);
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

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      this.hide();
   }

   @UiHandler("btnOk")
   void onBtnOkClick(ClickEvent event)
   {
      delegate.onOKClicked();
   }
}