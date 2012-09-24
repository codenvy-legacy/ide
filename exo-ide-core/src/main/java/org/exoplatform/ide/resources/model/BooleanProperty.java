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
package org.exoplatform.ide.resources.model;

import org.exoplatform.ide.json.JsonArray;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BooleanProperty.java 65773 2011-02-02 13:46:50Z andrew00x $
 */
public class BooleanProperty extends Property<Boolean>
{
   public BooleanProperty()
   {
      super();
   }

   public BooleanProperty(String name, Boolean value)
   {
      super(name, value);
   }

   public BooleanProperty(String name, JsonArray<Boolean> value)
   {
      super(name, value);
   }

   public JsonArray<Boolean> getValue()
   {
      return value;
   }

   public void setValue(JsonArray<Boolean> value)
   {
      this.value = value;
   }
}
