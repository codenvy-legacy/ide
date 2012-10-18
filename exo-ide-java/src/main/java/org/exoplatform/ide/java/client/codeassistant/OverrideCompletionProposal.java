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
package org.exoplatform.ide.java.client.codeassistant;

import org.exoplatform.ide.java.client.JavaPreferencesSettings;
import org.exoplatform.ide.java.client.codeassistant.ui.StyledString;
import org.exoplatform.ide.java.client.core.JavaCore;
import org.exoplatform.ide.java.client.core.compiler.CharOperation;
import org.exoplatform.ide.java.client.core.dom.AST;
import org.exoplatform.ide.java.client.core.dom.ASTNode;
import org.exoplatform.ide.java.client.core.dom.ASTParser;
import org.exoplatform.ide.java.client.core.dom.AbstractTypeDeclaration;
import org.exoplatform.ide.java.client.core.dom.AnonymousClassDeclaration;
import org.exoplatform.ide.java.client.core.dom.ChildListPropertyDescriptor;
import org.exoplatform.ide.java.client.core.dom.CompilationUnit;
import org.exoplatform.ide.java.client.core.dom.IMethodBinding;
import org.exoplatform.ide.java.client.core.dom.ITypeBinding;
import org.exoplatform.ide.java.client.core.dom.MethodDeclaration;
import org.exoplatform.ide.java.client.core.dom.NodeFinder;
import org.exoplatform.ide.java.client.core.dom.rewrite.ASTRewrite;
import org.exoplatform.ide.java.client.core.dom.rewrite.ITrackedNodePosition;
import org.exoplatform.ide.java.client.core.dom.rewrite.ImportRewrite;
import org.exoplatform.ide.java.client.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.exoplatform.ide.java.client.core.dom.rewrite.ListRewrite;
import org.exoplatform.ide.java.client.core.formatter.IndentManipulation;
import org.exoplatform.ide.java.client.internal.corext.codemanipulation.CodeGenerationSettings;
import org.exoplatform.ide.java.client.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.exoplatform.ide.java.client.internal.corext.codemanipulation.StubUtility;
import org.exoplatform.ide.java.client.internal.corext.codemanipulation.StubUtility2;
import org.exoplatform.ide.java.client.internal.corext.dom.Bindings;
import org.exoplatform.ide.runtime.Assert;
import org.exoplatform.ide.runtime.CoreException;
import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.DocumentImpl;
import org.exoplatform.ide.text.Region;
import org.exoplatform.ide.text.TextUtilities;
import org.exoplatform.ide.text.edits.MalformedTreeException;

public class OverrideCompletionProposal extends JavaTypeCompletionProposal
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
   public CharSequence getPrefixCompletionText(Document document, int completionOffset)
   {
      return fMethodName;
   }

   private CompilationUnit getRecoveredAST(Document document, int offset, Document recoveredDocument)
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
      return (CompilationUnit)parser.createAST();
   }

   /*
    * @see JavaTypeCompletionProposal#updateReplacementString(IDocument,char,int,ImportRewrite)
    */
   @Override
   protected boolean updateReplacementString(Document document, char trigger, int offset, ImportRewrite importRewrite)
      throws CoreException, BadLocationException
   {
      Document recoveredDocument = new DocumentImpl();
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
               e.printStackTrace(); //NOSONAR
            }
            catch (BadLocationException e)
            {
               e.printStackTrace();//NOSONAHR
            }
         }
      }
      return true;
   }

   private static String getIndentAt(Document document, int offset, CodeGenerationSettings settings)
   {
      try
      {
         Region region = document.getLineInformationOfOffset(offset);
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
