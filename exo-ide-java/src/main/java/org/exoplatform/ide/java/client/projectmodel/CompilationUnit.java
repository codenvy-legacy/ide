/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.java.client.projectmodel;

import com.google.gwt.json.client.JSONObject;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Folder;

/**
 * Java Compilation unit is a .java file that contains top level class and can be compiled
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class CompilationUnit extends File
{
   public static final String TYPE = "java.compilationUnit";

   /**
    * Internal default constructor
    */
   protected CompilationUnit()
   {
      super(TYPE);
   }

   /**
    * Constructor for unmarshalling
    *
    * @param itemObject
    */
   protected CompilationUnit(JSONObject itemObject)
   {
      this();
      init(itemObject);
   }

   /**
    * Get Package containing this {@link CompilationUnit}.
    *
    * @return Parent Package
    */
   public Package getPackage()
   {
      Folder parent = getParent();
      // check parent is Package
      checkValidParent(parent);

      // return Parent package
      return (Package)parent;
   }

   /**
    * Set Parent Package
    *
    * @param parentPackage the parentPackage to set
    */
   public void setPackage(Package parentPackage)
   {
      setParent(parentPackage);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setParent(Folder parent)
   {
      // check parent is Package
      checkValidParent(parent);
      // set Package as CompilationUnit's parent element
      super.setParent(parent);
   }

   /**
    * Check that given parent is an instance of {@link Package}
    *
    * @param parent
    */
   protected void checkValidParent(Folder parent)
   {
      if (!(parent instanceof Package) && !(parent instanceof SourceFolder))
      {
         throw new IllegalArgumentException("Invalid CompilationUnit parent. It must be an instance of Package or SourceFolder class");
      }
   }
}
