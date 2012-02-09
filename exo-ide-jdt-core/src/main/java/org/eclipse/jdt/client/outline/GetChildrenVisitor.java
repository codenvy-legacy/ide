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
package org.eclipse.jdt.client.outline;

import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTVisitor;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.EnumDeclaration;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This visitor is used for collecting child nodes of the parent AST node.
 * First, <code>visit</code> method must be called for necessary AST node.
 * Child nodes are available with the use of  {@link GetChildrenVisitor.#getNodes()}.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 6:07:12 PM anya $
 * 
 */
public class GetChildrenVisitor extends ASTVisitor
{

   /**
    * Child nodes.
    */
   private List<Object> nodes = new ArrayList<Object>();

   /**
    * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.CompilationUnit)
    */
   @SuppressWarnings("unchecked")
   @Override
   public boolean visit(CompilationUnit compilationUnit)
   {
      nodes.clear();
      nodes.add(compilationUnit.getPackage());
      nodes.add(new ImportGroupNode("import declarations", compilationUnit.imports()));
      if (!compilationUnit.types().isEmpty())
      {
         nodes.add((ASTNode)compilationUnit.types().get(0));
      }
      return true;
   }

   /**
    * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.TypeDeclaration)
    */
   @Override
   public boolean visit(TypeDeclaration typeDeclaration)
   {
      nodes.clear();
      nodes.addAll(Arrays.asList(typeDeclaration.getFields()));
      nodes.addAll(Arrays.asList(typeDeclaration.getMethods()));
      nodes.addAll(Arrays.asList(typeDeclaration.getTypes()));
      return true;
   }

   /**
    * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.EnumDeclaration)
    */
   @Override
   public boolean visit(EnumDeclaration enumDeclaration)
   {
      nodes.clear();
      nodes.addAll(Arrays.asList(enumDeclaration.enumConstants()));
      return true;
   }

   /**
    * @return {@link List} the list of child nodes
    */
   public List<Object> getNodes()
   {
      return nodes;
   }
}
