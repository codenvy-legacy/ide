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
package org.exoplatform.ide.codeassistant.asm;

import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class AsmAnnotationParameter implements AnnotationParameter
{

   private String name;

   private Object value;

   /**
    * @param name
    * @param value
    */
   public AsmAnnotationParameter(String name, Object value)
   {
      super();
      this.name = name;
      this.value = value;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter#getName()
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter#getValue()
    */
   @Override
   public AnnotationValue getValue()
   {
      return new AsmAnnotationValue(value);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter#setName(java.lang.String)
    */
   @Override
   public void setName(String name)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter#setValue(org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue)
    */
   @Override
   public void setValue(AnnotationValue value)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

}
