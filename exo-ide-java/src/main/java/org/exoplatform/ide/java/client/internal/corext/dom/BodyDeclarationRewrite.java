/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.exoplatform.ide.java.client.internal.corext.dom;

import org.exoplatform.ide.java.client.core.dom.ASTNode;
import org.exoplatform.ide.java.client.core.dom.BodyDeclaration;
import org.exoplatform.ide.java.client.core.dom.ChildListPropertyDescriptor;
import org.exoplatform.ide.java.client.core.dom.rewrite.ASTRewrite;
import org.exoplatform.ide.java.client.core.dom.rewrite.ListRewrite;
import org.exoplatform.ide.text.edits.TextEditGroup;

import java.util.List;

public class BodyDeclarationRewrite
{

   private ASTNode fTypeNode;

   private ListRewrite fListRewrite;

   public static BodyDeclarationRewrite create(ASTRewrite rewrite, ASTNode typeNode)
   {
      return new BodyDeclarationRewrite(rewrite, typeNode);
   }

   private BodyDeclarationRewrite(ASTRewrite rewrite, ASTNode typeNode)
   {
      ChildListPropertyDescriptor property = ASTNodes.getBodyDeclarationsProperty(typeNode);
      fTypeNode = typeNode;
      fListRewrite = rewrite.getListRewrite(typeNode, property);
   }

   public void insert(BodyDeclaration decl, TextEditGroup description)
   {
      List<BodyDeclaration> container = ASTNodes.getBodyDeclarations(fTypeNode);
      int index = ASTNodes.getInsertionIndex(decl, container);
      fListRewrite.insertAt(decl, index, description);
   }
}
