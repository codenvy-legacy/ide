//TODO: need rework according new VFS
///*
// * Copyright (C) 2010 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.exoplatform.ide.client.permissions;
//
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.event.dom.client.HasClickHandlers;
//import com.google.gwt.uibinder.client.UiBinder;
//import com.google.gwt.uibinder.client.UiField;
//import com.google.gwt.user.client.ui.HasValue;
//import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.user.client.ui.Widget;
//
//import org.exoplatform.gwtframework.ui.client.component.ImageButton;
//import org.exoplatform.gwtframework.ui.client.component.TextField;
//import org.exoplatform.gwtframework.ui.client.util.UIHelper;
//import org.exoplatform.ide.client.IDE;
//import org.exoplatform.ide.client.IDEImageBundle;
//import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
//import org.exoplatform.ide.client.framework.vfs.Item;
//import org.exoplatform.ide.client.framework.vfs.acl.AccessControlEntry;
//
///**
// *This class represent form for managing permissions.<br>
// *Its contains:
// *<ul>
// *<li> {@link TextField} - name of {@link Item}
// *<li> {@link TextField} - owner of {@link Item} 
// *<li> {@link PermissionsListGrid} - list of all permission for {@link Item}
// *<li> several {@link ImageButton} (Add, Remove, Save and Cancel)
// *</ul>
// * Created by The eXo Platform SAS .
// * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
// * @version $Id: Oct 19, 2010 $
// *
// */
//public class PermissionsManagerView extends ViewImpl implements PermissionsManagerPresenter.Display
//{
//   
//   private static final String ID_SAVE = "ideSaveACL";
//
//   private static final String ID_ADD_ENTITY = "ideAddEntity";
//
//   private static final String ID_CANCEL = "ideCancelACL";
//
//   private static int WIDTH = 450;
//
//   private static int HEIGHT = 370;
//
//   private static String ID = "idePermissionManager";
//
//   @UiField
//   PermissionsListGrid permissionsListGrid;
//
//   @UiField
//   ImageButton cancelButton;
//
//   @UiField
//   ImageButton saveACLButton;
//
//   @UiField
//   ImageButton addEntityButton;
//
//   @UiField
//   ImageButton removeButton;
//   
//   @UiField
//   TextField itemNameField;
//
//   @UiField
//   TextField itemOwnerField;
//   
//   private static final String TITLE = IDE.PERMISSIONS_CONSTANT.permissionsTitle();
//   
//   interface PermissionsManagerViewUiBinder extends UiBinder<Widget, PermissionsManagerView>
//   {
//   }
//   
//   private static PermissionsManagerViewUiBinder uiBinder = GWT.create(PermissionsManagerViewUiBinder.class);
//
//   public PermissionsManagerView()
//   {
//      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT);
//      add(uiBinder.createAndBindUi(this));
//      
//      UIHelper.setAsReadOnly(itemNameField.getName());
//      UIHelper.setAsReadOnly(itemOwnerField.getName());
//      
//      saveACLButton.setId(ID_SAVE);
//      addEntityButton.setId(ID_ADD_ENTITY);
//      cancelButton.setId(ID_CANCEL);
//   }
//
//    /**
//    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Display#getPermissionsListGrid()
//    */
//   public PermissionsListGrid getPermissionsListGrid()
//   {
//      return permissionsListGrid;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Display#getAddEntityButton()
//    */
//   public HasClickHandlers getAddEntityButton()
//   {
//      return addEntityButton;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Display#getSaveACLButton()
//    */
//   public HasClickHandlers getSaveACLButton()
//   {
//      return saveACLButton;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Display#getCancelButton()
//    */
//   public HasClickHandlers getCancelButton()
//   {
//      return cancelButton;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Display#getFileNameLabel()
//    */
//   public HasValue<String> getFileNameLabel()
//   {
//      return itemNameField;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Display#getFileOwnerLabel()
//    */
//   public HasValue<String> getFileOwnerLabel()
//   {
//      return itemOwnerField;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Display#getRemoveEntityButton()
//    */
//   public HasClickHandlers getRemoveEntityButton()
//   {
//      return removeButton;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.permissions.PermissionsManagerPresenter.Display#selectItem(org.exoplatform.ide.client.framework.vfs.acl.AccessControlEntry)
//    */
//   public void selectItem(AccessControlEntry item)
//   {
//      permissionsListGrid.selectItem(item);
//   }
//}
