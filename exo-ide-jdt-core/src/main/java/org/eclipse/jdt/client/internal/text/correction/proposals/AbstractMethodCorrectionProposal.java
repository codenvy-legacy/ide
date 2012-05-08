/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla - [quick fix] Create Method in void context should 'box' void. - https://bugs.eclipse.org/bugs/show_bug.cgi?id=107985
 *******************************************************************************/

package org.eclipse.jdt.client.internal.text.correction.proposals;

import com.google.gwt.user.client.ui.Image;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.client.JavaPreferencesSettings;
import org.eclipse.jdt.client.JdtExtension;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.Block;
import org.eclipse.jdt.client.core.dom.BodyDeclaration;
import org.eclipse.jdt.client.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.IExtendedModifier;
import org.eclipse.jdt.client.core.dom.ITypeBinding;
import org.eclipse.jdt.client.core.dom.IVariableBinding;
import org.eclipse.jdt.client.core.dom.Javadoc;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.Name;
import org.eclipse.jdt.client.core.dom.PrimitiveType;
import org.eclipse.jdt.client.core.dom.ReturnStatement;
import org.eclipse.jdt.client.core.dom.SimpleName;
import org.eclipse.jdt.client.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.client.core.dom.Type;
import org.eclipse.jdt.client.core.dom.TypeParameter;
import org.eclipse.jdt.client.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.client.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.client.internal.corext.codemanipulation.ASTResolving;
import org.eclipse.jdt.client.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.client.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.client.internal.corext.dom.ASTNodeFactory;
import org.eclipse.jdt.client.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.client.internal.corext.dom.Bindings;
import org.eclipse.jdt.client.runtime.CoreException;
import org.exoplatform.ide.editor.runtime.Assert;
import org.exoplatform.ide.editor.text.IDocument;

public abstract class AbstractMethodCorrectionProposal extends LinkedCorrectionProposal
{

   private ASTNode fNode;

   private ITypeBinding fSenderBinding;

   public AbstractMethodCorrectionProposal(String label, ASTNode invocationNode, ITypeBinding binding, int relevance,
      IDocument document, Image image)
   {
      super(label, null, relevance, document, image);

      Assert.isTrue(binding != null && Bindings.isDeclarationBinding(binding));

      fNode = invocationNode;
      fSenderBinding = binding;
   }

   protected ASTNode getInvocationNode()
   {
      return fNode;
   }

   /**
    * @return The binding of the type declaration (generic type)
    */
   protected ITypeBinding getSenderBinding()
   {
      return fSenderBinding;
   }

   @Override
   protected ASTRewrite getRewrite() throws CoreException
   {
      CompilationUnit astRoot = ASTResolving.findParentCompilationUnit(fNode);
      ASTNode typeDecl = astRoot.findDeclaringNode(fSenderBinding);
      ASTNode newTypeDecl = null;
      boolean isInDifferentCU;
      if (typeDecl != null)
      {
         isInDifferentCU = false;
         newTypeDecl = typeDecl;
      }
      else
      {
         isInDifferentCU = true;
         astRoot = ASTResolving.createQuickFixAST(document, null);
         newTypeDecl = astRoot.findDeclaringNode(fSenderBinding.getKey());
      }
      createImportRewrite(astRoot);

      if (newTypeDecl != null)
      {
         ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

         MethodDeclaration newStub = getStub(rewrite, newTypeDecl);

         ChildListPropertyDescriptor property = ASTNodes.getBodyDeclarationsProperty(newTypeDecl);
         List<BodyDeclaration> members = (List<BodyDeclaration>)newTypeDecl.getStructuralProperty(property);

         int insertIndex;
         if (isConstructor())
         {
            insertIndex = findConstructorInsertIndex(members);
         }
         else if (!isInDifferentCU)
         {
            insertIndex = findMethodInsertIndex(members, fNode.getStartPosition());
         }
         else
         {
            insertIndex = members.size();
         }
         ListRewrite listRewriter = rewrite.getListRewrite(newTypeDecl, property);
         listRewriter.insertAt(newStub, insertIndex, null);

         return rewrite;
      }
      return null;
   }

