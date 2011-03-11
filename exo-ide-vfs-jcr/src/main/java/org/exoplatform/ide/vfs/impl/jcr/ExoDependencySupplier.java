/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.jcr;

import org.everrest.core.BaseDependencySupplier;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;

/**
 * 
 * TODO Move it to the more appropriate place
 * 
 * @version $Id:$
 *
 */
public class ExoDependencySupplier extends BaseDependencySupplier
{

   /**
    * @see org.everrest.core.DependencySupplier#getComponent(java.lang.Class)
    */
   @Override
   public Object getComponent(Class<?> type)
   {     
      ExoContainer c = ExoContainerContext.getCurrentContainer();
      return c.getComponentInstanceOfType(type);
   }

}
