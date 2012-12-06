/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.core.expressions;

/**
 * Abstract Expression class. Should be used instead of dirrectly implementing {@link Expression}
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class AbstractExpression implements Expression
{
   private static int UNIQUE_ID = 0;

   protected boolean value;

   protected int id;

   /**
    * Construct expression with auto-generated id and register in {@link ExpressionManager} 
    * @param value default initial value
    */
   public AbstractExpression(ExpressionManager expressionManager, boolean value)
   {
      this.value = value;
      this.id = ++UNIQUE_ID;
      expressionManager.registerExpression(this);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int getId()
   {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean getValue()
   {
      return value;
   }
}