   private MethodDeclaration getStub(ASTRewrite rewrite, ASTNode targetTypeDecl) throws CoreException
   {
      AST ast = targetTypeDecl.getAST();
      MethodDeclaration decl = ast.newMethodDeclaration();

      SimpleName newNameNode = getNewName(rewrite);

      decl.setConstructor(isConstructor());

      addNewModifiers(rewrite, targetTypeDecl, decl.modifiers());

      ArrayList<String> takenNames = new ArrayList<String>();
      addNewTypeParameters(rewrite, takenNames, decl.typeParameters());

      decl.setName(newNameNode);

      IVariableBinding[] declaredFields = fSenderBinding.getDeclaredFields();
      for (int i = 0; i < declaredFields.length; i++)
      { // avoid to take parameter names that are equal to field names
         takenNames.add(declaredFields[i].getName());
      }

      String bodyStatement = ""; //$NON-NLS-1$
      if (!isConstructor())
      {
         Type returnType = getNewMethodType(rewrite);
         decl.setReturnType2(returnType);

         boolean isVoid =
            returnType instanceof PrimitiveType
               && PrimitiveType.VOID.equals(((PrimitiveType)returnType).getPrimitiveTypeCode());
         if (!fSenderBinding.isInterface() && !isVoid)
         {
            ReturnStatement returnStatement = ast.newReturnStatement();
            returnStatement.setExpression(ASTNodeFactory.newDefaultExpression(ast, returnType, 0));
            bodyStatement =
               ASTNodes.asFormattedString(returnStatement, 0, String.valueOf('\n'), JdtExtension.get().getOptions());
         }
      }

      addNewParameters(rewrite, takenNames, decl.parameters());
      addNewExceptions(rewrite, decl.thrownExceptions());

      Block body = null;
      if (!fSenderBinding.isInterface())
      {
         body = ast.newBlock();
         String placeHolder =
            StubUtility.getMethodBodyContent(isConstructor(), fSenderBinding.getName(), newNameNode.getIdentifier(),
               bodyStatement, String.valueOf('\n'));

         if (placeHolder != null)
         {
            ReturnStatement todoNode =
               (ReturnStatement)rewrite.createStringPlaceholder(placeHolder, ASTNode.RETURN_STATEMENT);
            body.statements().add(todoNode);
         }
      }
      decl.setBody(body);

      CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings();
      if (settings.createComments && !fSenderBinding.isAnonymous())
      {
         String string = getMethodComment(fSenderBinding.getName(), decl, null, String.valueOf('\n'));

         if (string != null)
         {
            Javadoc javadoc = (Javadoc)rewrite.createStringPlaceholder(string, ASTNode.JAVADOC);
            decl.setJavadoc(javadoc);
         }
      }
      return decl;
   }

   private int findMethodInsertIndex(List<BodyDeclaration> decls, int currPos)
   {
      int nDecls = decls.size();
      for (int i = 0; i < nDecls; i++)
      {
         BodyDeclaration curr = decls.get(i);
         if (curr instanceof MethodDeclaration && currPos < curr.getStartPosition() + curr.getLength())
         {
            return i + 1;
         }
      }
      return nDecls;
   }

   private int findConstructorInsertIndex(List<BodyDeclaration> decls)
   {
      int nDecls = decls.size();
      int lastMethod = 0;
      for (int i = nDecls - 1; i >= 0; i--)
      {
         BodyDeclaration curr = decls.get(i);
         if (curr instanceof MethodDeclaration)
         {
            if (((MethodDeclaration)curr).isConstructor())
            {
               return i + 1;
            }
            lastMethod = i;
         }
      }
      return lastMethod;
   }

   protected abstract boolean isConstructor();

   protected abstract void addNewModifiers(ASTRewrite rewrite, ASTNode targetTypeDecl,
      List<IExtendedModifier> exceptions);

   protected abstract void addNewTypeParameters(ASTRewrite rewrite, List<String> takenNames, List<TypeParameter> params)
      throws CoreException;

   protected abstract void addNewParameters(ASTRewrite rewrite, List<String> takenNames,
      List<SingleVariableDeclaration> params) throws CoreException;

   protected abstract void addNewExceptions(ASTRewrite rewrite, List<Name> exceptions) throws CoreException;

   protected abstract SimpleName getNewName(ASTRewrite rewrite);

   protected abstract Type getNewMethodType(ASTRewrite rewrite) throws CoreException;

}
