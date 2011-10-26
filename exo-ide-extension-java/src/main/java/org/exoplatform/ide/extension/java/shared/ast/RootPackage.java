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

package org.exoplatform.ide.extension.java.shared.ast;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONObject;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RootPackage extends AstItem
{

   private String source;

   private String id;

   private String projectId;

   private List<Package> packages;

   public RootPackage()
   {
      super(Types.ROOT_PACKAGE);
   }

   public RootPackage(JSONObject itemObject)
   {
      super(itemObject);
   }

   public void init(JSONObject itemObject)
   {
      super.init(itemObject);
      
      id = itemObject.get("id").isString().stringValue();
      projectId = itemObject.get("projectId").isString().stringValue();
      source = itemObject.get("source").isString().stringValue();
   }

   public String getSource()
   {
      return source;
   }

   public void setSource(String source)
   {
      this.source = source;
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getProjectId()
   {
      return projectId;
   }

   public void setProjectId(String projectId)
   {
      this.projectId = projectId;
   }

   public List<Package> getPackages()
   {
      if (packages == null)
      {
         packages = new ArrayList<Package>();
      }

      return packages;
   }

   public void setPackages(List<Package> packages)
   {
      this.packages = packages;
   }

}
