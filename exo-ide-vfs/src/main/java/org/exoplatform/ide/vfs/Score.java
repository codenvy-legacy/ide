/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.vfs;

/**
 * Provides decimal value in the interval [0,1] . Value is 0 if the object is
 * considered by the repository as having absolutely no relevance with respect
 * to the CONTAINS() function specified in the query. Value 1 if the object is
 * considered by the repository as having absolutely complete relevance with
 * respect to the CONTAINS() function specified in the query.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class Score
{
   /** An alias column name defined for the SCORE() function. */
   private final String scoreColumnName;

   /** Score value. */
   private final double scoreValue;

   public Score(String scoreColumnName, double scoreValue)
   {
      this.scoreColumnName = scoreColumnName;
      this.scoreValue = scoreValue;
   }

   /**
    * @return alias column name defined for the SCORE() function.
    */
   public String getScoreColumnName()
   {
      return scoreColumnName;
   }

   /**
    * @return score value
    */
   public double getScoreValue()
   {
      return scoreValue;
   }
}
