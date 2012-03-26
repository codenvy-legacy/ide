/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import java.util.List;

/**
 * Interface describe package.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Package.java Mar 26, 2012 12:27:33 AM azatsarynnyy $
 *
 */
public interface Package extends AstItem
{

   /**
    * Returns the package name.
    * 
    * @return the package name
    */
   public String getName();

   /**
    * Change the package name.
    * 
    * @param name the package name
    */
   public void setName(String name);

   /**
    * Returns the project identifier.
    * 
    * @return project identifier
    */
   public String getProjectId();

   /**
    * Change the project identifier.
    * 
    * @param projectId the project identifier
    */
   public void setProjectId(String projectId);

   /**
    * Returns the items of AST.
    * 
    * @return items
    */
   public List<AstItem> getItems();

   /**
    * Set the items of AST.
    * 
    * @param items the items
    */
   public void setItems(List<AstItem> items);

   /**
    * Returns the source.
    * 
    * @return the source
    */
   public String getSource();

   /**
    * Set the source.
    * 
    * @param source the source
    */
   public void setSource(String source);

}