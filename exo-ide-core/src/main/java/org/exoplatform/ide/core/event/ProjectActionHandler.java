package org.exoplatform.ide.core.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Resource API fires ProjectAction Events when project any kind of operations that 
 * changes the project invoked. Those are opening, closing, changing the description.
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ProjectActionHandler extends EventHandler
{
   /**
    * Project opened
    * 
    * @param event
    */
   void onProjectOpened(ProjectActionEvent event);

   /**
    * Project opened
    * 
    * @param event
    */
   void onProjectClosed(ProjectActionEvent event);

   /**
    * Project Description Changed
    * 
    * @param event
    */
   void onProjectDescriptionChanged(ProjectActionEvent event);
}
