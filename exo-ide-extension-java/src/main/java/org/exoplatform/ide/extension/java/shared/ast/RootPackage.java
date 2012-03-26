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
 * Interface describe root package of AST.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: RootPackage.java Mar 26, 2012 1:16:37 AM azatsarynnyy $
 *
 */
public interface RootPackage extends AstItem
{

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

   /**
    * Returns the identifier.
    * 
    * @return the identifier
    */
   public String getId();

   /**
    * Set the identifier.
    * 
    * @param id the identifier
    */
   public void setId(String id);

   /**
    * Returns the identifier of project.
    * 
    * @return project ID
    */
   public String getProjectId();

   /**
    * Set the identifier of project.
    * 
    * @param projectId project ID
    */
   public void setProjectId(String projectId);

   /**
    * Returns the list of the packages.
    * 
    * @return list of the packages
    */
   public List<Package> getPackages();

   /**
    * Set the list of the packages.
    * 
    * @param packages the list of the packages
    */
   public void setPackages(List<Package> packages);

}