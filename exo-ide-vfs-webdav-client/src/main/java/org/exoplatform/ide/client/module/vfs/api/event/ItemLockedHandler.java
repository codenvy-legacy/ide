package org.exoplatform.ide.client.module.vfs.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface ItemLockedHandler extends EventHandler
{

   void onItemLocked(ItemLockedEvent event);
   
}
