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

import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.client.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.client.core.dom.BodyDeclaration;
import org.eclipse.jdt.client.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.Expression;
import org.eclipse.jdt.client.core.dom.ITypeBinding;
import org.eclipse.jdt.client.core.dom.MemberValuePair;
import org.eclipse.jdt.client.core.dom.SimpleName;
import org.eclipse.jdt.client.core.dom.Type;
import org.eclipse.jdt.client.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.client.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.client.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.client.internal.corext.codemanipulation.ASTResolving;
import org.eclipse.jdt.client.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.jdt.client.internal.corext.dom.ASTNodeFactory;
import org.eclipse.jdt.client.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.client.runtime.CoreException;
import org.exoplatform.ide.editor.text.IDocument;

import java.util.List;

public class NewAnnotationMemberProposal extends LinkedCorrectionProposal
{

   private static final String KEY_NAME = "name"; //$NON-NLS-1$

   private static final String KEY_TYPE = "type"; //$NON-NLS-1$

   private final ASTNode fInvocationNode;

   private final ITypeBinding fSenderBinding;

   public NewAnnotationMemberProposal(String label, ASTNode invocationNode, ITypeBinding binding, int relevance,
      IDocument document, Image image)
   {
      super(label, null, relevance, document, image);
      fInvocationNode = invocationNode;
      fSenderBinding = binding;
   }

   @Override
   protected ASTRewrite getRewrite() throws CoreException
   {
      CompilationUnit astRoot = ASTResolving.findParentCompilationUnit(fInvocationNode);
      ASTNode typeDecl = astRoot.findDeclaringNode(fSenderBinding);
      ASTNode newTypeDecl = null;
      if (typeDecl != null)
      {
         newTypeDecl = typeDecl;
      }
      else
      {
         astRoot = ASTResolving.createQuickFixAST(document, null);
         newTypeDecl = astRoot.findDeclaringNode(fSenderBinding.getKey());
      }
      createImportRewrite(astRoot);

      if (newTypeDecl instanceof AnnotationTypeDeclaration)
      {
         ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

         AnnotationTypeMemberDeclaration newStub = getStub(rewrite, (AnnotationTypeDeclaration)newTypeDecl);

         ChildListPropertyDescriptor property = ASTNodes.getBodyDeclarationsProperty(newTypeDecl);
         List<? extends ASTNode> members = (List<? extends ASTNode>)newTypeDecl.getStructuralProperty(property);
         int insertIndex = members.size();

         ListRewrite listRewriter = rewrite.getListRewrite(newTypeDecl, property);
         listRewriter.insertAt(newStub, insertIndex, null);

         return rewrite;
      }
      return null;
   }

   private AnnotationTypeMemberDeclaration getStub(ASTRewrite rewrite, AnnotationTypeDeclaration targetTypeDecl)
   {
      AST ast = targetTypeDecl.getAST();

      AnnotationTypeMemberDeclaration decl = ast.newAnnotationTypeMemberDeclaration();

      SimpleName newNameNode = getNewName(rewrite);

      decl.modifiers().addAll(ASTNodeFactory.newModifiers(ast, evaluateModifiers(targetTypeDecl)));

//      ModifierCorrectionSubProcessor.installLinkedVisibilityProposals(getLinkedProposalModel(), rewrite,
//         decl.modifiers(), true);

      decl.setName(newNameNode);

      Type returnType = getNewType(rewrite);
      decl.setType(returnType);
      return decl;
   }

   private Type getNewType(ASTRewrite rewrite)
   {
      AST ast = rewrite.getAST();
      Type newTypeNode = null;
      ITypeBinding binding = null;
      if (fInvocationNode.getLocationInParent() == MemberValuePair.NAME_PROPERTY)
      {
         Expression value = ((MemberValuePair)fInvocationNode.getParent()).getValue();
         binding = value.resolveTypeBinding();
      }
      else if (fInvocationNode instanceof Expression)
      {
         binding = ((Expression)fInvocationNode).resolveTypeBinding();
      }
      if (binding != null)
      {
         ImportRewriteContext importRewriteContext =
            new ContextSensitiveImportRewriteContext(fInvocationNode, getImportRewrite());
         newTypeNode = getImportRewrite().addImport(binding, ast, importRewriteContext);
      }
      if (newTypeNode == null)
      {
         newTypeNode = ast.newSimpleType(ast.newSimpleName("String")); //$NON-NLS-1$
      }
//      addLinkedPosition(rewrite.track(newTypeNode), false, KEY_TYPE);
      return newTypeNode;
   }

   private int evaluateModifiers(AnnotationTypeDeclaration targetTypeDecl)
   {
      List<BodyDeclaration> methodDecls = targetTypeDecl.bodyDeclarations();
      for (int i = 0; i < methodDecls.size(); i++)
      {
         Object curr = methodDecls.get(i);
         if (curr instanceof AnnotationTypeMemberDeclaration)
         {
            return ((AnnotationTypeMemberDeclaration)curr).getModifiers();
         }
      }
      return 0;
   }

   private SimpleName getNewName(ASTRewrite rewrite)
   {
      AST ast = rewrite.getAST();
      String name;
      if (fInvocationNode.getLocationInParent() == MemberValuePair.NAME_PROPERTY)
      {
         name = ((SimpleName)fInvocationNode).getIdentifier();
//         if (ast == fInvocationNode.getAST())
//         {
//            addLinkedPosition(rewrite.track(fInvocationNode), true, KEY_NAME);
//         }
      }
      else
      {
         name = "value"; //$NON-NLS-1$
      }

      SimpleName newNameNode = ast.newSimpleName(name);
//      addLinkedPosition(rewrite.track(newNameNode), false, KEY_NAME);
      return newNameNode;
   }

}
