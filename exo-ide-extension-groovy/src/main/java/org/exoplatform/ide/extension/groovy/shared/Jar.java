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
package org.exoplatform.ide.extension.groovy.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains description of the JAR file.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Jar
{

   /**
    * Absolute path to the JAR on the server.
    */
   private String path;

   /**
    * The list of attributes.
    */
   private List<Attribute> attributes = new ArrayList<Attribute>();

   /**
    * Creates a new instance of this Jar.
    */
   public Jar() {
   }
   
   /**
    * Creates a new instance of this Jar.
    * 
    * @param path absolute path to the JAR file on the server
    */
   public Jar(String path)
   {
      this.path = path;
   }

   /**
    * Returns path to the JAR file.
    * 
    * @return path to the JAR file
    */
   public String getPath()
   {
      return path;
   }

   /**
    * Sets new path of this JAR file.
    * 
    * @param path new path of this JAR file
    */
   public void setPath(String path)
   {
      this.path = path;
   }

   /**
    * Gets the list of attributes.
    * 
    * @return list of attributes
    */
   public List<Attribute> getAttributes()
   {
      return attributes;
   }

}
