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
package org.exoplatform.ide.client.permissions;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.module.groovy.Images;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 19, 2010 $
 *
 */
public class PermissionsManagerForm extends DialogWindow implements PermissionsManagerPresenter.Dispaly
{
   private static final String ID_SAVE = "ideSaveACL";

   private static final String ID_ADD_ENTITY = "ideAddEntity";

   private static final String ID_CANCEL = "ideCancelACL";
   
   private static int WIDTH = 350;

   private static int HEIGTH = 400;

   private static String ID = "idePermissionManager";
   
   private PermissionsManagerPresenter presenter;

   private PermissionsListGrid permissionsListGrid;

   private IButton cancelButton;

   private IButton saveACLButton;

   private IButton addEntityButton;
   
   private VLayout vLayout;
   
   public PermissionsManagerForm(HandlerManager eventBus, Item item)
   {
      super(eventBus, WIDTH, HEIGTH, ID);
      
      setTitle("Permissions");
      
      vLayout = new VLayout();
      vLayout.setHeight100();
      vLayout.setMargin(2);
      
      addItem(vLayout);
      addPermissionsListGrid();
      
      addButtonForm();
      
      show();
      
      presenter = new PermissionsManagerPresenter(eventBus, item);
      presenter.bindDisplay(this);
      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
      
   }

   /**
    * 
    */
   private void addPermissionsListGrid()
   {
      permissionsListGrid = new PermissionsListGrid();
      permissionsListGrid.setWidth100();
      permissionsListGrid.setHeight100();
      vLayout.addMember(permissionsListGrid);
      
   }

   /**
    * 
    */
   private void addButtonForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setMargin(3);
      buttonsForm.setPadding(3);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      addEntityButton = new IButton("Add");
      addEntityButton.setWidth(90);
      addEntityButton.setHeight(22);
      addEntityButton.setID(ID_ADD_ENTITY);

      addEntityButton.setIcon(Images.Buttons.URL);

      saveACLButton = new IButton("Save");
      saveACLButton.setWidth(90);
      saveACLButton.setHeight(22);
      saveACLButton.setIcon(Images.Buttons.YES);
//      saveACLButton.setDisabled(true);
      saveACLButton.setID(ID_SAVE);

      cancelButton = new IButton("Cancel");
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);
      cancelButton.setID(ID_CANCEL);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      StatefulCanvas delimiter2 = new StatefulCanvas();
      delimiter2.setWidth(3);
      tbi.setButtons(addEntityButton, delimiter1, saveACLButton, delimiter2, cancelButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();
      vLayout.addMember(buttonsForm);
   }

   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }
   
   /**
    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Dispaly#closeForm()
    */
   public void closeForm()
   {
     destroy();      
   }

   /**
    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Dispaly#getPermissionsListGrid()
    */
   public PermissionsListGrid getPermissionsListGrid()
   {
      return permissionsListGrid;
   }

   /**
    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Dispaly#getAddEntityButton()
    */
   public HasClickHandlers getAddEntityButton()
   {
      return addEntityButton;
   }

   /**
    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Dispaly#getSaveACLButton()
    */
   public HasClickHandlers getSaveACLButton()
   {
      return saveACLButton;
   }

   /**
    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Dispaly#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }
}
