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
package org.exoplatform.ide.extension.groovy.server;

import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.server.observation.MimeTypeFilter;
import org.exoplatform.ide.vfs.server.observation.TypeFilter;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent.ChangeType;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class GroovyScriptServiceApplication extends Application
{
   private final Set<Object> objects = new HashSet<Object>();

   private final Set<Class<?>> classes = new HashSet<Class<?>>();

   public GroovyScriptServiceApplication(EventListenerList listenerList)
   {
      classes.add(GroovyTemplateService.class);
      classes.add(RestCodeAssistantGroovy.class);
      classes.add(GroovyScriptService.class);

      ProjectCreationListener listener = new ProjectCreationListener();
      listenerList.addEventListener(ChangeEventFilter.createAndFilter(//
         new MimeTypeFilter(Project.PROJECT_MIME_TYPE), new TypeFilter(ChangeType.RENAMED)),//
         listener);

      listenerList.addEventListener(ChangeEventFilter.createAndFilter(//
         new MimeTypeFilter(Project.PROJECT_MIME_TYPE), new TypeFilter(ChangeType.CREATED)),//
         listener);
      
      listenerList.addEventListener(ChangeEventFilter.createAndFilter(//
         new MimeTypeFilter(Project.PROJECT_MIME_TYPE), new TypeFilter(ChangeType.PROPERTIES_UPDATED)),//
         listener);
   }

   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   public Set<Object> getSingletons()
   {
      return objects;
   }
}
