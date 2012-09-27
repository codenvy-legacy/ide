package org.exoplatform.ide.core.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handles ResourceChangedEvent
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ResourceChangedHandler extends EventHandler
{
   /**
    * Resource created
    * 
    * @param event
    */
   void onResourceCreated(ResourceChangedEvent event);

   /**
    * Resource deleted
    * 
    * @param event
    */
   void onResourceDeleted(ResourceChangedEvent event);

   /**
    * Resource renamed
    * 
    * @param event
    */
   void onResourceRenamed(ResourceChangedEvent event);
   
   /**
    * Resource moved
    * 
    * @param event
    */
   void onResourceMoved(ResourceChangedEvent event);

}
