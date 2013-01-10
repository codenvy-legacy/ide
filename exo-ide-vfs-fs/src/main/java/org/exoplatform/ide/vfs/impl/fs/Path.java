/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

import java.util.Arrays;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class Path
{
   static Path fromString(String path)
   {
      return new Path(normalize(path));
   }

   static final Path ROOT = new Path();

   private static final String[] empty = new String[0];

   private static String[] normalize(String raw)
   {
      return ((raw == null) || raw.isEmpty() || ((raw.length() == 1) && (raw.charAt(0) == '/')))
         ? empty : ((raw.charAt(0) == '/') ? raw.substring(1).split("/") : raw.split("/"));
   }

   private final String[] elements;
   private final int hashCode;

   private String path;

   private Path(String... elements)
   {
      this.elements = new String[elements.length];
      System.arraycopy(elements, 0, this.elements, 0, elements.length);
      int hash = 8;
      hashCode = 31 * hash + Arrays.hashCode(elements);
   }

   Path getParent()
   {
      return isRoot() ? null : elements.length == 1 ? ROOT : subPath(0, elements.length - 1);
   }

   Path subPath(int beginIndex, int endIndex)
   {
      if (beginIndex < 0 || beginIndex >= elements.length || endIndex > elements.length || beginIndex >= endIndex)
      {
         throw new IllegalArgumentException("Invalid end or begin index. ");
      }
      int len = endIndex - beginIndex;
      String[] subPath = new String[len];
      System.arraycopy(elements, beginIndex, subPath, 0, len);
      return new Path(subPath);
   }

   String getName()
   {
      return isRoot() ? "" : element(elements.length - 1);
   }

   String[] elements()
   {
      String[] copy = new String[elements.length];
      System.arraycopy(elements, 0, copy, 0, elements.length);
      return copy;
   }

   private String element(int index)
   {
      if (index < 0 || index >= elements.length)
      {
         throw new IllegalArgumentException("Invalid index. ");
      }
      return elements[index];
   }

   boolean isRoot()
   {
      return elements.length == 0;
   }

   Path newPath(String name)
   {
      String[] relative = normalize(name);
      if (relative.length == 0)
      {
         return this; // It is safety to return this instance since it is immutable.
      }
      String[] absolute = new String[elements.length + relative.length];
      System.arraycopy(elements, 0, absolute, 0, elements.length);
      System.arraycopy(relative, 0, absolute, elements.length, relative.length);
      return new Path(absolute);
   }

   @Override
   public String toString()
   {
      if (path != null)
      {
         return path;
      }
      if (elements.length == 0)
      {
         return path = "/";
      }
      StringBuilder builder = new StringBuilder();
      for (String element : elements)
      {
         builder.append('/');
         builder.append(element);
      }
      return path = builder.toString();
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (!(o instanceof Path))
      {
         return false;
      }
      Path path = (Path)o;
      return Arrays.equals(elements, path.elements);
   }

   @Override
   public int hashCode()
   {
      return hashCode;
   }
}
