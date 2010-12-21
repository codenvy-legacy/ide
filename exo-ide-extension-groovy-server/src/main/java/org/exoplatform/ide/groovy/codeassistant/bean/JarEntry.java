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
package org.exoplatform.ide.groovy.codeassistant.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 20, 2010 11:22:58 AM evgen $
 *
 */
public class JarEntry
{

   private String jarPath;

   private List<String> includePkg = new ArrayList<String>();

   public JarEntry()
   {
   }

   /**
    * @return the jarPath
    */
   public String getJarPath()
   {
      return jarPath;
   }

   /**
    * @param jarPath the jarPath to set
    */
   public void setJarPath(String jarPath)
   {
      this.jarPath = jarPath;
   }

   /**
    * @return the includePkj
    */
   public List<String> getIncludePkgs()
   {
      return includePkg;
   }

   /**
    * @param includePkg the includePkj to set
    */
   public void setIncludePkg(String includePkg)
   {
      this.includePkg.add(includePkg);
   }

}
