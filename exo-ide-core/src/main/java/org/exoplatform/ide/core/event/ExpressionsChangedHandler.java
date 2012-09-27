package org.exoplatform.ide.core.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handles ExpressionsEventHandler
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ExpressionsChangedHandler extends EventHandler
{
   /**
    * The value of the Core Expressions changed. Event contains the list of IDs and new values
    * 
    * @param event
    */
   void onExpressionsChanged(ExpressionsChangedEvent event);

}
