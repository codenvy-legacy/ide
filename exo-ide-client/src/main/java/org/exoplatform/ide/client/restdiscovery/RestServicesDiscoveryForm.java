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

import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
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
   private static int WIDTH = 600;

   private static int HEIGTH = 370;

   private static String ID = "ideRestServiceDiscovery";

   private static String ID_OK = "ideRestServiceDiscoveryOkButton";

//   private RestServiceListGrid listGrid;
   
   private RestServiceTreeGrid treeGrid;

   private IButton okButton;

   private VLayout vLayout;

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
      vLayout.setMargin(15);
      vLayout.setAlign(Alignment.CENTER);
      setTitle("REST Services Discovery");
      
      addItem(vLayout);

      createGrid();

      createButtons();
      show();
      redraw();

      addCloseClickHandler(new CloseClickHandler()
      {

         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
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
   private void createGrid()
   {
//      listGrid = new RestServiceListGrid();
//      listGrid.setWidth100();
//      listGrid.setHeight100();
//
//      vLayout.addMember(listGrid);
      treeGrid = new RestServiceTreeGrid();
      treeGrid.setWidth100();
      treeGrid.setHeight100();
      
      vLayout.addMember(treeGrid);

   }

   /**
    * 
    */
   private void createButtons()
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
      vLayout.addMember(buttonsForm);
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

}
