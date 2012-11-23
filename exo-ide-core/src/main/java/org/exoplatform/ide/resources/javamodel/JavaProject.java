/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.resources.javamodel;

import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.resources.model.Project;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class JavaProject extends Project
{
   /** Java-scpecific project description */
   private JavaProjectDesctiprion description;

   /**
    * @param eventBus
    */
   protected JavaProject(EventBus eventBus)
   {
      super(eventBus);
      this.description = new JavaProjectDesctiprion(this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JavaProjectDesctiprion getDescription()
   {
      return description;
   }

}
