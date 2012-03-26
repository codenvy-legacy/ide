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

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.extension.java.shared.ast.AstItem;
import org.exoplatform.ide.extension.java.shared.ast.AstItemBean;
import org.exoplatform.ide.extension.java.shared.ast.Package;
import org.exoplatform.ide.extension.java.shared.ast.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PackageBean extends AstItemBean implements Package
{

   private String name;

   private String projectId;

   private String source;

   private List<AstItem> items;

   public PackageBean()
   {
      super(Types.PACKAGE);
   }

   public PackageBean(JSONObject itemObject)
   {
      super(itemObject);
   }

   public void init(JSONObject itemObject)
   {
      super.init(itemObject);

      name = itemObject.get("name").isString().stringValue();
      projectId = itemObject.get("projectId").isString().stringValue();
      source = itemObject.get("source").isString().stringValue();
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.Package#getName()
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.Package#setName(java.lang.String)
    */
   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.Package#getProjectId()
    */
   @Override
   public String getProjectId()
   {
      return projectId;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.Package#setProjectId(java.lang.String)
    */
   @Override
   public void setProjectId(String projectId)
   {
      this.projectId = projectId;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.Package#getItems()
    */
   @Override
   public List<AstItem> getItems()
   {
      if (items == null)
      {
         items = new ArrayList<AstItem>();
      }

      return items;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.Package#setItems(java.util.List)
    */
   @Override
   public void setItems(List<AstItem> items)
   {
      this.items = items;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.Package#getSource()
    */
   @Override
   public String getSource()
   {
      return source;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.Package#setSource(java.lang.String)
    */
   @Override
   public void setSource(String source)
   {
      this.source = source;
   }

}
