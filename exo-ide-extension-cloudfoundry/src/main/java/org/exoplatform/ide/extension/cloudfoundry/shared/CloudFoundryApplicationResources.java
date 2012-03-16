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
package org.exoplatform.ide.extension.cloudfoundry.shared;

/**
 * Cloud Foundry application info.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: CloudFoundryApplicationResources.java Mar 16, 2012 2:14:15 PM azatsarynnyy $
 *
 */
public interface CloudFoundryApplicationResources
{

   /**
    * Get amount of memory available for application (in MB).
    * 
    * @return amount of memory.
    */
   public int getMemory();

   /**
    * Set amount of memory available for application (in MB).
    * 
    * @param memory amount of memory.
    */
   public void setMemory(int memory);

   /**
    * Get amount disk space available for application (in MB).
    * 
    * @return amount of disk space.
    */
   public int getDisk();

   /**
    * Set amount disk space available for application (in MB).
    * 
    * @param disk amount of disk space.
    */
   public void setDisk(int disk);

}