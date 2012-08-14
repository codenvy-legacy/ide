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
package org.exoplatform.ide.client.framework.template;

import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Jul 26, 2012 12:38:17 PM anya $
 *
 */
public interface ProjectTemplate extends FolderTemplate
{

   /**
    * @return the classPathLocation
    */
   String getClassPathLocation();

   /**
    * @param classPathLocation the classPathLocation to set
    */
   void setClassPathLocation(String classPathLocation);

   /**
    * @return the type
    */
   String getType();

   /**
    * @param type the type to set
    */
   void setType(String type);

   /**
    * @return the destination
    */
   List<String> getTargets();

   /**
    * @param destination the destination to set
    */
   void setTargets(List<String> targets);

}