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

package org.exoplatform.ide.extension.java.server;

import org.exoplatform.ide.extension.java.shared.ast.AstItemBean;
import org.exoplatform.ide.extension.java.shared.ast.Package;
import org.exoplatform.ide.extension.java.shared.ast.RootPackage;
import org.exoplatform.ide.extension.java.shared.ast.Types;

import com.google.gwt.json.client.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RootPackageBean extends AstItemBean implements RootPackage
{

   private String source;

   private String id;

   private String projectId;

   private List<Package> packages;

   public RootPackageBean()
   {
      super(Types.ROOT_PACKAGE);
   }

   public RootPackageBean(JSONObject itemObject)
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

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.RootPackage#getSource()
    */
   @Override
   public String getSource()
   {
      return source;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.RootPackage#setSource(java.lang.String)
    */
   @Override
   public void setSource(String source)
   {
      this.source = source;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.RootPackage#getId()
    */
   @Override
   public String getId()
   {
      return id;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.RootPackage#setId(java.lang.String)
    */
   @Override
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.RootPackage#getProjectId()
    */
   @Override
   public String getProjectId()
   {
      return projectId;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.RootPackage#setProjectId(java.lang.String)
    */
   @Override
   public void setProjectId(String projectId)
   {
      this.projectId = projectId;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.RootPackage#getPackages()
    */
   @Override
   public List<Package> getPackages()
   {
      if (packages == null)
      {
         packages = new ArrayList<Package>();
      }

      return packages;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.RootPackage#setPackages(java.util.List)
    */
   @Override
   public void setPackages(List<Package> packages)
   {
      this.packages = packages;
   }

}
