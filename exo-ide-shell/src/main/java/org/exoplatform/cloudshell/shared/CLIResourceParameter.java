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

public class CLIResourceParameter
{
   public enum Type {
      PATH("path"), //
      QUERY("query"), //
      HEADER("header"), //
      MATRIX("matrix"), //
      COOKIE("cookie"), //
      FORM("form"), //
      BODY("body");

      private final String type;

      private Type(String type)
      {
         this.type = type;
      }

      public String toString()
      {
         return type;
      }
   }

   private String restName;
   private Set<String> cliNames;
   private Type type;
   private boolean mandatory;

   public CLIResourceParameter(String restName, Set<String> cliNames, Type type, boolean mandatory)
   {
      this.restName = restName;
      this.cliNames = cliNames;
      this.type = type;
      this.mandatory = mandatory;
   }

   public CLIResourceParameter()
   {
   }

   public String getRestName()
   {
      return restName;
   }

   public void setRestName(String restName)
   {
      this.restName = restName;
   }

   public Set<String> getCliNames()
   {
      return cliNames;
   }

   public void setCliNames(Set<String> cliNames)
   {
      this.cliNames = cliNames;
   }

   public Type getType()
   {
      return type;
   }

   public void setType(Type type)
   {
      this.type = type;
   }

   public boolean isMandatory()
   {
      return mandatory;
   }

   public void setMandatory(boolean mandatory)
   {
      this.mandatory = mandatory;
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = hash * 31 + ((restName == null) ? 0 : restName.hashCode());
      hash = hash * 31 + ((type == null) ? 0 : type.hashCode());
      hash = hash * 31 + ((cliNames == null) ? 0 : cliNames.hashCode());
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
      
      CLIResourceParameter other = (CLIResourceParameter)obj;
      if (restName == null)
      {
         if (other.restName != null)
            return false;
      }
      else if (!restName.equals(other.restName))
      {
         return false;
      }
      if (type != other.type)
      {
         return false;
      }
      if (cliNames == null)
      {
         if (other.cliNames != null)
            return false;
      }
      else if (!cliNames.equals(other.cliNames))
      {
         return false;
      }
      return true;
   }

   @Override
   public String toString()
   {
      return "CLIResourceParameter [restName=" + restName + ", cliNames=" + cliNames + ", type=" + type
         + ", mandatory=" + mandatory + "]";
   }
}
