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
package org.eclipse.jdt.client;

import com.google.gwt.user.client.ui.FlexTable;

import com.google.gwt.user.client.ui.Label;

import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.google.gwt.core.client.GWT;

import com.google.gwt.resources.client.TextResource;

import com.google.gwt.resources.client.ClientBundle;

import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;

import com.google.gwt.core.client.EntryPoint;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Jan 5, 2012 5:15:04 PM evgen $
 *
 */
public class JdtTest implements EntryPoint
{

   public interface Data extends ClientBundle
   {
      
      @Source("org/eclipse/jdt/client/TypeDeclaration.txt")
      TextResource content();
   }
   
   /**
    * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
    */
   @Override
   public void onModuleLoad()
   {
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      Data d = GWT.create(Data.class);
      parser.setSource(d.content().getText().toCharArray());
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setUnitName("Display");
      FlexTable table = new FlexTable();
      RootLayoutPanel.get().add(table);
      table.setWidget(0, 0, new Label(System.currentTimeMillis() + ""));
      ASTNode ast = parser.createAST(null);
      table.setWidget(0, 1, new Label(System.currentTimeMillis() + ""));
      CompilationUnit unit  = (CompilationUnit)ast;
      TypeDeclaration type = (TypeDeclaration)unit.types().get(0);
      RootLayoutPanel.get().add(new Label(type.getName().getFullyQualifiedName()));
   }

}
