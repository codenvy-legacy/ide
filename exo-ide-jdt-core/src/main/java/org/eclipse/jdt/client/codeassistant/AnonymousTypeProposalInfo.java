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

import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.core.Signature;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

/**
 * Proposal info that computes the javadoc lazily when it is queried.
 * 
 * @since 3.1
 */
public final class AnonymousTypeProposalInfo extends MemberProposalInfo
{

   /**
    * Creates a new proposal info.
    * 
    * @param project the java project to reference when resolving types
    * @param proposal the proposal to generate information for
    */
   public AnonymousTypeProposalInfo(CompletionProposal proposal, String projectId, String docContext)
   {
      super(proposal, projectId, docContext);
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.MemberProposalInfo#getURL()
    */
   @Override
   protected String getURL()
   {
      return docContext + Signature.toString(new String(Signature.getTypeErasure(fProposal.getDeclarationKey()))) + "&projectid=" + projectId
               + "&vfsid=" + VirtualFileSystem.getInstance().getInfo().getId() + "&isclass=true";
   }
}
