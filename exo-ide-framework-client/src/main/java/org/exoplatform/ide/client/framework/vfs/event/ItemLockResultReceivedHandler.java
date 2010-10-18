package org.exoplatform.ide.client.framework.vfs.event;

import com.google.gwt.event.shared.EventHandler;

public interface ItemLockResultReceivedHandler extends EventHandler
{

   void onItemLockResultReceived(ItemLockResultReceivedEvent event);

}
