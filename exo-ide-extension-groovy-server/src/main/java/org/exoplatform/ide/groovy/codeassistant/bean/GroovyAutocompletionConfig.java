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
 * @version $Id: Dec 20, 2010 11:52:57 AM evgen $
 *
 */
public class GroovyAutocompletionConfig
{
   private List<JarEntry> jarEntries = new ArrayList<JarEntry>();

   private List<JarEntry> jarDocs = new ArrayList<JarEntry>();

   private String wsName;

   private boolean runInThread = false;

   /**
    * @return the jarEntry
    */
   public List<JarEntry> getJarEntries()
   {
      return jarEntries;
   }

   /**
    * @param jarEntry the jarEntry to set
    */
   public void setJarsEntries(JarEntry jarsEntry)
   {
      this.jarEntries.add(jarsEntry);
   }

   /**
    * @return the wsName
    */
   public String getWsName()
   {
      return wsName;
   }

   /**
    * @param wsName the wsName to set
    */
   public void setWsName(String wsName)
   {
      this.wsName = wsName;
   }

   /**
    * @return the jarsDock
    */
   public List<JarEntry> getJarsDocs()
   {
      return jarDocs;
   }

   /**
    * @param jarsDock the jarsDock to set
    */
   public void setJarsDocs(JarEntry jarsDock)
   {
      this.jarDocs.add(jarsDock);
   }

   /**
    * @return the runInThread
    */
   public boolean isRunInThread()
   {
      return runInThread;
   }

   /**
    * @param runInThread the runInThread to set
    */
   public void setRunInThread(boolean runInThread)
   {
      this.runInThread = runInThread;
   }

}
