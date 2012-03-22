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
package org.exoplatform.ide.extension.groovy.server;

import org.exoplatform.ide.extension.groovy.shared.Attribute;

/**
 * This class describes attribute that are contained in MANIFEST.MF
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AttributeBean implements Attribute
{

   /**
    * Name of attribute.
    */
   private String name;

   /**
    * Value of attribute.
    */
   private String value;

   /**
    * Creates an empty attribute.
    */
   public AttributeBean()
   {
   }

   /**
    * Creates attribute with specified parameters.
    * 
    * @param name name of attribute
    * @param value value of attribute
    */
   public AttributeBean(String name, String value)
   {
      this.name = name;
      this.value = value;
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.shared.Attribute#getName()
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.shared.Attribute#setName(java.lang.String)
    */
   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.shared.Attribute#getValue()
    */
   @Override
   public String getValue()
   {
      return value;
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.shared.Attribute#setValue(java.lang.String)
    */
   @Override
   public void setValue(String value)
   {
      this.value = value;
   }

}
