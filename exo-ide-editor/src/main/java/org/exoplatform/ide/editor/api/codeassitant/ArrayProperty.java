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
package org.exoplatform.ide.editor.api.codeassitant;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ArrayProperty extends TokenProperty
{

   private List<? extends Token> value;

   /**
    * @param value
    */
   public ArrayProperty(List<? extends Token> value)
   {
      super();
      this.value = value;
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.TokenProperty#isArrayProperty()
    */
   @Override
   public ArrayProperty isArrayProperty()
   {
      return this;
   }

   /**
    * @return value of this property
    */
   public List<? extends Token> arrayValue()
   {
      return value;
   }
}
