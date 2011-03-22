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

import java.util.Map;

/**
 * Abstract request to {@link org.exoplatform.ide.git.server.GitConnection}.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public abstract class GitRequest
{
   /**
    * User on whose behalf the current request will be performed, e.g. commit
    * author.
    */
   private GitUser user;

   /**
    * Opaque set of request attributes. If some attribute not need value then
    * empty string or <code>null</code> may be used.
    */
   private Map<String, String> attributes;

   /**
    * @return user on whose behalf the current request will be performed
    */
   public GitUser getUser()
   {
      return user;
   }

   /**
    * @param user user on whose behalf the current request will be performed
    */
   public void setUser(GitUser user)
   {
      this.user = user;
   }

   /**
    * @return set of request attributes
    */
   public Map<String, String> getAttributes()
   {
      return attributes;
   }

   /**
    * @param attributes set of request attributes
    */
   public void setAttributes(Map<String, String> attributes)
   {
      this.attributes = attributes;
   }
}
