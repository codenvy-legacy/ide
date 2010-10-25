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

import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.StringValueReceivedCallback;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.ACL.AccessControlEntry;
import org.exoplatform.ide.client.framework.vfs.event.ItemACLSavedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemACLSavedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 19, 2010 $
 *
 */
public class PermissionsManagerPresenter implements ItemACLSavedHandler, ExceptionThrownHandler
{

   public interface Dispaly
   {

      void closeForm();

      PermissionsListGrid getPermissionsListGrid();

      HasClickHandlers getAddEntityButton();

      HasClickHandlers getSaveACLButton();

      HasClickHandlers getCancelButton();
      

   }

   private HandlerManager eventBus;

   private Item item;

   private Dispaly dispaly;
   
   private Handlers handlers;

   /**
    * @param eventBus
    * @param item
    */
   public PermissionsManagerPresenter(HandlerManager eventBus, Item item)
   {
      this.eventBus = eventBus;
      this.item = item;
      
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Dispaly d)
   {
      dispaly = d;

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
//            Window.alert("Add");
            Dialogs.getInstance().askForValue("IDE", "Entity:", "", new StringValueReceivedCallback()
            {
               
               public void execute(String value)
               {
                 if(value != null && !"".equals(value))
                 {
                    addEntity(value);
                 }
               }
            });
         }
      });
      
      dispaly.getSaveACLButton().addClickHandler(new ClickHandler()
      {
         
         public void onClick(ClickEvent event)
         {
            saveACL();
         }
      });
      
      dispaly.getPermissionsListGrid().setValue(item.getAcl().getPermissionsList());
      
      dispaly.getPermissionsListGrid().addSelectionHandler(new SelectionHandler<AccessControlEntry>()
      {
         
         public void onSelection(SelectionEvent<AccessControlEntry> event)
         {
            event.getSelectedItem();
         }
      });
      
      dispaly.getPermissionsListGrid().addValueChangeHandler(new ValueChangeHandler<List<AccessControlEntry>>()
      {
         
         public void onValueChange(ValueChangeEvent<List<AccessControlEntry>> event)
         {
            Window.alert("Change!!!11");            
         }
      });
      
      handlers.addHandler(ItemACLSavedEvent.TYPE, this);
   }

   private void addEntity(String identity)
   {
      item.getAcl().getPermissionsList().add(new AccessControlEntry(identity));
      dispaly.getPermissionsListGrid().setValue(item.getAcl().getPermissionsList());
   }
   
   private void saveACL()
   {
      VirtualFileSystem.getInstance().saveACL(item);
   }
   
   /**
    * 
    */
   public void destroy()
   {
      handlers.removeHandlers();      
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemACLSavedHandler#onItemACLSaved(org.exoplatform.ide.client.framework.vfs.event.ItemACLSavedEvent)
    */
   public void onItemACLSaved(ItemACLSavedEvent event)
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

}
