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
package org.exoplatform.ide.client.restdiscovery;

import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 22, 2010 9:39:09 AM evgen $
 *
 */
public class RestServicesDiscoveryForm extends DialogWindow implements RestServicesDiscoveryPresenter.Display
{
   private static int WIDTH = 650;

   private static int HEIGTH = 370;

   private static String ID = "ideRestServiceDiscovery";

   private static String ID_OK = "ideRestServiceDiscoveryOkButton";

   //   private RestServiceListGrid listGrid;

   private RestServiceTreeGrid treeGrid;

   private IButton okButton;

   private VLayout vLayout;

   private HLayout hLayout;

   private StaticTextItem requestType;

   private StaticTextItem responseType;

   private RestServiceParameterListGrid parameters;

   /**
    * @param eventBus
    * @param width
    * @param height
    * @param id
    */
   public RestServicesDiscoveryForm(HandlerManager eventBus)
   {
      super(eventBus, WIDTH, HEIGTH, ID);
      vLayout = new VLayout();
      vLayout.setHeight100();
      vLayout.setMargin(10);
      vLayout.setAlign(Alignment.CENTER);
      setTitle("REST Services Discovery");

      hLayout = new HLayout();
      hLayout.setWidth100();
      hLayout.setHeight100();
      addItem(vLayout);

      vLayout.addMember(hLayout);

      createGrid(hLayout);
      createButtons(vLayout);
      createInfoForm(hLayout);
      show();
      //      redraw();

      addCloseClickHandler(new CloseClickHandler()
      {

         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
   }

   /**
    * @param hLayout2
    */
   private void createInfoForm(Layout layout)
   {
      VLayout vL = new VLayout();
      vL.setMembersMargin(3);
      vL.setShowEdges(true);
      vL.setEdgeSize(1);
      DynamicForm form = new DynamicForm();
      form.setID("ideRestServiceDiscoveryForm");
      form.setAlign(Alignment.LEFT);
      form.setWidth100();
      form.setHeight(20);
      form.setPadding(5);
      form.setTitleWidth(20);

      requestType = new StaticTextItem();
      requestType.setTitle("<nobr>Request media type</nobr>");

      responseType = new StaticTextItem();
      responseType.setTitle("<nobr>Response media type<nobr>");

      parameters = new RestServiceParameterListGrid();
      parameters.setID("ideRestServiceDiscoveryParameters");
      parameters.setWidth100();
      parameters.setHeight100();
      parameters.setShowEdges(false);
      parameters.setMargin(3);

      form.setFields(requestType, responseType);
      vL.addMember(form);
      vL.addMember(parameters);
      layout.addMember(vL);

   }

   /**
    * @see com.smartgwt.client.widgets.BaseWidget#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      eventBus.fireEvent(new ViewClosedEvent(ID));
      super.onDestroy();
   }

   /**
    * 
    */
   private void createGrid(Layout layout)
   {
      treeGrid = new RestServiceTreeGrid();
      treeGrid.setShowResizeBar(true);
      //      treeGrid.setWidth(250);
      treeGrid.setWidth("40%");
      treeGrid.setHeight100();

      layout.addMember(treeGrid);

   }

   /**
    * 
    */
   private void createButtons(Layout layout)
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setMargin(5);
      buttonsForm.setPadding(3);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      okButton = new IButton("Ok");
      okButton.setWidth(90);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.YES);
      okButton.setID(ID_OK);

      ToolbarItem tbi = new ToolbarItem();

      tbi.setButtons(okButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();
      layout.addMember(buttonsForm);
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getOkButton()
    */
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#closeView()
    */
   public void closeView()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getTreeGrid()
    */
   public UntypedTreeGrid getTreeGrid()
   {
      return treeGrid;
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getRequestField()
    */
   public HasValue<String> getRequestField()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getResponseField()
    */
   public HasValue<String> getResponseField()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setRequestType(java.lang.String)
    */
   public void setRequestType(String value)
   {
      requestType.setValue(value);
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setResponseType(java.lang.String)
    */
   public void setResponseType(String value)
   {
      responseType.setValue(value);
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getParametersListGrid()
    */
   public ListGridItem<Param> getParametersListGrid()
   {
      return parameters;
   }

}
