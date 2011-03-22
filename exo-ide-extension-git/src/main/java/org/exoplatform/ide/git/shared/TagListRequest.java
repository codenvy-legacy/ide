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
package org.exoplatform.ide.git.shared;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagListRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagListRequest extends GitRequest
{
   /**
    * List tags with names that match the this pattern. If <code>null</code>
    * then all tags included in result list.
    * 
    * @see org.exoplatform.ide.git.server.GitClient#tagList(TagListRequest)
    */
   private String pattern;

   /**
    * @param pattern
    */
   public TagListRequest(String pattern)
   {
      this.pattern = pattern;
   }

   public TagListRequest()
   {
   }

   public String getPattern()
   {
      return pattern;
   }

   public void setPattern(String pattern)
   {
      this.pattern = pattern;
   }
}
