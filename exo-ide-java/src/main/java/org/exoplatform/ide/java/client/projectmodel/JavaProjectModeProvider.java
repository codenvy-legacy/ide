/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.java.client.projectmodel;

import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.resources.ModelProvider;
import org.exoplatform.ide.resources.model.Project;

/**
 * Model provider for Java projects.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaProjectModeProvider implements ModelProvider
{

   private final EventBus eventBus;

   /**
    * @param eventBus
    */
   public JavaProjectModeProvider(EventBus eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Project createProjectInstance()
   {
      return new JavaProject(eventBus);
   }

}
