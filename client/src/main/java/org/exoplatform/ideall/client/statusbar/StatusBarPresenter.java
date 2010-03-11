package org.exoplatform.ideall.client.statusbar;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.event.file.SelectedItemsEvent;
import org.exoplatform.ideall.client.event.file.SelectedItemsHandler;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Item;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class StatusBarPresenter implements SelectedItemsHandler
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
      handlers.add(eventBus.addHandler(SelectedItemsEvent.TYPE, this));
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

   public void onItemsSelected(SelectedItemsEvent event)
   {

      String statusMessage = null;

      if (event.getSelectedItems().size() == 1)
      {
         Item item = event.getSelectedItems().get(0);
         statusMessage = item.getPath();
         if (item instanceof File)
         {
            statusMessage = statusMessage.substring(0, statusMessage.lastIndexOf("/"));
         }

      }
      else if (event.getSelectedItems().size() == 0)
      {
         statusMessage = "No items selected!";
      }
      else
      {
         statusMessage = "Slected: <b>" + event.getSelectedItems().size() + "</b> items";
      }

      display.getPathInfoField().setValue(statusMessage);
   }

}
