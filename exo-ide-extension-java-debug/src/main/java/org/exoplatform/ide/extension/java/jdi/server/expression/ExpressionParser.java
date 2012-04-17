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
package org.exoplatform.ide.extension.java.jdi.server.expression;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class ExpressionParser
{
   /**
    * Create new instance of parser for specified Java expression.
    *
    * @param expression
    *    Java language expression
    * @return concrete implementation of ExpressionParser
    */
   public static ExpressionParser newInstance(String expression)
   {
      // At the moment create instance of ANTLRExpressionParser directly.
      return new ANTLRExpressionParser(expression);
   }

   private final String expression;

   protected ExpressionParser(String expression)
   {
      this.expression = expression;
   }

   /**
    * Get expression for this parser.
    *
    * @return expression
    */
   public String getExpression()
   {
      return expression;
   }

   /**
    * Evaluate expression.
    *
    * @param ev
    *    Evaluator
    * @return result of evaluation
    * @throws ExpressionException
    *    if specified expression is invalid or another error occurs when try to evaluate expression
    */
   public abstract com.sun.jdi.Value evaluate(Evaluator ev);
}
