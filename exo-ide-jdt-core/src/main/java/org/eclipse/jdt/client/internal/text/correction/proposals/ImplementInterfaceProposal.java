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
package org.eclipse.jdt.client.internal.text.correction.proposals;

import com.google.gwt.user.client.ui.Image;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.IBinding;
import org.eclipse.jdt.client.core.dom.ITypeBinding;
import org.eclipse.jdt.client.core.dom.Type;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;
import org.eclipse.jdt.client.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.client.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.client.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.client.core.dom.rewrite.ListRewrite;

import org.eclipse.jdt.client.internal.corext.codemanipulation.ASTResolving;
import org.eclipse.jdt.client.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.jdt.client.internal.corext.dom.Bindings;
import org.eclipse.jdt.client.internal.text.correction.CorrectionMessages;
import org.eclipse.jdt.client.runtime.CoreException;
import org.exoplatform.ide.editor.shared.runtime.Assert;
import org.exoplatform.ide.editor.shared.text.IDocument;

public class ImplementInterfaceProposal extends LinkedCorrectionProposal
{

   private IBinding fBinding;

   private CompilationUnit fAstRoot;

   private ITypeBinding fNewInterface;

   public ImplementInterfaceProposal(ITypeBinding binding, CompilationUnit astRoot, ITypeBinding newInterface,
      int relevance, IDocument document)
   {
      super("", null, relevance, document, new Image(JdtClientBundle.INSTANCE.correction_change())); //$NON-NLS-1$

      Assert.isTrue(binding != null && Bindings.isDeclarationBinding(binding));

      fBinding = binding;
      fAstRoot = astRoot;
      fNewInterface = newInterface;

      setDisplayName(CorrectionMessages.INSTANCE.ImplementInterfaceProposal_name(binding.getName(),
         Bindings.getRawName(newInterface)));
   }

   @Override
   protected ASTRewrite getRewrite() throws CoreException
   {
      ASTNode boundNode = fAstRoot.findDeclaringNode(fBinding);
      ASTNode declNode = null;
      CompilationUnit newRoot = fAstRoot;
      if (boundNode != null)
      {
         declNode = boundNode; // is same CU
      }
      else
      {
         newRoot = ASTResolving.createQuickFixAST(document, null);
         declNode = newRoot.findDeclaringNode(fBinding.getKey());
      }
      ImportRewrite imports = createImportRewrite(newRoot);

      if (declNode instanceof TypeDeclaration)
      {
         AST ast = declNode.getAST();
         ASTRewrite rewrite = ASTRewrite.create(ast);

         ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(declNode, imports);
         Type newInterface = imports.addImport(fNewInterface, ast, importRewriteContext);
         ListRewrite listRewrite = rewrite.getListRewrite(declNode, TypeDeclaration.SUPER_INTERFACE_TYPES_PROPERTY);
         listRewrite.insertLast(newInterface, null);

         // set up linked mode
         final String KEY_TYPE = "type"; //$NON-NLS-1$
         //         addLinkedPosition(rewrite.track(newInterface), true, KEY_TYPE);
         return rewrite;
      }
      return null;
   }

}
