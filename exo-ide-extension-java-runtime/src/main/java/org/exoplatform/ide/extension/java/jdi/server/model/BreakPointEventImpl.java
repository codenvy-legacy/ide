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
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEvent;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BreakPointEventImpl implements BreakPointEvent
{
   private BreakPoint breakPoint;

   public BreakPointEventImpl(BreakPoint breakPoint)
   {
      this.breakPoint = breakPoint;
   }

   @Override
   public BreakPoint getBreakPoint()
   {
      return breakPoint;
   }

   @Override
   public void setBreakPoint(BreakPoint breakPoint)
   {
      this.breakPoint = breakPoint;
   }

   @Override
   public String toString()
   {
      return "BreakPointEventImpl{" +
         "breakPoint=" + breakPoint +
         '}';
   }

   @Override
   public final int getType()
   {
      return BREAKPOINT;
   }
}
