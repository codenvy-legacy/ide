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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.acl.AccessControlEntry;
import org.exoplatform.ide.client.framework.vfs.acl.AccessControlList;
import org.exoplatform.ide.client.framework.vfs.acl.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *This class is presenter for {@link PermissionsManagerView}<br>
 * Created by The eXo Platform SAS .
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 19, 2010 $
 *
 */
public class PermissionsManagerPresenter implements ShowPermissionsHandler, ItemsSelectedHandler,
ApplicationSettingsReceivedHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {

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

   private Display display;

   private AccessControlList acl;

   private AccessControlEntry selectedEntry;
   
   private Map<String, String> lockTokens;

   /**
    * @param eventBus
    * @param item
    */
   public PermissionsManagerPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(ShowPermissionsEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      try
      {
         acl = parseItemACL();

         display.getPermissionsListGrid().setValue(acl.getPermissionsList());

         display.getFileNameLabel().setValue(item.getName());
         display.getFileOwnerLabel().setValue(getFileOwner());

      }
      catch (Exception e)
      {
         GWT.log(e.getMessage());
         Dialogs.getInstance().showError(e.getMessage());
         return;
      }

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getAddEntityButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            acl.addPermission(new AccessControlEntry(""));
            display.getPermissionsListGrid().setValue(acl.getPermissionsList());

         }
      });

      display.getSaveACLButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            saveACL();
         }
      });

      display.getPermissionsListGrid().addSelectionHandler(new SelectionHandler<AccessControlEntry>()
      {

         public void onSelection(SelectionEvent<AccessControlEntry> event)
         {
            selectedEntry = event.getSelectedItem();

         }
      });

      display.getRemoveEntityButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            removeSelectedPermission();
         }

      });

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
      VirtualFileSystem.getInstance().setACL(item, acl, lockTokens.get(item.getHref()), new AsyncRequestCallback<Item>()
      {
         
         @Override
         protected void onSuccess(Item result)
         {
            closeView();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exception, IDE.PERMISSIONS_CONSTANT.permissionsSetAclFailure()));
            closeView();            
         }
      });
   }

   /**
    * 
    */
   public void destroy()
   {
   }

   private void removeSelectedPermission()
   {
      if (selectedEntry == null)
         return;

      int i = acl.getPermissionsList().indexOf(selectedEntry);
      acl.removePermission(selectedEntry.getIdentity());
      display.getPermissionsListGrid().setValue(acl.getPermissionsList());
      if (i > acl.getPermissionsList().size() - 1)
      {
         i = acl.getPermissionsList().size() - 1;
      }

      if (!acl.getPermissionsList().isEmpty())
      {
         AccessControlEntry itemToSelect = acl.getPermissionsList().get(i);

         display.selectItem(itemToSelect);
      }
   }

   private AccessControlList parseItemACL() throws Exception
   {

      AccessControlList accessControlList = new AccessControlList();

      Property acl = item.getProperty(ItemProperty.ACL.ACL);

      if (acl == null)
      {
         throw new Exception(IDE.PERMISSIONS_CONSTANT.permissionsNoAclProperty());
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
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
      
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 1)
      {
         item = event.getSelectedItems().get(0);
      }
   }

   /**
    * @see org.exoplatform.ide.client.permissions.ShowPermissionsHandler#onShowPermissions(org.exoplatform.ide.client.permissions.ShowPermissionsEvent)
    */
   @Override
   public void onShowPermissions(ShowPermissionsEvent event)
   {
      if (item == null)
         return;

      VirtualFileSystem.getInstance().getProperties(item,
         Arrays.asList(new QName[]{ItemProperty.ACL.ACL, ItemProperty.OWNER}), new ItemPropertiesCallback()
         {
            @Override
            protected void onSuccess(Item result)
            {
               openView();
            }
         });
   }
   
   private void openView()
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         bindDisplay(d);
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent("Display PermissionsManager must be null"));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

}
