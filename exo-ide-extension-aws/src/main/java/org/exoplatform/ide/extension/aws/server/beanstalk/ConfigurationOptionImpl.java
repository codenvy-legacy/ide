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
package org.exoplatform.ide.extension.aws.server.beanstalk;

import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOption;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ConfigurationOptionImpl implements ConfigurationOption
{
   private String namespace;
   private String name;
   private String value;

   public ConfigurationOptionImpl(String namespace, String name, String value)
   {
      this.namespace = namespace;
      this.name = name;
      this.value = value;
   }

   public ConfigurationOptionImpl()
   {
   }

   @Override
   public String getNamespace()
   {
      return namespace;
   }

   @Override
   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public String getValue()
   {
      return value;
   }

   @Override
   public void setValue(String value)
   {
      this.value = value;
   }

   @Override
   public String toString()
   {
      return "ConfigurationOptionImpl{" +
         "namespace='" + namespace + '\'' +
         ", name='" + name + '\'' +
         ", value='" + value + '\'' +
         '}';
   }
}
