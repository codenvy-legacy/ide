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
package org.exoplatform.ide.vfs.shared;

import java.util.List;
import java.util.Map;

/**
 * The Project - folder w/ special meaning
 */
public class Project extends Folder
{
   public static String PROJECT_MIME_TYPE = "text/vnd.ideproject+directory";

   protected String projectType;

   @SuppressWarnings("rawtypes")
   public Project(String id, String name, String mimeType, String path, String parentId, long creationDate,
      List<Property> properties, Map<String, Link> links, String projectType)
   {
      super(id, name, mimeType, path, parentId, creationDate, properties, links);
      this.projectType = projectType;
   }

   public Project()
   {
      super();
      mimeType = PROJECT_MIME_TYPE;
   }

   public final String getProjectType()
   {
      return projectType;
   }

   public void setProjectType(String projectType)
   {
      this.projectType = projectType;
   }
}
