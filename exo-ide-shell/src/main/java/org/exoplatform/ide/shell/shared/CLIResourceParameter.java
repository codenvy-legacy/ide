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
package org.exoplatform.ide.shell.shared;

import org.exoplatform.ide.shell.shared.CLIResourceParameter;

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

   private String name;
   private Set<String> options;
   private Type type;
   private boolean mandatory;
   private boolean hasArg = true;

   public CLIResourceParameter(String name, Set<String> options, Type type, boolean mandatory, boolean hasArg)
   {
      this.name = name;
      this.options = options;
      this.type = type;
      this.mandatory = mandatory;
      this.hasArg = hasArg;
   }

   public CLIResourceParameter()
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

   public Set<String> getOptions()
   {
      return options;
   }

   public void setOptions(Set<String> options)
   {
      this.options = options;
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

   public boolean isHasArg()
   {
      return hasArg;
   }

   public void setHasArg(boolean hasArg)
   {
      this.hasArg = hasArg;
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = hash * 31 + ((name == null) ? 0 : name.hashCode());
      hash = hash * 31 + ((type == null) ? 0 : type.hashCode());
      hash = hash * 31 + ((options == null) ? 0 : options.hashCode());
      hash = hash * 31 + (mandatory ? 123 : 321);
      hash = hash * 31 + (hasArg ? 456 : 654);
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
      if (name == null)
      {
         if (other.name != null)
            return false;
      }
      else if (!name.equals(other.name))
      {
         return false;
      }
      if (type != other.type)
      {
         return false;
      }
      if (options == null)
      {
         if (other.options != null)
            return false;
      }
      else if (!options.equals(other.options))
      {
         return false;
      }
      if (mandatory != other.mandatory)
      {
         return false;
      }
      if (hasArg != other.hasArg)
      {
         return false;
      }
      return true;
   }

   @Override
   public String toString()
   {
      return "CLIResourceParameter [name=" + name + ", options=" + options + ", type=" + type + ", mandatory="
         + mandatory + ", hasArg=" + hasArg + "]";
   }
}
