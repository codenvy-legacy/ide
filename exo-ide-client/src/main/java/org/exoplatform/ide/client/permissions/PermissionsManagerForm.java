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
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.acl.AccessControlEntry;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 *This class represent form for managing permissions.<br>
 *Its contains:
 *<ul>
 *<li> {@link TextField} - name of {@link Item}
 *<li> {@link TextField} - owner of {@link Item} 
 *<li> {@link PermissionsListGrid} - list of all permission for {@link Item}
 *<li> several {@link IButton} (Add, Remove, Save and Cancel)
 *</ul>
 * Created by The eXo Platform SAS .
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 19, 2010 $
 *
 */
public class PermissionsManagerForm extends DialogWindow implements PermissionsManagerPresenter.Dispaly
{
   private static final String ID_SAVE = "ideSaveACL";

   private static final String ID_ADD_ENTITY = "ideAddEntity";

   private static final String ID_CANCEL = "ideCancelACL";

   private static int WIDTH =450;

   private static int HEIGTH = 370;

   private static String ID = "idePermissionManager";

   private PermissionsManagerPresenter presenter;

   private PermissionsListGrid permissionsListGrid;

   private IButton cancelButton;

   private IButton saveACLButton;

   private IButton addEntityButton;

   private VLayout vLayout;

   private TextField itemNameField;

   private TextField itemOwnerField;

   private IButton removeButton;

   public PermissionsManagerForm(HandlerManager eventBus, Item item)
   {
      super(eventBus, WIDTH, HEIGTH, ID);

      setTitle("Permissions");

      vLayout = new VLayout();
      vLayout.setHeight100();
      vLayout.setMargin(15);
      vLayout.setAlign(Alignment.CENTER);

      addItem(vLayout);

      addFileInfo();
      addPermissionsListGrid();
      
      addButtonForm();
      setMargin(5);
      show();

      UIHelper.setAsReadOnly(itemNameField.getName());
      UIHelper.setAsReadOnly(itemOwnerField.getName());
      
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
   private void addFileInfo()
   {
      itemNameField = new TextField();
      itemNameField.setTitle("Name");
      itemNameField.setHeight(20);
      itemNameField.setWidth(320);
      itemNameField.setTitleAlign(Alignment.LEFT);

      DynamicForm formName = new DynamicForm();
      formName.setWidth100();
      formName.setLayoutAlign(Alignment.LEFT);
      formName.setFields(itemNameField);
      formName.setHeight(28);

      itemOwnerField = new TextField("Owner");
      itemOwnerField.setHeight(20);
      itemOwnerField.setWidth(320);
      itemOwnerField.setTitleAlign(Alignment.LEFT);
      
      
      DynamicForm formOwner = new DynamicForm();
      formOwner.setWidth100();
      formOwner.setLayoutAlign(Alignment.LEFT);
      formOwner.setFields(itemOwnerField);
      formOwner.setHeight(28);

      vLayout.addMember(formName);
      vLayout.addMember(formOwner);
   }

   /**
    * 
    */
   private void addPermissionsListGrid()
   {
      DynamicForm form = new DynamicForm();
      form.setGroupTitle("Permissions:");
      form.setIsGroup(true);
      form.setWidth(392);
      form.setPadding(0);
      form.setMargin(0);
      form.setHeight100();
      form.setLayoutAlign(Alignment.CENTER);
      form.setLayoutAlign(VerticalAlignment.CENTER);
      
      permissionsListGrid = new PermissionsListGrid();
      permissionsListGrid.setWidth(384);
      permissionsListGrid.setHeight(140);
//      permissionsListGrid.setMargin(5);

      CanvasItem cItem = new CanvasItem();

      cItem.setCanvas(permissionsListGrid);
      cItem.setShowTitle(false);
      cItem.setHeight(150);
      cItem.setWidth("300");
      
      addEntityButton = new IButton("Add");
      addEntityButton.setWidth(90);
      addEntityButton.setHeight(22);
      addEntityButton.setID(ID_ADD_ENTITY);
      addEntityButton.setIcon(Images.Buttons.ADD);

      removeButton = new IButton("Remove");
      removeButton.setWidth(90);
      removeButton.setHeight(22);
      removeButton.setIcon(Images.Buttons.DELETE);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      StatefulCanvas delimiter2 = new StatefulCanvas();
      delimiter2.setWidth(94);
      tbi.setButtons(delimiter2,addEntityButton, delimiter1, removeButton);
      tbi.setAlign(Alignment.RIGHT);
      tbi.setHeight(30);
            
      form.setFields( cItem, tbi);

      vLayout.addMember(form);

   }

   /**
    * 
    */
   private void addButtonForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setMargin(5);
      buttonsForm.setPadding(3);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      saveACLButton = new IButton("Save");
      saveACLButton.setWidth(90);
      saveACLButton.setHeight(22);
      saveACLButton.setIcon(Images.Buttons.YES);
      saveACLButton.setID(ID_SAVE);

      cancelButton = new IButton("Cancel");
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);
      cancelButton.setID(ID_CANCEL);

      ToolbarItem tbi = new ToolbarItem();
      
      StatefulCanvas delimiter2 = new StatefulCanvas();
      delimiter2.setWidth(3);
      tbi.setButtons(saveACLButton, delimiter2, cancelButton);
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

   /**
    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Dispaly#getFileNameLabel()
    */
   public HasValue<String> getFileNameLabel()
   {
      return itemNameField;
   }

   /**
    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Dispaly#getFileOwnerLabel()
    */
   public HasValue<String> getFileOwnerLabel()
   {
      return itemOwnerField;
   }

   /**
    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Dispaly#getRemoveEntityButton()
    */
   public HasClickHandlers getRemoveEntityButton()
   {
      return removeButton;
   }

   /**
    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Dispaly#selectItem(org.exoplatform.ide.client.framework.vfs.acl.AccessControlEntry)
    */
   public void selectItem(AccessControlEntry item)
   {
      permissionsListGrid.selectItem(item);
   }
}
