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
import org.exoplatform.ide.extension.java.shared.ast.JavaProject;
import org.exoplatform.ide.extension.java.shared.ast.RootPackage;
import org.exoplatform.ide.extension.java.shared.ast.Types;

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

public class JavaProjectBean extends AstItemBean implements JavaProject
{

   private String id;

   private String name;

   private List<RootPackage> rootPackages;

   public JavaProjectBean()
   {
      super(Types.PROJECT);
   }

   public JavaProjectBean(JSONObject itemObject)
   {
      super(itemObject);
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.JavaProject#getId()
    */
   @Override
   public String getId()
   {
      return id;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.JavaProject#setId(java.lang.String)
    */
   @Override
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.JavaProject#getName()
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.JavaProject#setName(java.lang.String)
    */
   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.JavaProject#getRootPackages()
    */
   @Override
   public List<RootPackage> getRootPackages()
   {
      if (rootPackages == null)
      {
         rootPackages = new ArrayList<RootPackage>();
      }

      return rootPackages;
   }

   /**
    * @see org.exoplatform.ide.extension.java.shared.ast.JavaProject#setRootPackages(java.util.List)
    */
   @Override
   public void setRootPackages(List<RootPackage> rootPackages)
   {
      this.rootPackages = rootPackages;
   }

   public void init(JSONObject itemObject)
   {
      super.init(itemObject);

      id = itemObject.get("id").isString().stringValue();
      name = itemObject.get("name").isString().stringValue();
   }

}
