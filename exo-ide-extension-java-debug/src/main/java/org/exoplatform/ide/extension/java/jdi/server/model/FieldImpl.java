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
package org.exoplatform.ide.extension.java.jdi.server.model;

import org.exoplatform.ide.extension.java.jdi.shared.Field;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FieldImpl extends VariableImpl implements Field
{
   private boolean isFinal;
   private boolean isStatic;
   private boolean isTransient;
   private boolean isVolatile;

   public FieldImpl(String name,
                    String value,
                    String type,
                    boolean isFinal,
                    boolean isStatic,
                    boolean isTransient,
                    boolean isVolatile)
   {
      super(name, value, type);
      this.isFinal = isFinal;
      this.isStatic = isStatic;
      this.isTransient = isTransient;
      this.isVolatile = isVolatile;
   }

   public FieldImpl()
   {
   }

   @Override
   public boolean isFinal()
   {
      return isFinal;
   }

   @Override
   public void setFinal(boolean value)
   {
      this.isFinal = value;
   }

   @Override
   public boolean isStatic()
   {
      return isStatic;
   }

   @Override
   public void setStatic(boolean value)
   {
      this.isStatic = value;
   }

   @Override
   public boolean isTransient()
   {
      return isTransient;
   }

   @Override
   public void setTransient(boolean value)
   {
      this.isTransient = value;
   }

   @Override
   public boolean isVolatile()
   {
      return isVolatile;
   }

   @Override
   public void setVolatile(boolean value)
   {
      this.isVolatile = value;
   }

   @Override
   public String toString()
   {
      return "FieldImpl{" +
         "name='" + getName() + '\'' +
         ", value='" + getValue() + '\'' +
         ", type='" + getType() + '\'' +
         ", final=" + isFinal +
         ", static=" + isStatic +
         ", transient=" + isTransient +
         ", volatile=" + isVolatile +
         '}';
   }
}
