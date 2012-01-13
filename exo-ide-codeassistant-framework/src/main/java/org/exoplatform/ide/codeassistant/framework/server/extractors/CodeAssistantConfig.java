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
package org.exoplatform.ide.codeassistant.framework.server.extractors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 20, 2010 11:52:57 AM evgen $
 * 
 */
public class CodeAssistantConfig
{
   private List<JarEntry> jars = new ArrayList<JarEntry>();

   private String wsName;

   private boolean runInThread = false;

   /**
    * @return the jar
    */
   public List<JarEntry> getJars()
   {
      return jars;
   }

   /**
    * @param jar the jar to set
    */
   public void setJars(List<JarEntry> jar)
   {
      this.jars = jar;
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
