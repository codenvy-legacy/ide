/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ide.groovy;


import org.everrest.groovy.BaseResourceId;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: NodeScriptKey.java 34445 2009-07-24 07:51:18Z dkatayev $
 */
public class NodeScriptKey extends BaseResourceId
{
   private final String repositoryName;

   private final String workspaceName;

   private final String path;

   public NodeScriptKey(String repositoryName, String workspaceName, Node node) throws RepositoryException
   {
      this(repositoryName, workspaceName, node.getPath());
   }

   public NodeScriptKey(String repositoryName, String workspaceName, String path)
   {
      super(repositoryName + '@' + workspaceName + ':' + path);
      this.repositoryName = repositoryName;
      this.workspaceName = workspaceName;
      this.path = path;
   }

   public String getRepositoryName()
   {
      return repositoryName;
   }

   public String getWorkspaceName()
   {
      return workspaceName;
   }

   public String getPath()
   {
      return path;
   }
}
