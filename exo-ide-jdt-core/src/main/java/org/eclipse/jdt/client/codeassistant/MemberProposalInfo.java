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

import com.google.gwt.user.client.ui.Frame;

import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.core.IJavaElement;
import org.exoplatform.ide.editor.runtime.Assert;

/**
 * Proposal info that computes the javadoc lazily when it is queried.
 * 
 * @since 3.1
 */
public abstract class MemberProposalInfo extends ProposalInfo
{

   protected CompletionProposal fProposal;

   protected final String projectId;

   protected final String docContext;
   
   private boolean isResolved = false;
   
   private Frame frame;

   /**
    * Creates a new proposal info.
    * 
    * @param project the java project to reference when resolving types
    * @param proposal the proposal to generate information for
    */
   public MemberProposalInfo(CompletionProposal proposal, String projectId, String docContext)
   {
      this.projectId = projectId;
      this.docContext = docContext;
      Assert.isNotNull(proposal);
      this.fProposal = proposal;
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ProposalInfo#getInfo()
    */
   @Override
   public Widget getInfo()
   {
      if(isResolved)
         return frame;
      
      String url = getURL();
      isResolved = true;
      if (url != null)
      {
         frame = new Frame(url);
      }
      return frame;
   }

   @Override
   public IJavaElement getJavaElement()
   {
      return null;
   }

   protected abstract String getURL();

}
