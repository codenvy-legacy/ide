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
package org.exoplatform.ide.extension.groovy.shared;

/**
 * Interface describes attribute that are contained in MANIFEST.MF.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Attribute.java Mar 22, 2012 10:30:59 AM azatsarynnyy $
 *
 */
public interface Attribute
{

   /**
    * Gets name of attribute.
    * 
    * @return name of attribute
    */
   public String getName();

   /**
    * Sets a new name of attribute.
    * 
    * @param name new name of attribute
    */
   public void setName(String name);

   /**
    * Gets value of attribute.
    * 
    * @return value of attribute
    */
   public String getValue();

   /**
    * Sets a new value of attribute.
    * 
    * @param value new value of attribute
    */
   public void setValue(String value);

}