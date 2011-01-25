/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.client.module.groovy.classpath.ui.event;

import org.exoplatform.ide.client.module.groovy.classpath.GroovyClassPathEntry;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired for adding new sources (dir or file) 
 * to build class path of the current project.
 * 
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 10, 2011 $
 *
 */
public class AddSourceToBuildPathEvent extends GwtEvent<AddSourceToBuildPathHandler>
{

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<AddSourceToBuildPathHandler> TYPE =
      new GwtEvent.Type<AddSourceToBuildPathHandler>();

   /**
    * Sources to add to classpath file.
    */
   private List<GroovyClassPathEntry> classPathEntries;
   
   /**
    * @param classPathEntries sources to add to classpath file
    */
   public AddSourceToBuildPathEvent(List<GroovyClassPathEntry> classPathEntries)
   {
      this.classPathEntries = classPathEntries;
   }
   
   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<AddSourceToBuildPathHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(AddSourceToBuildPathHandler handler)
   {
      handler.onAddSourceToBuildPath(this);
   }

   /**
    * @return the classPathEntries
    */
   public List<GroovyClassPathEntry> getClassPathEntries()
   {
      return classPathEntries;
   }
}
