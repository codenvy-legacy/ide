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
package org.exoplatform.cloudshell.shared;

import java.util.Set;

public class CLIResource
{
   private String command;
   private String path;
   private String method;
   private Set<String> consumes;
   private Set<String> produces;
   private Set<CLIResourceParameter> params;

   public CLIResource(String command, String path, String method, Set<String> consumes, Set<String> produces,
      Set<CLIResourceParameter> params)
   {
      this.command = command;
      this.path = path;
      this.method = method;
      this.consumes = consumes;
      this.produces = produces;
      this.params = params;
   }

   public CLIResource()
   {
   }

   public String getCommand()
   {
      return command;
   }

   public void setCommand(String command)
   {
      this.command = command;
   }

   public String getPath()
   {
      return path;
   }

   public void setPath(String path)
   {
      this.path = path;
   }

   public String getMethod()
   {
      return method;
   }

   public void setMethod(String method)
   {
      this.method = method;
   }

   public Set<String> getConsumes()
   {
      return consumes;
   }

   public void setConsumes(Set<String> consumes)
   {
      this.consumes = consumes;
   }

   public Set<String> getProduces()
   {
      return produces;
   }

   public void setProduces(Set<String> produces)
   {
      this.produces = produces;
   }

   public Set<CLIResourceParameter> getParams()
   {
      return params;
   }

   public void setParams(Set<CLIResourceParameter> params)
   {
      this.params = params;
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = hash * 31 + ((command == null) ? 0 : command.hashCode());
      hash = hash * 31 + ((path == null) ? 0 : path.hashCode());
      hash = hash * 31 + ((method == null) ? 0 : method.hashCode());
      hash = hash * 31 + ((consumes == null) ? 0 : consumes.hashCode());
      hash = hash * 31 + ((produces == null) ? 0 : produces.hashCode());
      hash = hash * 31 + ((params == null) ? 0 : params.hashCode());
      return hash;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;

      CLIResource other = (CLIResource)obj;
      if (command == null)
      {
         if (other.command != null)
            return false;
      }
      else if (!command.equals(other.command))
      {
         return false;
      }
      if (path == null)
      {
         if (other.path != null)
            return false;
      }
      else if (!path.equals(other.path))
      {
         return false;
      }
      if (method == null)
      {
         if (other.method != null)
            return false;
      }
      else if (!method.equals(other.method))
      {
         return false;
      }
      if (consumes == null)
      {
         if (other.consumes != null)
            return false;
      }
      else if (!consumes.equals(other.consumes))
      {
         return false;
      }
      if (produces == null)
      {
         if (other.produces != null)
            return false;
      }
      else if (!produces.equals(other.produces))
      {
         return false;
      }
      if (params == null || params.isEmpty())
      {
         if (other.params != null && !params.isEmpty())
            return false;
      }
      else if (!params.equals(other.params))
      {
         return false;
      }
      return true;
   }

   @Override
   public String toString()
   {
      return "CLIResource [command=" + command + ", path=" + path + ", method=" + method + ", consumes=" + consumes
         + ", produces=" + produces + ", params=" + params + "]";
   }
}
