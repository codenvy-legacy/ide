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
package org.exoplatform.ide.extension.java.jdi.server;

import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BreakPointImpl implements BreakPoint
{
   private String className;
   private int lineNumber;
   private boolean enabled;

   public BreakPointImpl(String className, int lineNumber, boolean enabled)
   {
      this.className = className;
      this.lineNumber = lineNumber;
      this.enabled = enabled;
   }

   public BreakPointImpl()
   {
   }

   @Override
   public String getClassName()
   {
      return className;
   }

   @Override
   public int getLineNumber()
   {
      return lineNumber;
   }

   @Override
   public boolean isEnabled()
   {
      return enabled;
   }

   @Override
   public void setClassName(String className)
   {
      this.className = className;
   }

   @Override
   public void setLineNumber(int lineNumber)
   {
      this.lineNumber = lineNumber;
   }

   @Override
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   @Override
   public String toString()
   {
      return "BreakPointImpl{" +
         "className='" + className + '\'' +
         ", lineNumber=" + lineNumber +
         ", enabled=" + enabled +
         '}';
   }
}
