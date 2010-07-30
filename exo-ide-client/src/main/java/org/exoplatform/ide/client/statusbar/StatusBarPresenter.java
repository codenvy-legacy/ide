package org.exoplatform.ide.client.statusbar;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Item;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

public class StatusBarPresenter implements ItemsSelectedHandler, EntryPointChangedHandler
{

   interface Display
   {

      HasValue<String> getPathInfoField();

   }

   private Display display;

   private HandlerManager eventBus;

   private Handlers handlers;

   private String entryPoint;

   public StatusBarPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      handlers.addHandler(ItemsSelectedEvent.TYPE, this);
      handlers.addHandler(EntryPointChangedEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   void bindDisplay(final Display d)
   {
      display = d;
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      String statusMessage = null;

      if (entryPoint == null)
      {
         statusMessage = "No entry point selected!";
      }
      else if (event.getSelectedItems().size() == 1)
      {
         Item item = event.getSelectedItems().get(0);
         statusMessage = item.getHref();
         if (item instanceof File)
         {
            statusMessage = statusMessage.substring(0, statusMessage.lastIndexOf("/"));
         }

         String prefix = entryPoint;
         if (prefix.endsWith("/"))
         {
            prefix = prefix.substring(0, prefix.length() - 1);
         }

         prefix = prefix.substring(0, prefix.lastIndexOf("/"));
         statusMessage = statusMessage.substring(prefix.length());
         if (statusMessage.endsWith("/"))
         {
            statusMessage = statusMessage.substring(0, statusMessage.length() - 1);
         }
      }
      else if (event.getSelectedItems().size() == 0)
      {
         statusMessage = "No items selected!";
      }
      else
      {
         statusMessage = "Selected: <b>" + event.getSelectedItems().size() + "</b> items";
      }

      display.getPathInfoField().setValue(statusMessage);
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

}
