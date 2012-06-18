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

import com.sun.jdi.Value;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;

/**
 * ANTLR based implementation of ExpressionParser.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ANTLRExpressionParser extends ExpressionParser
{
   private CommonTreeNodeStream nodes;

   public ANTLRExpressionParser(String expression)
   {
      super(expression);
   }

   @Override
   public Value evaluate(Evaluator ev)
   {
      try
      {
         if (nodes == null)
         {
            parse();
         }
         else
         {
            nodes.reset();
         }
         JavaTreeParser walker = new JavaTreeParser(nodes, ev);
         return walker.evaluate();
      }
      catch (RecognitionException e)
      {
         throw new ExpressionException(e.getMessage(), e);
      }
   }

   private void parse() throws RecognitionException
   {
      JavaLexer lexer = new JavaLexer(new ANTLRStringStream(getExpression()));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      JavaParser parser = new JavaParser(tokens);
      nodes = new CommonTreeNodeStream(parser.expression().getTree());
   }
}
