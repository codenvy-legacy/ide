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

import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.Location;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BreakPointImpl implements BreakPoint
{
   private Location location;
   // Always enable at the moment. Managing states of breakpoint is not supported for now.
   private boolean enabled = true;
   private String expression;

   public BreakPointImpl(Location location, String expression)
   {
      this.location = location;
      this.expression = expression;
   }

   public BreakPointImpl(Location location)
   {
      this.location = location;
   }

   public BreakPointImpl()
   {
   }

   @Override
   public Location getLocation()
   {
      return location;
   }

   @Override
   public void setLocation(Location location)
   {
      this.location = location;
   }

   @Override
   public boolean isEnabled()
   {
      return enabled;
   }

   @Override
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   @Override
   public String getCondition()
   {
      return expression;
   }

   @Override
   public void setCondition(String expression)
   {
      this.expression = expression;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }
      BreakPointImpl other = (BreakPointImpl)o;
      return location == null ? other.location == null : location.equals(other.location);
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = 31 * hash + (location == null ? 0 : location.hashCode());
      return hash;
   }

   @Override
   public String toString()
   {
      return "BreakPointImpl{" +
         "location=" + location +
         ", enabled=" + enabled +
         '}';
   }
}
