package org.exoplatform.ideall.client.statusbar;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.event.file.ItemSelectedEvent;
import org.exoplatform.ideall.client.event.file.ItemSelectedHandler;
import org.exoplatform.ideall.client.model.File;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class StatusBarPresenter implements ItemSelectedHandler
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
      handlers.add(eventBus.addHandler(ItemSelectedEvent.TYPE, this));
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

   public void onItemSelected(ItemSelectedEvent event)
   {
      String path = event.getSelectedItem().getPath();

      if (event.getSelectedItem() instanceof File)
      {
         path = path.substring(0, path.lastIndexOf("/"));
      }

      display.getPathInfoField().setValue(path);
   }

}
