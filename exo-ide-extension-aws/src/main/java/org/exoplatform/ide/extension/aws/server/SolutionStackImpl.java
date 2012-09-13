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
package org.exoplatform.ide.extension.aws.server;

import org.exoplatform.ide.extension.aws.shared.SolutionStack;

import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SolutionStackImpl implements SolutionStack
{
   private String name;
   private List<String> permittedFileTypes;

   public SolutionStackImpl(String name, List<String> permittedFileTypes)
   {
      this.name = name;
      this.permittedFileTypes = permittedFileTypes;
   }

   public SolutionStackImpl()
   {
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
   public List<String> getPermittedFileTypes()
   {
      return permittedFileTypes;
   }

   @Override
   public void setPermittedFileTypes(List<String> permittedFileTypes)
   {
      this.permittedFileTypes = permittedFileTypes;
   }

   @Override
   public String toString()
   {
      return "SolutionStackImpl{" +
         "name='" + name + '\'' +
         ", permittedFileTypes=" + permittedFileTypes +
         '}';
   }
}
