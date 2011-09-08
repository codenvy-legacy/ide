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
package org.exoplatform.ide.client.framework.discovery;


/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class EntryPoint 
{
   private String href;
   
   private String workspace;
   
   /**
    * 
    */
   public EntryPoint()
   {
   }

   /**
    * @param href
    * @param workspace
    */
   public EntryPoint(String href, String workspace)
   {
      this.href = href;
      this.workspace = workspace;
   }

   
   /**
    * @return the href
    */
   public String getHref()
   {
      return href;
   }

   /**
    * @param href the href to set
    */
   public void setHref(String href)
   {
      this.href = href;
   }

   /**
    * @return the workspace
    */
   public String getWorkspace()
   {
      return workspace;
   }

   /**
    * @param workspace the workspace to set
    */
   public void setWorkspace(String workspace)
   {
      this.workspace = workspace;
   }
   
   

   

   
}
