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
package org.eclipse.jdt.client.internal.text.correction;

import com.google.gwt.user.client.ui.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.codeassistant.api.IInvocationContext;
import org.eclipse.jdt.client.codeassistant.api.IProblemLocation;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.client.core.dom.BodyDeclaration;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.client.core.dom.FieldDeclaration;
import org.eclipse.jdt.client.core.dom.IBinding;
import org.eclipse.jdt.client.core.dom.IMethodBinding;
import org.eclipse.jdt.client.core.dom.ITypeBinding;
import org.eclipse.jdt.client.core.dom.Javadoc;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.Name;
import org.eclipse.jdt.client.core.dom.PrimitiveType;
import org.eclipse.jdt.client.core.dom.SimpleName;
import org.eclipse.jdt.client.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.client.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.client.core.dom.TagElement;
import org.eclipse.jdt.client.core.dom.TextElement;
import org.eclipse.jdt.client.core.dom.Type;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;
import org.eclipse.jdt.client.core.dom.TypeParameter;
import org.eclipse.jdt.client.core.dom.VariableDeclaration;
import org.eclipse.jdt.client.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.client.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.client.core.dom.rewrite.ListRewrite;

import org.eclipse.jdt.client.internal.corext.codemanipulation.ASTResolving;
import org.eclipse.jdt.client.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.client.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.client.internal.corext.dom.Bindings;
import org.eclipse.jdt.client.internal.corext.util.Strings;
import org.eclipse.jdt.client.internal.text.correction.proposals.ASTRewriteCorrectionProposal;
import org.eclipse.jdt.client.internal.text.correction.proposals.CUCorrectionProposal;
import org.eclipse.jdt.client.internal.text.correction.proposals.LinkedCorrectionProposal;
import org.eclipse.jdt.client.runtime.CoreException;
import org.eclipse.jdt.client.runtime.IStatus;
import org.eclipse.jdt.client.runtime.JavaUIStatus;
import org.exoplatform.ide.editor.shared.runtime.Assert;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.editor.shared.text.TextUtilities;
import org.exoplatform.ide.editor.shared.text.edits.InsertEdit;
import org.exoplatform.ide.editor.shared.text.edits.TextEdit;
import org.exoplatform.ide.editor.shared.text.edits.TextEditGroup;

/**
 *
 */
public class JavadocTagsSubProcessor
{

   private static final class AddJavadocCommentProposal extends CUCorrectionProposal
   {

      private final int fInsertPosition;

      private final String fComment;

      private AddJavadocCommentProposal(String name, int relevance, IDocument document, int insertPosition,
         String comment)
      {
         super(name, relevance, document, new Image(JdtClientBundle.INSTANCE.javadoc()));
         fInsertPosition = insertPosition;
         fComment = comment;
      }

      @Override
      protected void addEdits(IDocument document, TextEdit rootEdit) throws CoreException
      {
         try
         {
            String lineDelimiter = TextUtilities.getDefaultLineDelimiter(document);
            //            final IJavaProject project = getCompilationUnit().getJavaProject();
            IRegion region = document.getLineInformationOfOffset(fInsertPosition);

            String lineContent = document.get(region.getOffset(), region.getLength());
            String indentString = Strings.getIndentString(lineContent);
            String str = Strings.changeIndent(fComment, 0, indentString, lineDelimiter);
            InsertEdit edit = new InsertEdit(fInsertPosition, str);
            rootEdit.addChild(edit);
            if (fComment.charAt(fComment.length() - 1) != '\n')
            {
               rootEdit.addChild(new InsertEdit(fInsertPosition, lineDelimiter));
               rootEdit.addChild(new InsertEdit(fInsertPosition, indentString));
            }
         }
         catch (BadLocationException e)
         {
            throw new CoreException(JavaUIStatus.createError(IStatus.ERROR, e));
         }
      }
   }

   private static final class AddMissingJavadocTagProposal extends LinkedCorrectionProposal
   {

      private final BodyDeclaration fBodyDecl; // MethodDecl or TypeDecl

      private final ASTNode fMissingNode;

      public AddMissingJavadocTagProposal(String label, BodyDeclaration methodDecl, ASTNode missingNode, int relevance,
         IDocument document)
      {
         super(label, null, relevance, document, new Image(JdtClientBundle.INSTANCE.javadoc()));
         fBodyDecl = methodDecl;
         fMissingNode = missingNode;
      }

