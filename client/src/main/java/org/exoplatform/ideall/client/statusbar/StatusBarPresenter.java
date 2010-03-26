package org.exoplatform.ideall.client.statusbar;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.browser.event.ItemsSelectedEvent;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedHandler;
import org.exoplatform.ideall.client.model.vfs.api.Item;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class StatusBarPresenter implements ItemsSelectedHandler
{

   interface Display
   {

      HasValue<String> getPathInfoField();

   }

   private Display display;

   private HandlerManager eventBus;

   private List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();

   public StatusBarPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers.add(eventBus.addHandler(ItemsSelectedEvent.TYPE, this));
   }

   public void destroy()
   {
      for (HandlerRegistration handler : handlers)
      {
         handler.removeHandler();
      }
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
//         if (item instanceof File)
//         {
//            statusMessage = statusMessage.substring(0, statusMessage.lastIndexOf("/"));
//         }

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
