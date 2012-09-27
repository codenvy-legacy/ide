package org.exoplatform.ide.core.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.resources.model.Project;

/**
 * Event that describes the fact that Project Action has be performed
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ProjectActionEvent extends GwtEvent<ProjectActionHandler>
{

   public static Type<ProjectActionHandler> TYPE = new Type<ProjectActionHandler>();
   
   public static enum ProjectAction {
      OPENED, CLOSED, DESCRIPTION_CHANGED, RESOURCE_CHANGED;
   }
   
   private final Project project;
   private final ProjectAction projectAction;
   
   public static ProjectActionEvent createProjectOpenedEvent(Project project)
   {
      return new ProjectActionEvent(project, ProjectAction.OPENED);
   }
   
   public static ProjectActionEvent createProjectClosedEvent(Project project)
   {
      return new ProjectActionEvent(project, ProjectAction.CLOSED);
   }
   
   public static ProjectActionEvent createProjectDescriptionChangedEvent(Project project)
   {
      return new ProjectActionEvent(project, ProjectAction.DESCRIPTION_CHANGED);
   }
   
   protected ProjectActionEvent(Project project, ProjectAction projectAction)
   {
      this.project = project;
      this.projectAction = projectAction;
   }

   @Override
   public Type<ProjectActionHandler> getAssociatedType()
   {
      return TYPE;
   }

   public Project getProject()
   {
      return project;
   }
   
   public ProjectAction getProjectAction()
   {
      return projectAction;
   }

   @Override
   protected void dispatch(ProjectActionHandler handler)
   {
      switch (projectAction)
      {
         case OPENED :
            handler.onProjectOpened(this);
            break;
         case CLOSED :
            handler.onProjectClosed(this);
            break;
         case DESCRIPTION_CHANGED :
            handler.onProjectDescriptionChanged(this);
            break;
         default :
            break;
      }
   }
}