      @Override
      protected ASTRewrite getRewrite() throws CoreException
      {
         AST ast = fBodyDecl.getAST();
         ASTRewrite rewrite = ASTRewrite.create(ast);
         insertMissingJavadocTag(rewrite, fMissingNode, fBodyDecl);
         return rewrite;
      }

      private void insertMissingJavadocTag(ASTRewrite rewrite, ASTNode missingNode, BodyDeclaration bodyDecl)
      {
         AST ast = bodyDecl.getAST();
         Javadoc javadoc = bodyDecl.getJavadoc();
         if (javadoc == null)
         {
            javadoc = ast.newJavadoc();
            rewrite.set(bodyDecl, bodyDecl.getJavadocProperty(), javadoc, null);
         }

         ListRewrite tagsRewriter = rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY);

         StructuralPropertyDescriptor location = missingNode.getLocationInParent();
         TagElement newTag;
         if (location == SingleVariableDeclaration.NAME_PROPERTY)
         {
            // normal parameter
            SingleVariableDeclaration decl = (SingleVariableDeclaration)missingNode.getParent();

            String name = ((SimpleName)missingNode).getIdentifier();
            newTag = ast.newTagElement();
            newTag.setTagName(TagElement.TAG_PARAM);
            newTag.fragments().add(ast.newSimpleName(name));

            MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDecl;
            List<SingleVariableDeclaration> params = methodDeclaration.parameters();

            Set<String> sameKindLeadingNames = getPreviousParamNames(params, decl);

            List<TypeParameter> typeParams = methodDeclaration.typeParameters();
            for (int i = 0; i < typeParams.size(); i++)
            {
               String curr = '<' + typeParams.get(i).getName().getIdentifier() + '>';
               sameKindLeadingNames.add(curr);
            }
            insertTag(tagsRewriter, newTag, sameKindLeadingNames);
         }
         else if (location == TypeParameter.NAME_PROPERTY)
         {
            // type parameter
            TypeParameter typeParam = (TypeParameter)missingNode.getParent();

            String name = '<' + ((SimpleName)missingNode).getIdentifier() + '>';
            newTag = ast.newTagElement();
            newTag.setTagName(TagElement.TAG_PARAM);
            TextElement text = ast.newTextElement();
            text.setText(name);
            newTag.fragments().add(text);
            List<TypeParameter> params;
            if (bodyDecl instanceof TypeDeclaration)
            {
               params = ((TypeDeclaration)bodyDecl).typeParameters();
            }
            else
            {
               params = ((MethodDeclaration)bodyDecl).typeParameters();
            }
            insertTag(tagsRewriter, newTag, getPreviousTypeParamNames(params, typeParam));
         }
         else if (location == MethodDeclaration.RETURN_TYPE2_PROPERTY)
         {
            newTag = ast.newTagElement();
            newTag.setTagName(TagElement.TAG_RETURN);
            insertTag(tagsRewriter, newTag, null);
         }
         else if (location == MethodDeclaration.THROWN_EXCEPTIONS_PROPERTY)
         {
            newTag = ast.newTagElement();
            newTag.setTagName(TagElement.TAG_THROWS);
            TextElement excNode = ast.newTextElement();
            excNode.setText(ASTNodes.asString(missingNode));
            newTag.fragments().add(excNode);
            List<Name> exceptions = ((MethodDeclaration)bodyDecl).thrownExceptions();
            insertTag(tagsRewriter, newTag, getPreviousExceptionNames(exceptions, missingNode));
         }
         else
         {
            Assert.isTrue(false, "AddMissingJavadocTagProposal: unexpected node location"); //$NON-NLS-1$
            return;
         }

         TextElement textElement = ast.newTextElement();
         textElement.setText(""); //$NON-NLS-1$
         newTag.fragments().add(textElement);

         //         addLinkedPosition(rewrite.track(textElement), false, "comment_start"); //$NON-NLS-1$

