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
package org.exoplatform.ide.jdt.core;

import static org.junit.Assert.*;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import java.io.IOException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Jan 4, 2012 4:21:31 PM evgen $
 *
 */
public class ParserTest
{

   private char[] javaFiles;

   private ASTParser parser;

   @Before
   public void parseFile() throws IOException
   {
      javaFiles =
         IOUtils.toCharArray(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("Display.java"));
      parser = ASTParser.newParser(AST.JLS3);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
   }

   @Test
   public void paser2() throws Exception
   {
      parser.setUnitName("Display");
      parser.setSource(javaFiles);
      parser.setEnvironment(null, new String[]{"/my/path"}, new String[]{"UTF-8"}, true);
      ASTNode ast = parser.createAST(null);
      
   }
}
