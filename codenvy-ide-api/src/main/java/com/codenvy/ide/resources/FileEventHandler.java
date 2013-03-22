package com.codenvy.ide.resources;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handles OpenFileEvent
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 * @version $Id: OpenFileEventHandler.java 34360 2009-07-22 23:58:59Z nzamosenchuk $
 *
 */
public interface FileEventHandler extends EventHandler
{
   /**
    * @param event OpenFileEvent
    */
   void onFileOperation(FileEvent event);
}