         if (bodyDecl.getJavadoc() == null)
         {
            // otherwise the linked position spans over a line delimiter
            newTag.fragments().add(ast.newTextElement());
         }
      }
   }

   private static final class AddAllMissingJavadocTagsProposal extends LinkedCorrectionProposal
   {

      private final BodyDeclaration fBodyDecl;

      public AddAllMissingJavadocTagsProposal(String label, BodyDeclaration bodyDecl, int relevance, IDocument document)
      {
         super(label, null, relevance, document, new Image(JdtClientBundle.INSTANCE.javadoc()));
         fBodyDecl = bodyDecl;
      }

      @Override
      protected ASTRewrite getRewrite() throws CoreException
      {
         ASTRewrite rewrite = ASTRewrite.create(fBodyDecl.getAST());
         if (fBodyDecl instanceof MethodDeclaration)
         {
            insertAllMissingMethodTags(rewrite, (MethodDeclaration)fBodyDecl);
         }
         else
         {
            insertAllMissingTypeTags(rewrite, (TypeDeclaration)fBodyDecl);
         }
         return rewrite;
      }

      private void insertAllMissingMethodTags(ASTRewrite rewriter, MethodDeclaration methodDecl)
      {
         AST ast = methodDecl.getAST();
         Javadoc javadoc = methodDecl.getJavadoc();
         ListRewrite tagsRewriter = rewriter.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY);

         List<TypeParameter> typeParams = methodDecl.typeParameters();
         ASTNode root = methodDecl.getRoot();
         if (root instanceof CompilationUnit)
         {
            ASTNode typeRoot = ((CompilationUnit)root).getParent();
            if (typeRoot != null && !StubUtility.shouldGenerateMethodTypeParameterTags())
               typeParams = Collections.emptyList();
         }
         List<String> typeParamNames = new ArrayList<String>();
         for (int i = typeParams.size() - 1; i >= 0; i--)
         {
            TypeParameter decl = typeParams.get(i);
            String name = '<' + decl.getName().getIdentifier() + '>';
            if (findTag(javadoc, TagElement.TAG_PARAM, name) == null)
            {
               TagElement newTag = ast.newTagElement();
               newTag.setTagName(TagElement.TAG_PARAM);
               TextElement text = ast.newTextElement();
               text.setText(name);
               newTag.fragments().add(text);
               insertTabStop(rewriter, newTag.fragments(), "typeParam" + i); //$NON-NLS-1$
               insertTag(tagsRewriter, newTag, getPreviousTypeParamNames(typeParams, decl));
            }
            typeParamNames.add(name);
         }
         List<SingleVariableDeclaration> params = methodDecl.parameters();
         for (int i = params.size() - 1; i >= 0; i--)
         {
            SingleVariableDeclaration decl = params.get(i);
            String name = decl.getName().getIdentifier();
            if (findTag(javadoc, TagElement.TAG_PARAM, name) == null)
            {
               TagElement newTag = ast.newTagElement();
               newTag.setTagName(TagElement.TAG_PARAM);
               newTag.fragments().add(ast.newSimpleName(name));
               insertTabStop(rewriter, newTag.fragments(), "methParam" + i); //$NON-NLS-1$
               Set<String> sameKindLeadingNames = getPreviousParamNames(params, decl);
               sameKindLeadingNames.addAll(typeParamNames);
               insertTag(tagsRewriter, newTag, sameKindLeadingNames);
            }
         }
         if (!methodDecl.isConstructor())
         {
            Type type = methodDecl.getReturnType2();
            if (!type.isPrimitiveType() || (((PrimitiveType)type).getPrimitiveTypeCode() != PrimitiveType.VOID))
            {
               if (findTag(javadoc, TagElement.TAG_RETURN, null) == null)
               {
                  TagElement newTag = ast.newTagElement();
                  newTag.setTagName(TagElement.TAG_RETURN);
                  insertTabStop(rewriter, newTag.fragments(), "return"); //$NON-NLS-1$
                  insertTag(tagsRewriter, newTag, null);
               }
            }
         }
         List<Name> thrownExceptions = methodDecl.thrownExceptions();
         for (int i = thrownExceptions.size() - 1; i >= 0; i--)
         {
            Name exception = thrownExceptions.get(i);
            ITypeBinding binding = exception.resolveTypeBinding();
            if (binding != null)
            {
               String name = binding.getName();
               if (findThrowsTag(javadoc, name) == null)
               {
                  TagElement newTag = ast.newTagElement();
                  newTag.setTagName(TagElement.TAG_THROWS);
                  TextElement excNode = ast.newTextElement();
                  excNode.setText(ASTNodes.asString(exception));
                  newTag.fragments().add(excNode);
                  insertTabStop(rewriter, newTag.fragments(), "exception" + i); //$NON-NLS-1$
                  insertTag(tagsRewriter, newTag, getPreviousExceptionNames(thrownExceptions, exception));
               }
            }
         }
      }

      private void insertAllMissingTypeTags(ASTRewrite rewriter, TypeDeclaration typeDecl)
      {
         AST ast = typeDecl.getAST();
         Javadoc javadoc = typeDecl.getJavadoc();
         ListRewrite tagsRewriter = rewriter.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY);

         List<TypeParameter> typeParams = typeDecl.typeParameters();
         for (int i = typeParams.size() - 1; i >= 0; i--)
         {
            TypeParameter decl = typeParams.get(i);
            String name = '<' + decl.getName().getIdentifier() + '>';
            if (findTag(javadoc, TagElement.TAG_PARAM, name) == null)
            {
               TagElement newTag = ast.newTagElement();
               newTag.setTagName(TagElement.TAG_PARAM);
               TextElement text = ast.newTextElement();
               text.setText(name);
               newTag.fragments().add(text);
               insertTabStop(rewriter, newTag.fragments(), "typeParam" + i); //$NON-NLS-1$
               insertTag(tagsRewriter, newTag, getPreviousTypeParamNames(typeParams, decl));
            }
         }
      }

      private void insertTabStop(ASTRewrite rewriter, List<ASTNode> fragments, String linkedName)
      {
         TextElement textElement = rewriter.getAST().newTextElement();
         textElement.setText(""); //$NON-NLS-1$
         fragments.add(textElement);
         //         addLinkedPosition(rewriter.track(textElement), false, linkedName);
      }

   }

   public static void getMissingJavadocTagProposals(IInvocationContext context, IProblemLocation problem,
      Collection<ICommandAccess> proposals)
   {
      ASTNode node = problem.getCoveringNode(context.getASTRoot());
      if (node == null)
      {
         return;
      }
      node = ASTNodes.getNormalizedNode(node);

      BodyDeclaration bodyDeclaration = ASTResolving.findParentBodyDeclaration(node);
      if (bodyDeclaration == null)
      {
         return;
      }
      Javadoc javadoc = bodyDeclaration.getJavadoc();
      if (javadoc == null)
      {
         return;
      }

      String label;
      StructuralPropertyDescriptor location = node.getLocationInParent();
      if (location == SingleVariableDeclaration.NAME_PROPERTY)
      {
         label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_addjavadoc_paramtag_description();
         if (node.getParent().getLocationInParent() != MethodDeclaration.PARAMETERS_PROPERTY)
         {
            return; // paranoia checks
         }
      }
      else if (location == TypeParameter.NAME_PROPERTY)
      {
         label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_addjavadoc_paramtag_description();
         StructuralPropertyDescriptor parentLocation = node.getParent().getLocationInParent();
         if (parentLocation != MethodDeclaration.TYPE_PARAMETERS_PROPERTY
            && parentLocation != TypeDeclaration.TYPE_PARAMETERS_PROPERTY)
         {
            return; // paranoia checks
         }
      }
      else if (location == MethodDeclaration.RETURN_TYPE2_PROPERTY)
      {
         label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_addjavadoc_returntag_description();
      }
      else if (location == MethodDeclaration.THROWN_EXCEPTIONS_PROPERTY)
      {
         label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_addjavadoc_throwstag_description();
      }
      else
      {
         return;
      }
      ASTRewriteCorrectionProposal proposal =
         new AddMissingJavadocTagProposal(label, bodyDeclaration, node, 4, context.getDocument());
      proposals.add(proposal);

      String label2 = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_addjavadoc_allmissing_description();
      ASTRewriteCorrectionProposal addAllMissing =
         new AddAllMissingJavadocTagsProposal(label2, bodyDeclaration, 5, context.getDocument());
      proposals.add(addAllMissing);
   }

   public static void getUnusedAndUndocumentedParameterOrExceptionProposals(IInvocationContext context,
      IProblemLocation problem, Collection<ICommandAccess> proposals)
   {

      if (!JavaCore.ENABLED.equals(JavaCore.getOption(JavaCore.COMPILER_DOC_COMMENT_SUPPORT)))
      {
         return;
      }

      boolean isUnusedParam = problem.getProblemId() == IProblem.ArgumentIsNeverUsed;
      String key =
         isUnusedParam ? JavaCore.COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE
            : JavaCore.COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_INCLUDE_DOC_COMMENT_REFERENCE;

      if (!JavaCore.ENABLED.equals(JavaCore.getOption(key)))
      {
         return;
      }

      ASTNode node = problem.getCoveringNode(context.getASTRoot());
      if (node == null)
      {
         return;
      }

      MethodDeclaration methodDecl = (MethodDeclaration)ASTNodes.getParent(node, ASTNode.METHOD_DECLARATION);
      if (methodDecl == null || methodDecl.resolveBinding() == null)
      {
         return;
      }

      String label;
      if (isUnusedParam)
      {
         label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_document_parameter_description();
      }
      else
      {
         label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_document_exception_description();
      }
      ASTRewriteCorrectionProposal proposal =
         new AddMissingJavadocTagProposal(label, methodDecl, node, 1, context.getDocument());
      proposals.add(proposal);
   }

   public static void getMissingJavadocCommentProposals(IInvocationContext context, IProblemLocation problem,
      Collection<ICommandAccess> proposals) throws CoreException
   {
      ASTNode node = problem.getCoveringNode(context.getASTRoot());
      if (node == null)
      {
         return;
      }
      BodyDeclaration declaration = ASTResolving.findParentBodyDeclaration(node);
      if (declaration == null)
      {
         return;
      }
      ITypeBinding binding = Bindings.getBindingOfParentType(declaration);
      if (binding == null)
      {
         return;
      }

      if (declaration instanceof MethodDeclaration)
      {
         MethodDeclaration methodDecl = (MethodDeclaration)declaration;
         IMethodBinding methodBinding = methodDecl.resolveBinding();
         IMethodBinding overridden = null;
         if (methodBinding != null)
         {
            overridden = Bindings.findOverriddenMethod(methodBinding, true);
         }

         String string =
            LinkedCorrectionProposal.getMethodComment(binding.getName(), methodDecl, overridden, String.valueOf('\n'));
         //            CodeGeneration.getMethodComment(cu, binding.getName(), methodDecl, overridden, );
         if (string != null)
         {
            String label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_addjavadoc_method_description();
            proposals.add(new AddJavadocCommentProposal(label, 1, context.getDocument(),
               declaration.getStartPosition(), string));
         }
      }
      else if (declaration instanceof AbstractTypeDeclaration)
      {
         String typeQualifiedName = Bindings.getTypeQualifiedName(binding);
         String[] typeParamNames;
         if (declaration instanceof TypeDeclaration)
         {
            List<TypeParameter> typeParams = ((TypeDeclaration)declaration).typeParameters();
            typeParamNames = new String[typeParams.size()];
            for (int i = 0; i < typeParamNames.length; i++)
            {
               typeParamNames[i] = (typeParams.get(i)).getName().getIdentifier();
            }
         }
         else
         {
            typeParamNames = new String[0];
         }

         String string = StubUtility.getTypeComment(typeQualifiedName, typeParamNames, String.valueOf('\n'));
         if (string != null)
         {
            String label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_addjavadoc_type_description();
            proposals.add(new AddJavadocCommentProposal(label, 1, context.getDocument(),
               declaration.getStartPosition(), string));
         }
      }
      else if (declaration instanceof FieldDeclaration)
      {
         String comment = "/**\n *\n */\n"; //$NON-NLS-1$
         List<VariableDeclarationFragment> fragments = ((FieldDeclaration)declaration).fragments();
         if (fragments != null && fragments.size() > 0)
         {
            VariableDeclaration decl = fragments.get(0);
            String fieldName = decl.getName().getIdentifier();
            String typeName = binding.getName();

            comment = StubUtility.getFieldComment(typeName, fieldName, String.valueOf('\n'));
         }
         if (comment != null)
         {
            String label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_addjavadoc_field_description();
            proposals.add(new AddJavadocCommentProposal(label, 1, context.getDocument(),
               declaration.getStartPosition(), comment));
         }
      }
      else if (declaration instanceof EnumConstantDeclaration)
      {
         EnumConstantDeclaration enumDecl = (EnumConstantDeclaration)declaration;
         String id = enumDecl.getName().getIdentifier();
         String comment = StubUtility.getFieldComment(binding.getName(), id, String.valueOf('\n'));
         String label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_addjavadoc_enumconst_description();
         proposals.add(new AddJavadocCommentProposal(label, 1, context.getDocument(), declaration.getStartPosition(),
            comment));
      }
   }

   public static Set<String> getPreviousTypeParamNames(List<TypeParameter> typeParams, ASTNode missingNode)
   {
      Set<String> previousNames = new HashSet<String>();
      for (int i = 0; i < typeParams.size(); i++)
      {
         TypeParameter curr = typeParams.get(i);
         if (curr == missingNode)
         {
            return previousNames;
         }
         previousNames.add('<' + curr.getName().getIdentifier() + '>');
      }
      return previousNames;
   }

   private static Set<String> getPreviousParamNames(List<SingleVariableDeclaration> params, ASTNode missingNode)
   {
      Set<String> previousNames = new HashSet<String>();
      for (int i = 0; i < params.size(); i++)
      {
         SingleVariableDeclaration curr = params.get(i);
         if (curr == missingNode)
         {
            return previousNames;
         }
         previousNames.add(curr.getName().getIdentifier());
      }
      return previousNames;
   }

   private static Set<String> getPreviousExceptionNames(List<Name> list, ASTNode missingNode)
   {
      Set<String> previousNames = new HashSet<String>();
      for (int i = 0; i < list.size() && missingNode != list.get(i); i++)
      {
         Name curr = list.get(i);
         previousNames.add(ASTNodes.getSimpleNameIdentifier(curr));
      }
      return previousNames;
   }

   public static TagElement findTag(Javadoc javadoc, String name, String arg)
   {
      List<TagElement> tags = javadoc.tags();
      int nTags = tags.size();
      for (int i = 0; i < nTags; i++)
      {
         TagElement curr = tags.get(i);
         if (name.equals(curr.getTagName()))
         {
            if (arg != null)
            {
               String argument = getArgument(curr);
               if (arg.equals(argument))
               {
                  return curr;
               }
            }
            else
            {
               return curr;
            }
         }
      }
      return null;
   }

   public static TagElement findParamTag(Javadoc javadoc, String arg)
   {
      List<TagElement> tags = javadoc.tags();
      int nTags = tags.size();
      for (int i = 0; i < nTags; i++)
      {
         TagElement curr = tags.get(i);
         String currName = curr.getTagName();
         if (TagElement.TAG_PARAM.equals(currName))
         {
            String argument = getArgument(curr);
            if (arg.equals(argument))
            {
               return curr;
            }
         }
      }
      return null;
   }

   public static TagElement findThrowsTag(Javadoc javadoc, String arg)
   {
      List<TagElement> tags = javadoc.tags();
      int nTags = tags.size();
      for (int i = 0; i < nTags; i++)
      {
         TagElement curr = tags.get(i);
         String currName = curr.getTagName();
         if (TagElement.TAG_THROWS.equals(currName) || TagElement.TAG_EXCEPTION.equals(currName))
         {
            String argument = getArgument(curr);
            if (arg.equals(argument))
            {
               return curr;
            }
         }
      }
      return null;
   }

   public static void insertTag(ListRewrite rewriter, TagElement newElement, Set<String> sameKindLeadingNames)
   {
      insertTag(rewriter, newElement, sameKindLeadingNames, null);
   }

   public static void insertTag(ListRewrite rewriter, TagElement newElement, Set<String> sameKindLeadingNames,
      TextEditGroup groupDescription)
   {
      List<? extends ASTNode> tags = rewriter.getRewrittenList();

      String insertedTagName = newElement.getTagName();

      ASTNode after = null;
      int tagRanking = getTagRanking(insertedTagName);
      for (int i = tags.size() - 1; i >= 0; i--)
      {
         TagElement curr = (TagElement)tags.get(i);
         String tagName = curr.getTagName();
         if (tagName == null || tagRanking > getTagRanking(tagName))
         {
            after = curr;
            break;
         }
         if (sameKindLeadingNames != null && isSameTag(insertedTagName, tagName))
         {
            String arg = getArgument(curr);
            if (arg != null && sameKindLeadingNames.contains(arg))
            {
               after = curr;
               break;
            }
         }
      }
      if (after != null)
      {
         rewriter.insertAfter(newElement, after, groupDescription);
      }
      else
      {
         rewriter.insertFirst(newElement, groupDescription);
      }
   }

   private static boolean isSameTag(String insertedTagName, String tagName)
   {
      if (insertedTagName.equals(tagName))
      {
         return true;
      }
      if (TagElement.TAG_EXCEPTION.equals(tagName))
      {
         return TagElement.TAG_THROWS.equals(insertedTagName);
      }
      return false;
   }

   private static String[] TAG_ORDER = { // see http://java.sun.com/j2se/javadoc/writingdoccomments/index.html#orderoftags
      TagElement.TAG_AUTHOR, TagElement.TAG_VERSION, TagElement.TAG_PARAM, TagElement.TAG_RETURN,
         TagElement.TAG_THROWS, // synonym to TAG_EXCEPTION
         TagElement.TAG_SEE, TagElement.TAG_SINCE, TagElement.TAG_SERIAL, TagElement.TAG_DEPRECATED};

   private static int getTagRanking(String tagName)
   {
      if (tagName.equals(TagElement.TAG_EXCEPTION))
      {
         tagName = TagElement.TAG_THROWS;
      }
      for (int i = 0; i < TAG_ORDER.length; i++)
      {
         if (tagName.equals(TAG_ORDER[i]))
         {
            return i;
         }
      }
      return TAG_ORDER.length;
   }

   private static String getArgument(TagElement curr)
   {
      List<? extends ASTNode> fragments = curr.fragments();
      if (!fragments.isEmpty())
      {
         Object first = fragments.get(0);
         if (first instanceof Name)
         {
            return ASTNodes.getSimpleNameIdentifier((Name)first);
         }
         else if (first instanceof TextElement && TagElement.TAG_PARAM.equals(curr.getTagName()))
         {
            String text = ((TextElement)first).getText();
            if ("<".equals(text) && fragments.size() >= 3) { //$NON-NLS-1$
               Object second = fragments.get(1);
               Object third = fragments.get(2);
               if (second instanceof Name && third instanceof TextElement && ">".equals(((TextElement)third).getText())) { //$NON-NLS-1$
                  return '<' + ASTNodes.getSimpleNameIdentifier((Name)second) + '>';
               }
            }
            else if (text.startsWith(String.valueOf('<')) && text.endsWith(String.valueOf('>')) && text.length() > 2)
            {
               return text.substring(1, text.length() - 1);
            }
         }
      }
      return null;
   }

   public static void getRemoveJavadocTagProposals(IInvocationContext context, IProblemLocation problem,
      Collection<ICommandAccess> proposals)
   {
      ASTNode node = problem.getCoveringNode(context.getASTRoot());
      while (node != null && !(node instanceof TagElement))
      {
         node = node.getParent();
      }
      if (node == null)
      {
         return;
      }
      ASTRewrite rewrite = ASTRewrite.create(node.getAST());
      rewrite.remove(node, null);

      String label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_removetag_description();
      Image image = new Image(JdtClientBundle.INSTANCE.delete_obj());
      proposals.add(new ASTRewriteCorrectionProposal(label, rewrite, 5, context.getDocument(), image));
   }

   public static void getInvalidQualificationProposals(IInvocationContext context, IProblemLocation problem,
      Collection<ICommandAccess> proposals)
   {
      ASTNode node = problem.getCoveringNode(context.getASTRoot());
      if (!(node instanceof Name))
      {
         return;
      }
      Name name = (Name)node;
      IBinding binding = name.resolveBinding();
      if (!(binding instanceof ITypeBinding))
      {
         return;
      }
      ITypeBinding typeBinding = (ITypeBinding)binding;

      AST ast = node.getAST();
      ASTRewrite rewrite = ASTRewrite.create(ast);
      rewrite.replace(name, ast.newName(typeBinding.getQualifiedName()), null);

      String label = CorrectionMessages.INSTANCE.JavadocTagsSubProcessor_qualifylinktoinner_description();
      Image image = new Image(JdtClientBundle.INSTANCE.correction_change());
      ASTRewriteCorrectionProposal proposal =
         new ASTRewriteCorrectionProposal(label, rewrite, 5, context.getDocument(), image);

      proposals.add(proposal);
   }
}
