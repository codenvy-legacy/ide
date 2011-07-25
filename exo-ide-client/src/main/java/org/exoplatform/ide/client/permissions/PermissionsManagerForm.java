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

import java.util.Map;

import org.exoplatform.gwtframework.ui.client.component.DynamicForm;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.IDEDialogWindow;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.acl.AccessControlEntry;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *This class represent form for managing permissions.<br>
 *Its contains:
 *<ul>
 *<li> {@link TextField} - name of {@link Item}
 *<li> {@link TextField} - owner of {@link Item} 
 *<li> {@link PermissionsListGrid} - list of all permission for {@link Item}
 *<li> several {@link ImageButton} (Add, Remove, Save and Cancel)
 *</ul>
 * Created by The eXo Platform SAS .
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 19, 2010 $
 *
 */
public class PermissionsManagerForm extends IDEDialogWindow implements PermissionsManagerPresenter.Dispaly
{
   
   private static final String ID_SAVE = "ideSaveACL";

   private static final String ID_ADD_ENTITY = "ideAddEntity";

   private static final String ID_CANCEL = "ideCancelACL";

   private static int WIDTH = 450;

   private static int HEIGTH = 370;

   private static String ID = "idePermissionManager";

   private PermissionsManagerPresenter presenter;

   private PermissionsListGrid permissionsListGrid;

   private ImageButton cancelButton;

   private ImageButton saveACLButton;

   private ImageButton addEntityButton;

   private VerticalPanel vLayout;

   private TextField itemNameField;

   private TextField itemOwnerField;

   private ImageButton removeButton;
   
   private static final String TITLE = IDE.PERMISSIONS_CONSTANT.permissionsTitle();
   
   private static final String NAME = IDE.PERMISSIONS_CONSTANT.permissionsName();
   
   private static final String OWNER = IDE.PERMISSIONS_CONSTANT.permissionsOwner();

   public PermissionsManagerForm(HandlerManager eventBus, Item item, Map<String, String> lockTokens)
   {
      super(WIDTH, HEIGTH, ID);

      setTitle(TITLE);

      vLayout = new VerticalPanel();
      vLayout.setHeight("100%");
      vLayout.setSpacing(5);
      //vLayout.setT(Align.CENTER);

      setWidget(vLayout);

      addFileInfo();
      addPermissionsListGrid();

      addButtonForm();
      //setMargin(5);
      show();

      UIHelper.setAsReadOnly(itemNameField.getName());
      UIHelper.setAsReadOnly(itemOwnerField.getName());

      presenter = new PermissionsManagerPresenter(eventBus, item, lockTokens);
      presenter.bindDisplay(this);
   }

   /**
    * 
    */
   private void addFileInfo()
   {
      itemNameField = new TextField();
      itemNameField.setTitle(NAME);
      itemNameField.setHeight(20);
      itemNameField.setWidth(320);
      //TODO fix form
     // itemNameField.setTitleAlign(Alignment.LEFT);

      DynamicForm formName = new DynamicForm();
      formName.setWidth("100%");
     // formName.setLayoutAlign(Alignment.LEFT);
      formName.add(itemNameField);
      formName.setHeight(28);

      itemOwnerField = new TextField();
      itemOwnerField.setTitle(OWNER);
      itemOwnerField.setHeight(20);
      itemOwnerField.setWidth(320);
      //itemOwnerField.setTitleAlign(Alignment.LEFT);

      DynamicForm formOwner = new DynamicForm();
      formOwner.setWidth("100%");
   //   formOwner.setLayoutAlign(Alignment.LEFT);
      formOwner.add(itemOwnerField);
      formOwner.setHeight(28);

      vLayout.add(formName);
      vLayout.add(formOwner);
   }

   /**
    * 
    */
   private void addPermissionsListGrid()
   {
      DynamicForm form = new DynamicForm();
      //TODO 
     /* form.setGroupTitle("Permissions:");
      form.setIsGroup(true);*/
      form.setWidth(392);
      form.setPadding(0);
      form.setMargin(0);
      form.setHeight("100%");
   //   form.setLayoutAlign(Alignment.CENTER);
    //  form.setLayoutAlign(VerticalAlignment.CENTER);

      permissionsListGrid = new PermissionsListGrid();
      permissionsListGrid.setWidth(384);
      permissionsListGrid.setHeight(140);

      addEntityButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.addButton());
      addEntityButton.setWidth("90px");
      addEntityButton.setHeight("22px");
      addEntityButton.setButtonId(ID_ADD_ENTITY);
      addEntityButton.setImage(new Image(Images.Buttons.ADD));

      removeButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.deleteButton());
      removeButton.setWidth("90px");
      removeButton.setHeight("22px");
      removeButton.setImage(new Image(Images.Buttons.REMOVE));

      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setWidth("100%");
      buttonsLayout.setHeight("22px");
   //   buttonsLayout.setLayoutAlign(Alignment.CENTER);
      buttonsLayout.setSpacing(5);

      buttonsLayout.add(addEntityButton);
      buttonsLayout.add(removeButton);

      form.add(permissionsListGrid);

      vLayout.add(form);
      vLayout.add(buttonsLayout);
   }

   /**
    * 
    */
   private void addButtonForm()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setWidth("100%");
      buttonsLayout.setHeight("22px");
    //  buttonsLayout.setLayoutAlign(Alignment.CENTER);
      buttonsLayout.setSpacing(5);

      saveACLButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.saveButton());
      saveACLButton.setWidth("90px");
      saveACLButton.setHeight("22px");
      saveACLButton.setImage(new Image(Images.Buttons.YES));
      saveACLButton.setButtonId(ID_SAVE);

      cancelButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.cancelButton());
      cancelButton.setWidth("90px");
      cancelButton.setHeight("22px");
      cancelButton.setImage(new Image(Images.Buttons.NO));
      cancelButton.setButtonId(ID_CANCEL);
      
      buttonsLayout.add(saveACLButton);
      buttonsLayout.add(cancelButton);
      
      vLayout.add(buttonsLayout);
   }

   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
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
