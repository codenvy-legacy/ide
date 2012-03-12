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
package org.eclipse.jdt.client.codeassistant;

import org.eclipse.jdt.client.JavaPreferencesSettings;
import org.eclipse.jdt.client.codeassistant.api.ICompletionProposalExtension4;
import org.eclipse.jdt.client.codeassistant.ui.StyledString;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.client.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.client.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.IMethodBinding;
import org.eclipse.jdt.client.core.dom.ITypeBinding;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.NodeFinder;
import org.eclipse.jdt.client.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.client.core.dom.rewrite.ITrackedNodePosition;
import org.eclipse.jdt.client.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.client.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.client.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.client.core.formatter.IndentManipulation;
import org.eclipse.jdt.client.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.client.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.jdt.client.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.client.internal.corext.codemanipulation.StubUtility2;
import org.eclipse.jdt.client.internal.corext.dom.Bindings;
import org.eclipse.jdt.client.runtime.CoreException;
import org.eclipse.jdt.client.runtime.NullProgressMonitor;
import org.exoplatform.ide.editor.runtime.Assert;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.Document;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.IRegion;
import org.exoplatform.ide.editor.text.TextUtilities;
import org.exoplatform.ide.editor.text.edits.MalformedTreeException;

public class OverrideCompletionProposal extends JavaTypeCompletionProposal implements ICompletionProposalExtension4
{

   private String fMethodName;

   private String[] fParamTypes;

   public OverrideCompletionProposal(String methodName, String[] paramTypes, int start, int length,
      StyledString displayName, String completionProposal, JavaContentAssistInvocationContext context)
   {
      super(completionProposal, start, length, null, displayName, 0, null, context);
      Assert.isNotNull(methodName);
      Assert.isNotNull(paramTypes);

      fParamTypes = paramTypes;
      fMethodName = methodName;
      StringBuffer buffer = new StringBuffer();
      buffer.append(completionProposal);
      buffer.append(" {};"); //$NON-NLS-1$

      setReplacementString(buffer.toString());
   }

   /*
    * @see
    * org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getPrefixCompletionText(org.eclipse.jface.text.IDocument
    * ,int)
    */
   @Override
   public CharSequence getPrefixCompletionText(IDocument document, int completionOffset)
   {
      return fMethodName;
   }

   private CompilationUnit getRecoveredAST(IDocument document, int offset, Document recoveredDocument)
   {
      CompilationUnit ast = fInvocationContext.getCompilationUnit();
      if (ast != null)
      {
         recoveredDocument.set(document.get());
         return ast;
      }

      char[] content = document.get().toCharArray();

      // clear prefix to avoid compile errors
      int index = offset - 1;
      while (index >= 0 && CharOperation.isJavaIdentifierPart(content[index]))
      {
         content[index] = ' ';
         index--;
      }

      recoveredDocument.set(new String(content));

      final ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setResolveBindings(true);
      parser.setStatementsRecovery(true);
      parser.setSource(content);
      return (CompilationUnit)parser.createAST(new NullProgressMonitor());
   }

   /*
    * @see JavaTypeCompletionProposal#updateReplacementString(IDocument,char,int,ImportRewrite)
    */
   @Override
   protected boolean updateReplacementString(IDocument document, char trigger, int offset, ImportRewrite importRewrite)
      throws CoreException, BadLocationException
   {
      Document recoveredDocument = new Document();
      CompilationUnit unit = getRecoveredAST(document, offset, recoveredDocument);
      ImportRewriteContext context;
      if (importRewrite != null)
      {
         context = new ContextSensitiveImportRewriteContext(unit, offset, importRewrite);
      }
      else
      {
         importRewrite = StubUtility.createImportRewrite(document, unit, true); // create a dummy import rewriter to have one
         context = new ImportRewriteContext()
         { // forces that all imports are fully qualified
               @Override
               public int findInContext(String qualifier, String name, int kind)
               {
                  return RES_NAME_CONFLICT;
               }
            };
      }

      ITypeBinding declaringType = null;
      ChildListPropertyDescriptor descriptor = null;
      ASTNode node = NodeFinder.perform(unit, offset, 1);
      if (node instanceof AnonymousClassDeclaration)
      {
         declaringType = ((AnonymousClassDeclaration)node).resolveBinding();
         descriptor = AnonymousClassDeclaration.BODY_DECLARATIONS_PROPERTY;
      }
      else if (node instanceof AbstractTypeDeclaration)
      {
         AbstractTypeDeclaration declaration = (AbstractTypeDeclaration)node;
         descriptor = declaration.getBodyDeclarationsProperty();
         declaringType = declaration.resolveBinding();
      }
      if (declaringType != null)
      {
         ASTRewrite rewrite = ASTRewrite.create(unit.getAST());
         IMethodBinding methodToOverride = Bindings.findMethodInHierarchy(declaringType, fMethodName, fParamTypes);
         if (methodToOverride == null && declaringType.isInterface())
         {
            methodToOverride =
               Bindings.findMethodInType(
                  node.getAST().resolveWellKnownType("java.lang.Object"), fMethodName, fParamTypes); //$NON-NLS-1$
         }
         if (methodToOverride != null)
         {
            CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings();
            MethodDeclaration stub =
               StubUtility2.createImplementationStub(rewrite, importRewrite, context, methodToOverride,
                  declaringType.getName(), settings, declaringType.isInterface());
            ListRewrite rewriter = rewrite.getListRewrite(node, descriptor);
            rewriter.insertFirst(stub, null);

            ITrackedNodePosition position = rewrite.track(stub);
            try
            {
               rewrite.rewriteAST(recoveredDocument, JavaCore.getOptions()).apply(recoveredDocument);

               String generatedCode = recoveredDocument.get(position.getStartPosition(), position.getLength());
               int generatedIndent =
                  IndentManipulation.measureIndentUnits(
                     getIndentAt(recoveredDocument, position.getStartPosition(), settings), settings.tabWidth,
                     settings.indentWidth);

               String indent = getIndentAt(document, getReplacementOffset(), settings);
               setReplacementString(IndentManipulation.changeIndent(generatedCode, generatedIndent, settings.tabWidth,
                  settings.indentWidth, indent, TextUtilities.getDefaultLineDelimiter(document)));

            }
            catch (MalformedTreeException e)
            {
               e.printStackTrace();
            }
            catch (BadLocationException e)
            {
               e.printStackTrace();
            }
         }
      }
      return true;
   }

   private static String getIndentAt(IDocument document, int offset, CodeGenerationSettings settings)
   {
      try
      {
         IRegion region = document.getLineInformationOfOffset(offset);
         return IndentManipulation.extractIndentString(document.get(region.getOffset(), region.getLength()),
            settings.tabWidth, settings.indentWidth);
      }
      catch (BadLocationException e)
      {
         return ""; //$NON-NLS-1$
      }
   }

   /*
    * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension4#isAutoInsertable()
    */
   public boolean isAutoInsertable()
   {
      return false;
   }
}
