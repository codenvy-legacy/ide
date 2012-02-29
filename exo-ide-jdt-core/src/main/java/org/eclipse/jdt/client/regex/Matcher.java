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

package org.eclipse.jdt.client.regex;

import com.google.gwt.regexp.shared.MatchResult;

public class Matcher
{

   private Pattern pat = null;

   private String expression = null;

   private int lastPos = 0;

   private MatchResult matches;

   Matcher(Pattern pat, CharSequence cs)
   {
      this.pat = pat;
      this.expression = cs.toString();
   }

   public boolean find()
   {
      matches = pat.matches(expression);
      return (matches != null);
   }

   public int start()
   {
      return expression.indexOf(matches.getGroup(0), lastPos);
   }

   public int end()
   {
      lastPos = expression.indexOf(matches.getGroup(0), lastPos) + matches.getGroup(0).length();
      return lastPos;
   }

   /**
    * @return
    */
   public String group()
   {
      return matches.getGroup(0);
   }

   /**
    * @param i
    * @return
    */
   public String group(int i)
   {
      return matches.getGroup(i);
   }

}