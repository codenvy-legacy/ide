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
package org.exoplatform.ide.vfs.server.observation;

import java.util.regex.Pattern;

/**
 * Filter for events by path of changed item. Path specified in constructor - regular expression.
 * For example, expression <code>^(.&#042/)?web\\.xml</code> matched for any web.xml files.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class PathFilter extends ChangeEventFilter
{
   private final Pattern pattern;

   public PathFilter(String path)
   {
      this.pattern = Pattern.compile(path);
   }

   @Override
   public boolean matched(ChangeEvent event)
   {
      final String path = event.getItemPath();
      final String oldPath = event.getOldItemPath();
      return path != null && pattern.matcher(path).matches() || oldPath != null && pattern.matcher(oldPath).matches();
   }
}
