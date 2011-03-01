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

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 22, 2010 9:39:09 AM evgen $
 *
 */
public class RestServicesDiscoveryForm extends DialogWindow implements RestServicesDiscoveryPresenter.Display
{
   private static int WIDTH = 670;

   private static int HEIGTH = 370;

   private static String ID = "ideRestServiceDiscovery";

   private static String ID_OK = "ideRestServiceDiscoveryOkButton";

   private RestServiceTreeGrid treeGrid;

   private IButton okButton;

   private VLayout vLayout;

   private HLayout hLayout;

   private TextField requestType;

   private TextField responseType;

   private TextField pathField;

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

      UIHelper.setAsReadOnly(requestType.getName());
      UIHelper.setAsReadOnly(responseType.getName());
      UIHelper.setAsReadOnly(pathField.getName());
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
      int width = 270;

      VLayout vL = new VLayout();
      vL.setMembersMargin(3);
      vL.setShowEdges(true);
      vL.setEdgeSize(1);
      vL.setOverflow(Overflow.AUTO);
      VerticalPanel form = new VerticalPanel();
      form.getElement().setAttribute("id", "ideRestServiceDiscoveryForm");
      form.setWidth("100%");
      form.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      form.setSpacing(3);

      pathField = createTextField("ideMethodPathField", "<nobr>Path</nobr>", width);

      requestType = createTextField("ideRequestType", "<nobr>Request media type</nobr>", width);

      responseType = createTextField("ideResponseType", "<nobr>Response media type<nobr>", width);

      parameters = new RestServiceParameterListGrid();
      parameters.setID("ideRestServiceDiscoveryParameters");
      parameters.setWidth100();
      parameters.setHeight100();
      parameters.setShowEdges(false);
      parameters.setMargin(3);
      parameters.setVisible(false);

      form.add(pathField);
      form.add(requestType);
      form.add(responseType);

      vL.addMember(form);
      vL.addMember(parameters);
      layout.addMember(vL);

   }

   private TextField createTextField(String name, String title, int width)
   {
      TextField textField = new TextField(name, title);
      textField.setWidth(width);
      textField.setHeight(20);
      textField.setShowDisabled(false);
      textField.setVisible(false);
      return textField;
   }

   /**
    * @see com.smartgwt.client.widgets.BaseWidget#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      super.onDestroy();
      eventBus.fireEvent(new ViewClosedEvent(ID));
   }

   /**
    * 
    */
   private void createGrid(Layout layout)
   {
      treeGrid = new RestServiceTreeGrid();
      treeGrid.setShowResizeBar(true);
      //      treeGrid.setWidth(250);
      treeGrid.setWidth("35%");
      treeGrid.setHeight100();
      layout.addMember(treeGrid);

   }

   /**
    * 
    */
   private void createButtons(Layout layout)
   {
      HLayout buttonsLayout = new HLayout();
      buttonsLayout.setAutoWidth();
      buttonsLayout.setHeight(22);
      buttonsLayout.setLayoutAlign(Alignment.CENTER);
      buttonsLayout.setMembersMargin(5);
      buttonsLayout.setMargin(10);

      okButton = new IButton("Ok");
      okButton.setWidth(90);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.YES);
      okButton.setID(ID_OK);

      buttonsLayout.addMember(okButton);

      layout.addMember(buttonsLayout);
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

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setResponseFieldVisible(boolean)
    */
   public void setResponseFieldVisible(boolean b)
   {
      responseType.setShowTitle(b);
      responseType.setVisible(b);
      UIHelper.setAsReadOnly(responseType.getName());
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setRequestFieldVisible(boolean)
    */
   public void setRequestFieldVisible(boolean b)
   {
      requestType.setShowTitle(b);
      requestType.setVisible(b);
      UIHelper.setAsReadOnly(requestType.getName());
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setParametersListGridVisible(boolean)
    */
   public void setParametersListGridVisible(boolean b)
   {
      parameters.setVisible(b);
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setParametersListGridEnabled(boolean)
    */
   public void setParametersListGridEnabled(boolean enabled)
   {
      if (enabled)
         parameters.enable();
      else
         parameters.disable();
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setResponseFieldEnabled(boolean)
    */
   public void setResponseFieldEnabled(boolean enabled)
   {
      UIHelper.setAsReadOnly(responseType.getName());
      responseType.setDisabled(!enabled);
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setRequestFieldEnabled(boolean)
    */
   public void setRequestFieldEnabled(boolean enabled)
   {
      //      requestType.setShowDisabled(enabled);
      requestType.setDisabled(!enabled);
      UIHelper.setAsReadOnly(requestType.getName());

   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#getPathField()
    */
   public HasValue<String> getPathField()
   {
      return pathField;
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter.Display#setPathFieldVisible(boolean)
    */
   public void setPathFieldVisible(boolean visible)
   {
      pathField.setVisible(visible);
      UIHelper.setAsReadOnly(pathField.getName());
   }
}
