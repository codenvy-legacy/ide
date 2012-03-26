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
 * Interface describe the java project.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: JavaProject.java Mar 26, 2012 1:24:53 AM azatsarynnyy $
 *
 */
public interface JavaProject extends AstItem
{

   /**
    * Returns the project ID.
    * 
    * @return project ID
    */
   public String getId();

   /**
    * Set the project ID.
    * 
    * @param id the project ID
    */
   public void setId(String id);

   /**
    * Returns the project name.
    * 
    * @return the project name
    */
   public String getName();

   /**
    * Set the project name.
    * 
    * @param name the project name
    */
   public void setName(String name);

   /**
    * Returns the list of root packages.
    * 
    * @return the list of packages
    */
   public List<RootPackage> getRootPackages();

   /**
    * Set the list of root packages.
    * 
    * @param rootPackages the list of root packages
    */
   public void setRootPackages(List<RootPackage> rootPackages);

}