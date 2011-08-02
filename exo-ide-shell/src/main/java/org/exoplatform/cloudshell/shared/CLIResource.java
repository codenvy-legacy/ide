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

import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

public class CLIResource
{
   private String path;
   private String method;
   private Set<MediaType> consumes;
   private Set<MediaType> produces;
   private Map<String, CLIResourceParameter> parameters;

   public CLIResource(String path, String method, Set<MediaType> consumes, Set<MediaType> produces,
      Map<String, CLIResourceParameter> parameters)
   {
      this.path = path;
      this.method = method;
      this.consumes = consumes;
      this.produces = produces;
      this.parameters = parameters;
   }

   public CLIResource()
   {
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

   public Set<MediaType> getConsumes()
   {
      return consumes;
   }

   public void setConsumes(Set<MediaType> consumes)
   {
      this.consumes = consumes;
   }

   public Set<MediaType> getProduces()
   {
      return produces;
   }

   public void setProduces(Set<MediaType> produces)
   {
      this.produces = produces;
   }

   public Map<String, CLIResourceParameter> getParameters()
   {
      return parameters;
   }

   public void setParameters(Map<String, CLIResourceParameter> parameters)
   {
      this.parameters = parameters;
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = hash * 31 + ((path == null) ? 0 : path.hashCode());
      hash = hash * 31 + ((method == null) ? 0 : method.hashCode());
      hash = hash * 31 + ((consumes == null) ? 0 : consumes.hashCode());
      hash = hash * 31 + ((produces == null) ? 0 : produces.hashCode());
      hash = hash * 31 + ((parameters == null) ? 0 : parameters.hashCode());
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
      if (parameters == null)
      {
         if (other.parameters != null)
            return false;
      }
      else if (!parameters.equals(other.parameters))
      {
         return false;
      }
      return true;
   }

   @Override
   public String toString()
   {
      return "CLIResource [path=" + path + ", method=" + method + ", consumes=" + consumes + ", produces=" + produces
         + ", parameters=" + parameters + "]";
   }
}
