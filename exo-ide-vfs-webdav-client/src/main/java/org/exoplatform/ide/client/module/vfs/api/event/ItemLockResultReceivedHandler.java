package org.exoplatform.ide.client.module.vfs.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface ItemLockResultReceivedHandler extends EventHandler
{

   void onItemLockResultReceived(ItemLockResultReceivedEvent event);

}
