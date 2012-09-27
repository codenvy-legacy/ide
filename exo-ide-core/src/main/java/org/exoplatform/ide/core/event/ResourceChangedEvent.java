package org.exoplatform.ide.core.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.resources.model.Resource;

/**
 * Event that describes the fact that Project Action has be performed
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ResourceChangedEvent extends GwtEvent<ResourceChangedHandler>
{

   public static Type<ResourceChangedHandler> TYPE = new Type<ResourceChangedHandler>();

   public static enum ResourceAction {
      CREATED, DELETED, RENAMED, MOVED;
   }

   private final Resource resource;

   private final ResourceAction resourceAction;

   public static ResourceChangedEvent createResourceCreatedEvent(Resource resource)
   {
      return new ResourceChangedEvent(resource, ResourceAction.CREATED);
   }

   public static ResourceChangedEvent createResourceDeletedEvent(Resource resource)
   {
      return new ResourceChangedEvent(resource, ResourceAction.DELETED);
   }

   public static ResourceChangedEvent createResourceRenamedEvent(Resource resource)
   {
      return new ResourceChangedEvent(resource, ResourceAction.RENAMED);
   }

   public static ResourceChangedEvent createResourceMovedEvent(Resource resource)
   {
      return new ResourceChangedEvent(resource, ResourceAction.MOVED);
   }

   protected ResourceChangedEvent(Resource resource, ResourceAction projectAction)
   {
      this.resource = resource;
      this.resourceAction = projectAction;
   }

   @Override
   public Type<ResourceChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   public Resource getResource()
   {
      return resource;
   }

   public ResourceAction getProjectAction()
   {
      return resourceAction;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   protected void dispatch(ResourceChangedHandler handler)
   {
      switch (resourceAction)
      {
         case CREATED :
            handler.onResourceCreated(this);
            break;
         case DELETED :
            handler.onResourceDeleted(this);
            break;
         case RENAMED :
            handler.onResourceRenamed(this);
            break;
         case MOVED :
            handler.onResourceMoved(this);
            break;
         default :
            break;
      }

   }
}
