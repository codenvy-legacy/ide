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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.acl.AccessControlEntry;
import org.exoplatform.ide.client.framework.vfs.acl.AccessControlList;
import org.exoplatform.ide.client.framework.vfs.acl.Permissions;
import org.exoplatform.ide.client.framework.vfs.event.SetACLResultReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.SetACLResultReceivedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 *This class is presenter for {@link PermissionsManagerForm}<br>
 * Created by The eXo Platform SAS .
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 19, 2010 $
 *
 */
public class PermissionsManagerPresenter implements SetACLResultReceivedHandler, ExceptionThrownHandler
{

   public interface Dispaly
   {

      void closeForm();

      PermissionsListGrid getPermissionsListGrid();

      HasClickHandlers getAddEntityButton();

      HasClickHandlers getSaveACLButton();

      HasClickHandlers getRemoveEntityButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getFileNameLabel();

      HasValue<String> getFileOwnerLabel();

      void selectItem(AccessControlEntry item);

   }

   private HandlerManager eventBus;

   private Item item;

   private Dispaly dispaly;

   private Handlers handlers;

   private AccessControlList acl;

   private AccessControlEntry selectedEntry;
   
   private Map<String, String> lockTokens;

   /**
    * @param eventBus
    * @param item
    */
   public PermissionsManagerPresenter(HandlerManager eventBus, Item item, Map<String, String> lockTokens)
   {
      this.eventBus = eventBus;
      this.item = item;
      this.lockTokens = lockTokens;

      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Dispaly d)
   {
      dispaly = d;

      try
      {
         acl = parseItemACL();

         dispaly.getPermissionsListGrid().setValue(acl.getPermissionsList());

         dispaly.getFileNameLabel().setValue(item.getName());
         dispaly.getFileOwnerLabel().setValue(getFileOwner());

      }
      catch (Exception e)
      {
         GWT.log(e.getMessage());
         Dialogs.getInstance().showError(e.getMessage());
         return;
      }

      dispaly.getCancelButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            dispaly.closeForm();
         }
      });

      dispaly.getAddEntityButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            acl.addPermission(new AccessControlEntry(""));
            dispaly.getPermissionsListGrid().setValue(acl.getPermissionsList());
            dispaly.getPermissionsListGrid().startEditing(acl.getPermissionsList().size() - 1, 0, false);

         }
      });

      dispaly.getSaveACLButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            saveACL();
         }
      });

      dispaly.getPermissionsListGrid().addSelectionHandler(new SelectionHandler<AccessControlEntry>()
      {

         public void onSelection(SelectionEvent<AccessControlEntry> event)
         {
            selectedEntry = event.getSelectedItem();

         }
      });

      dispaly.getRemoveEntityButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            removeSelectedPermission();
         }

      });

      handlers.addHandler(SetACLResultReceivedEvent.TYPE, this);
   }

   /**
    * @return owner of item
    */
   private String getFileOwner()
   {
      return item.getProperty(ItemProperty.OWNER).getChildProperty(ItemProperty.ACL.HREF).getValue();
   }

   private void saveACL()
   {
      acl.removeEmptyPermissions();
      VirtualFileSystem.getInstance().setACL(item, acl, lockTokens.get(item.getHref()));
   }

   /**
    * 
    */
   public void destroy()
   {
      handlers.removeHandlers();
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.SetACLResultReceivedHandler#onSetACLResultReceived(org.exoplatform.ide.client.framework.vfs.event.SetACLResultReceivedEvent)
    */
   public void onSetACLResultReceived(SetACLResultReceivedEvent event)
   {
      dispaly.closeForm();
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      dispaly.closeForm();
   }

   private void removeSelectedPermission()
   {
      if (selectedEntry == null)
         return;

      int i = acl.getPermissionsList().indexOf(selectedEntry);
      acl.removePermission(selectedEntry.getIdentity());
      dispaly.getPermissionsListGrid().setValue(acl.getPermissionsList());
      if (i > acl.getPermissionsList().size() - 1)
      {
         i = acl.getPermissionsList().size() - 1;
      }

      if (!acl.getPermissionsList().isEmpty())
      {
         AccessControlEntry itemToSelect = acl.getPermissionsList().get(i);

         dispaly.selectItem(itemToSelect);
      }
   }

   private AccessControlList parseItemACL() throws Exception
   {

      AccessControlList accessControlList = new AccessControlList();

      Property acl = item.getProperty(ItemProperty.ACL.ACL);

      if (acl == null)
      {
         throw new Exception("No acl property");
      }

      for (Property aceProperty : acl.getChildProperties())
      {

         String entity = "";
         List<Permissions> permissionList = new ArrayList<Permissions>();

         for (Property p : aceProperty.getChildProperties())
         {
            if (p.getName().equals(ItemProperty.ACL.PRINCIPAL))
            {
               // parse principal
               entity = getEntity(p.getChildProperties());
            }
            else if (p.getName().equals(ItemProperty.ACL.GRANT))
            {
               //parse grant
               permissionList = getPermission(p.getChildProperties());
            }
         }

         accessControlList.addPermission(new AccessControlEntry(entity, permissionList));
      }

      return accessControlList;
   }

   /**
    * @param childProperties
    * @return
    */
   private List<Permissions> getPermission(Collection<Property> childProperties)
   {
      List<Permissions> permissions = new ArrayList<Permissions>();

      for (Property p : childProperties)
      {
         if (p.getName().equals(ItemProperty.ACL.PRIVILEGE))
         {
            for (Property per : p.getChildProperties())
            {
               if (per.getName().getLocalName().equals(Permissions.READ.toString()))
               {
                  permissions.add(Permissions.READ);
               }
               else if (per.getName().getLocalName().equals(Permissions.WRITE.toString()))
                  permissions.add(Permissions.WRITE);
            }
         }
      }

      return permissions;
   }

   /**
    * @param childProperties
    * @return userID
    */
   private String getEntity(Collection<Property> childProperties)
   {
      for (Property p : childProperties)
      {
         if (p.getName().equals(ItemProperty.ACL.HREF))
            return p.getValue();
         else if (p.getName().equals(ItemProperty.ACL.PROPERTY))
            return p.getChildProperty(ItemProperty.OWNER).getName().getLocalName();
         else if (p.getName().equals(ItemProperty.ACL.ALL))
            return p.getName().getLocalName();
      }
      return "";
   }

}
