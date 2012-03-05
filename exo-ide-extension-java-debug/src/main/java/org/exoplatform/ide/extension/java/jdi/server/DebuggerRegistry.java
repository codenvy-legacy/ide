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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class DebuggerRegistry
{
   private static final AtomicLong counter = new AtomicLong(1);

   private final ConcurrentMap<String, Debugger> all = new ConcurrentHashMap<String, Debugger>();

   public Debugger get(String key)
   {
      Debugger d = all.get(key);
      if (d == null)
      {
         throw new IllegalArgumentException("Debugger " + key + " not found. ");
      }
      return d;
   }

   public String add(Debugger debugger)
   {
      final String key = Long.toString(counter.getAndIncrement());
      all.put(key, debugger);
      return key;
   }
}
