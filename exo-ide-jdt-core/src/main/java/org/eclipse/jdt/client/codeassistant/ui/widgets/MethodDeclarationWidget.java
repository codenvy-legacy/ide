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
package org.eclipse.jdt.client.codeassistant.ui.widgets;

import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.DummyNameEnvirement;
import org.eclipse.jdt.client.JavaPreferencesSettings;
import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.AbstractTypeDeclaration;
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
import org.eclipse.jdt.client.text.BadLocationException;
import org.eclipse.jdt.client.text.Document;
import org.eclipse.jdt.client.text.IDocument;
import org.eclipse.jdt.client.text.IRegion;
import org.eclipse.jdt.client.text.TextUtilities;
import org.eclipse.jdt.client.text.edits.InsertEdit;
import org.eclipse.jdt.client.text.edits.MalformedTreeException;
import org.eclipse.jdt.client.text.edits.MultiTextEdit;
import org.eclipse.jdt.client.text.edits.ReplaceEdit;
import org.eclipse.jdt.client.text.edits.TextEdit;

/**
 * Completion is a declaration of a method. This kind of completion might occur in a context like <code>"new List() {si^};"</code>
 * and complete it to <code>"new List() {public int size() {} };"</code>.
 * <p>
 * The following additional context information is available for this kind of completion proposal at little extra cost:
 * <ul>
 * <li>{@link #getDeclarationSignature()} - the type signature of the type that declares the method that is being overridden or
 * implemented</li>
 * <li>{@link #getDeclarationKey()} - the unique of the type that declares the method that is being overridden or implemented</li>
 * <li>{@link #getName()} - the simple name of the method that is being overridden or implemented</li>
 * <li>{@link #getSignature()} - the method signature of the method that is being overridden or implemented</li>
 * <li>{@link #getKey()} - the method unique key of the method that is being overridden or implemented</li>
 * <li>{@link #getFlags()} - the modifiers flags of the method that is being overridden or implemented</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 2:47:43 PM 34360 2009-07-22 23:58:59Z evgen $
 */
public class MethodDeclarationWidget extends MethodRef
{
   private String fMethodName;

   private String[] fParamTypes;

   private int fUserReplacementLength = -1;
   
   private ImportRewrite importRewrite;

   /** @param proposal */
   public MethodDeclarationWidget(CompletionProposal proposal)
   {
      super(proposal);
      fMethodName = String.valueOf(proposal.getName());

      fParamTypes = Signature.getParameterTypes(String.valueOf(proposal.getSignature()));
      for (int index = 0; index < fParamTypes.length; index++)
         fParamTypes[index] = Signature.toString(fParamTypes[index]);
   }

   /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getClassSignature() */
   @Override
   protected String getClassSignature()
   {
      return "Override method in '" +String.valueOf(Signature.getSignatureSimpleName(proposal.getDeclarationSignature())) + "'";
   }

   /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getDecription() */
   @Override
   public Widget getDecription()
   {
      return null;
   }

   private CompilationUnit getRecoveredAST(IDocument document, int offset, Document recoveredDocument)
   {
      // TODO
      // CompilationUnit ast= SharedASTProvider.getAST(fCompilationUnit, SharedASTProvider.WAIT_ACTIVE_ONLY, null);
      // if (ast != null) {
      // recoveredDocument.set(document.get());
      // return ast;
      // }

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
      parser.setEnvironment(new String[]{"fersf"}, new String[]{"wfer"}, new String[]{"UTF-8"}, true);
      parser.setSource(content);
      parser.setCompilerOptions(JavaCore.getOptions());
      parser.setUnitName(new String(Signature.getSignatureSimpleName(proposal.getDeclarationSignature())));
      parser.setNameEnvironment(new DummyNameEnvirement(null));
      return (CompilationUnit)parser.createAST(new NullProgressMonitor());
   }

   protected String updateReplacementString(IDocument document, char trigger, int offset, ImportRewrite importRewrite)
      throws CoreException, BadLocationException
   {
      Document recoveredDocument = new Document();
      CompilationUnit unit = getRecoveredAST(document, offset, recoveredDocument);
      ImportRewriteContext context;
//      if (importRewrite != null)
//      {
//         context = new ContextSensitiveImportRewriteContext(unit, offset, importRewrite);
//      }
//      else
//      {
//         importRewrite = ImportRewrite.create(recoveredDocument, unit, true); // StubUtility.createImportRewrite(unit, true); //
//                                                                              // create a dummy import rewriter to have one
//         context = new ImportRewriteContext()
//         { // forces that all imports are fully qualified
//               @Override
//               public int findInContext(String qualifier, String name, int kind)
//               {
//                  return RES_NAME_CONFLICT;
//               }
//            };
//      }
      if(importRewrite == null)
      {
         importRewrite = ImportRewrite.create(recoveredDocument, unit, true);
         importRewrite.setOnDemandImportThreshold(99);
         importRewrite.setStaticOnDemandImportThreshold(99);
      }
      
      this.importRewrite = importRewrite;
      context = new ContextSensitiveImportRewriteContext(unit, offset, importRewrite);
      
      ITypeBinding declaringType = null;
      ChildListPropertyDescriptor descriptor = null;
      ASTNode node = NodeFinder.perform(unit, offset, 1);
      if (node instanceof org.eclipse.jdt.client.core.dom.AnonymousClassDeclaration)
      {
         declaringType = ((org.eclipse.jdt.client.core.dom.AnonymousClassDeclaration)node).resolveBinding();
         descriptor = org.eclipse.jdt.client.core.dom.AnonymousClassDeclaration.BODY_DECLARATIONS_PROPERTY;
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

               String indent = getIndentAt(document, proposal.getReplaceStart(), settings);
               return (IndentManipulation.changeIndent(generatedCode, generatedIndent, settings.tabWidth,
                  settings.indentWidth, indent, TextUtilities.getDefaultLineDelimiter(document)));

            }
            catch (MalformedTreeException exception)
            {
               // JavaPlugin.log(exception);
            }
            catch (BadLocationException exception)
            {
               // JavaPlugin.log(exception);
            }
         }
      }
      return "";
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

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.widgets.MethodRef#apply(org.eclipse.jdt.client.text.IDocument)
    */
   @Override
   public void apply(IDocument document)
   {
      int start = proposal.getReplaceStart();
      int length = getLength(proposal);
      try
      {
         MultiTextEdit edit = new MultiTextEdit();
         String replacementString = updateReplacementString(document, (char)0, start + length, null);
         edit.addChild(new ReplaceEdit(start, length,replacementString));
         edit.addChild(importRewrite.rewriteImports(null));
         edit.apply(document);
      }
      catch (CoreException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (BadLocationException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * Returns the replacement length of a given completion proposal. The replacement length is usually the difference between the
    * return values of <code>proposal.getReplaceEnd</code> and <code>proposal.getReplaceStart</code>, but this behavior may be
    * overridden by calling {@link #setReplacementLength(int)}.
    * 
    * @param proposal the completion proposal to get the replacement length for
    * @return the replacement length for <code>proposal</code>
    */
   protected final int getLength(CompletionProposal proposal)
   {
      int start = proposal.getReplaceStart();
      int end = proposal.getReplaceEnd();
      int length;
      if (fUserReplacementLength == -1)
      {
         length = end - start;
      }
      else
      {
         length = fUserReplacementLength;
         // extend length to begin at start
         int behindCompletion = proposal.getCompletionLocation() + 1;
         if (start < behindCompletion)
         {
            length += behindCompletion - start;
         }
      }
      return length;
   }

}
