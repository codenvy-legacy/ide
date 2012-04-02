/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.codeassistant;

import org.eclipse.jdt.client.TypeInfoStorage;
import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.core.IJavaElement;
import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.internal.corext.util.SignatureUtil;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

/**
 * Proposal info that computes the javadoc lazily when it is queried.
 * 
 * @since 3.1
 */
public final class TypeProposalInfo extends MemberProposalInfo
{

   /**
    * Creates a new proposal info.
    * 
    * @param project the java project to reference when resolving types
    * @param proposal the proposal to generate information for
    */
   public TypeProposalInfo(CompletionProposal proposal, String projectId, String docContext)
   {
      super(proposal, projectId, docContext);
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.MemberProposalInfo#getURL()
    */
   @Override
   protected String getURL()
   {
      return docContext + Signature.toString(new String(fProposal.getSignature())) + "&projectid=" + projectId
         + "&vfsid=" + VirtualFileSystem.getInstance().getInfo().getId() + "&isclass=true";

   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.MemberProposalInfo#getJavaElement()
    */
   @Override
   public IJavaElement getJavaElement()
   {
      String fqn = String.valueOf(SignatureUtil.stripSignatureToFQN(String.valueOf(fProposal.getSignature())));
      return TypeInfoStorage.get().getTypeByFqn(fqn);
   }

}
