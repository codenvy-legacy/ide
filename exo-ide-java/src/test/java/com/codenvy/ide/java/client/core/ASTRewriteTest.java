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
package com.codenvy.ide.java.client.core;

import com.codenvy.ide.java.client.core.dom.AST;
import com.codenvy.ide.java.client.core.dom.ASTParser;
import com.codenvy.ide.java.client.core.dom.CompilationUnit;
import com.codenvy.ide.java.client.core.dom.ImportDeclaration;
import com.codenvy.ide.java.client.core.dom.TypeDeclaration;
import com.codenvy.ide.java.client.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.java.client.core.dom.rewrite.ITrackedNodePosition;
import com.codenvy.ide.java.client.core.dom.rewrite.ListRewrite;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentImpl;
import com.codenvy.ide.text.edits.MalformedTreeException;
import com.codenvy.ide.text.edits.TextEdit;
import com.codenvy.ide.text.edits.UndoEdit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 12:52:10 PM 34360 2009-07-22 23:58:59Z evgen $
 * 
 */
public class ASTRewriteTest extends ParserBaseTest
{

   @Test
   public void testRewrite()
   {

      Document document = new DocumentImpl("import java.util.List;\nclass X {}\n");
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setSource(document.get().toCharArray());
      CompilationUnit cu = (CompilationUnit)parser.createAST();
      AST ast = cu.getAST();
      ImportDeclaration id = ast.newImportDeclaration();
      id.setName(ast.newName(new String[]{"java", "util", "Set"}));
      ASTRewrite rewriter = ASTRewrite.create(ast);
      TypeDeclaration td = (TypeDeclaration)cu.types().get(0);
      ITrackedNodePosition tdLocation = rewriter.track(td);
      ListRewrite lrw = rewriter.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
      lrw.insertLast(id, null);
      TextEdit edits = rewriter.rewriteAST(document, null);
      UndoEdit undo = null;
      try
      {
         undo = edits.apply(document);
      }
      catch (MalformedTreeException e)
      {
         e.printStackTrace();
      }
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }
      Assert.assertTrue("import java.util.List;\nimport java.util.Set;\nclass X {}\n".equals(document.get()));
      // tdLocation.getStartPosition() and tdLocation.getLength()
      // are new source range for &quot;class X {}&quot; in document.get()
   }

}
