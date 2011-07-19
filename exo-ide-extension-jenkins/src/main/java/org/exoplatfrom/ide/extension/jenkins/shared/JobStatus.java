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
package org.exoplatfrom.ide.extension.jenkins.shared;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JobStatus
{
   public enum Status {
      QUEUE("in queue"), //
      BUILD("building"), //
      END("end");

      private final String value;

      private Status(String value)
      {
         this.value = value;
      }

      @Override
      public String toString()
      {
         return value;
      }
   }

   private String name;
   private Status status;
   private String lastBuildResult;

   public JobStatus(String name, Status status, String lastBuildResult)
   {
      this.name = name;
      this.status = status;
      this.lastBuildResult = lastBuildResult;
   }

   public JobStatus()
   {
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public Status getStatus()
   {
      return status;
   }

   public void setStatus(Status status)
   {
      this.status = status;
   }

   public String getLastBuildResult()
   {
      return lastBuildResult;
   }

   public void setLastBuildResult(String lastBuildResult)
   {
      this.lastBuildResult = lastBuildResult;
   }

   @Override
   public String toString()
   {
      return "JobStatus [name=" + name + ", status=" + status + ", lastBuildResult=" + lastBuildResult + "]";
   }
}
