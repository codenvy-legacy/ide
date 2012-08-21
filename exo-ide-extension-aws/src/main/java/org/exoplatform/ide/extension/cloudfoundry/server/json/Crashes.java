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
package org.exoplatform.ide.extension.cloudfoundry.server.json;

import java.util.Arrays;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Crashes
{
   private Crash[] crashes;

   public Crash[] getCrashes()
   {
      return crashes;
   }

   public void setCrashes(Crash[] crashes)
   {
      this.crashes = crashes;
   }

   @Override
   public String toString()
   {
      return "Crashes{" +
         "crashes=" + (crashes == null ? null : Arrays.asList(crashes)) +
         '}';
   }

   public static class Crash
   {
      private String instance;
      private long since;

      public String getInstance()
      {
         return instance;
      }

      public void setInstance(String instance)
      {
         this.instance = instance;
      }

      public long getSince()
      {
         return since;
      }

      public void setSince(long since)
      {
         this.since = since;
      }

      @Override
      public String toString()
      {
         return "Crash{" +
            "instance='" + instance + '\'' +
            ", since=" + since +
            '}';
      }
   }
}
