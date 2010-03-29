package org.exoplatform.ideall.client.statusbar;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedEvent;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Item;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

public class StatusBarPresenter implements ItemsSelectedHandler
{

   interface Display
   {

      HasValue<String> getPathInfoField();

   }

   private Display display;

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   public StatusBarPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      handlers.addHandler(ItemsSelectedEvent.TYPE, this);
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

      if (event.getSelectedItems().size() == 1)
      {
         Item item = event.getSelectedItems().get(0);
         statusMessage = item.getHref();
         if (item instanceof File)
         {
            statusMessage = statusMessage.substring(0, statusMessage.lastIndexOf("/"));
         }

         String prefix = context.getEntryPoint();
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

}
