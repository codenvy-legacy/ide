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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.codeassistant.api.IInvocationContext;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.rewrite.ASTRewrite;

import org.eclipse.jdt.client.internal.corext.dom.LinkedNodeFinder;
import org.eclipse.jdt.client.internal.text.correction.CorrectionMessages;
import org.eclipse.jdt.client.runtime.CoreException;
import org.exoplatform.ide.editor.shared.text.IDocument;

/**
 * Renames the primary type to be compatible with the name of the compilation unit.
 * All constructors and local references to the type are renamed as well.
 */
public class CorrectMainTypeNameProposal extends ASTRewriteCorrectionProposal
{

   private final String fOldName;

   private final String fNewName;

   private final IInvocationContext fContext;

   /**
    * Constructor for CorrectTypeNameProposal.
    * @param cu the compilation unit
    * @param context the invocation contect
    * @param oldTypeName the old type name
    * @param newTypeName the new type name
    * @param relevance the relevance
    */
   public CorrectMainTypeNameProposal(IInvocationContext context, String oldTypeName, String newTypeName,
      int relevance, IDocument document)
   {
      super("", null, relevance, document, null); //$NON-NLS-1$
      fContext = context;

      setDisplayName(CorrectionMessages.INSTANCE.ReorgCorrectionsSubProcessor_renametype_description(newTypeName));
      setImage(new Image(JdtClientBundle.INSTANCE.correction_change()));

      fOldName = oldTypeName;
      fNewName = newTypeName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jdt.internal.ui.text.correction.ASTRewriteCorrectionProposal#getRewrite()
    */
   @Override
   protected ASTRewrite getRewrite() throws CoreException
   {
      CompilationUnit astRoot = fContext.getASTRoot();

      AST ast = astRoot.getAST();
      ASTRewrite rewrite = ASTRewrite.create(ast);

      AbstractTypeDeclaration decl = findTypeDeclaration(astRoot.types(), fOldName);
      if (decl != null)
      {
         ASTNode[] sameNodes = LinkedNodeFinder.findByNode(astRoot, decl.getName());
         for (int i = 0; i < sameNodes.length; i++)
         {
            rewrite.replace(sameNodes[i], ast.newSimpleName(fNewName), null);
         }
      }
      return rewrite;
   }

   private AbstractTypeDeclaration findTypeDeclaration(List<AbstractTypeDeclaration> types, String name)
   {
      for (Iterator<AbstractTypeDeclaration> iter = types.iterator(); iter.hasNext();)
      {
         AbstractTypeDeclaration decl = iter.next();
         if (name.equals(decl.getName().getIdentifier()))
         {
            return decl;
         }
      }
      return null;
   }

}
