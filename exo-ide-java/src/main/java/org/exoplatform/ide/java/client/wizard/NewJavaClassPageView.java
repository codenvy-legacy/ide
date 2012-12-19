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
package org.exoplatform.ide.java.client.wizard;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.view.View;

/**
 * View for new Java class wizard.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public interface NewJavaClassPageView extends View<NewJavaClassPageView.ActionDelegate>
{
   /**
    * Action delegate for new Java class wizard
    */
   public interface ActionDelegate
   {

      /**
       * Selected parent changed.
       * @param index the parent index
       */
      void parentChanged(int index);

      /**
       * New type name changed, validate it.
       */
      void checkTypeName();
   }

   /**
    * Get new class name
    * @return new class name
    */
   String getClassName();

   /**
    * Get new class type (class, interface, enum, annotation)
    * @return new class type
    */
   String getClassType();

   /**
    * Set new class types
    * @param classTypes  the array of class types
    */
   void setClassTypes(JsonArray<String> classTypes);

   /**
    * Set parent names for new type
    * @param parentNames the array of names
    */
   void setParents(JsonArray<String> parentNames);
}
