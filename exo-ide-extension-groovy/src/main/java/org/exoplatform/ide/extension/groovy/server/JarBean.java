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
package org.exoplatform.ide.extension.groovy.server;

import org.exoplatform.ide.extension.groovy.shared.Attribute;
import org.exoplatform.ide.extension.groovy.shared.Jar;

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

public class JarBean implements Jar
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
   public JarBean() {
   }
   
   /**
    * Creates a new instance of this Jar.
    * 
    * @param path absolute path to the JAR file on the server
    */
   public JarBean(String path)
   {
      this.path = path;
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.shared.Jar#getPath()
    */
   @Override
   public String getPath()
   {
      return path;
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.shared.Jar#setPath(java.lang.String)
    */
   @Override
   public void setPath(String path)
   {
      this.path = path;
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.shared.Jar#getAttributes()
    */
   @Override
   public List<Attribute> getAttributes()
   {
      return attributes;
   }

   @Override
   public void setAttributes(Attribute attributes)
   {
      // TODO Auto-generated method stub
      
   }

}
