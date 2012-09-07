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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

import org.eclipse.jdt.client.core.CompletionProposal;
import org.exoplatform.ide.editor.api.contentassist.ContextInformation;

/**
 * Implementation of the <code>IContextInformation</code> interface.
 */
public final class ProposalContextInformation implements ContextInformation
{

   private final String fContextDisplayString;

   private final String fInformationDisplayString;

   private final Image fImage;

   private int fPosition;

   /**
    * Creates a new context information.
    * 
    * @param proposal the JDT Core completion proposal
    */
   public ProposalContextInformation(CompletionProposal proposal)
   {
      // don't cache the core proposal because the ContentAssistant might
      // hang on to the context info.
      CompletionProposalLabelProvider labelProvider = new CompletionProposalLabelProvider();
      fInformationDisplayString = labelProvider.createParameterList(proposal);
      ImageResource descriptor = labelProvider.createImageDescriptor(proposal);
      if (descriptor != null)
         fImage = new Image(descriptor);
      else
         fImage = null;
      if (proposal.getCompletion().length == 0)
         fPosition = proposal.getCompletionLocation() + 1;
      else
         fPosition = -1;
      fContextDisplayString = labelProvider.createLabel(proposal);
   }

   /*
    * @see IContextInformation#equals
    */
   @Override
   public boolean equals(Object object)
   {
      if (object instanceof ContextInformation)
      {
         ContextInformation contextInformation = (ContextInformation)object;
         boolean equals =
            getInformationDisplayString().equalsIgnoreCase(contextInformation.getInformationDisplayString());
         if (getContextDisplayString() != null)
            equals = equals && getContextDisplayString().equalsIgnoreCase(contextInformation.getContextDisplayString());
         return equals;
      }
      return false;
   }

   /*
    * @see java.lang.Object#hashCode()
    * @since 3.5
    */
   @Override
   public int hashCode()
   {
      int low = fContextDisplayString != null ? fContextDisplayString.hashCode() : 0;
      return (fInformationDisplayString.hashCode() << 16) | low;
   }

   /*
    * @see IContextInformation#getInformationDisplayString()
    */
   public String getInformationDisplayString()
   {
      return fInformationDisplayString;
   }

   /*
    * @see IContextInformation#getImage()
    */
   public Image getImage()
   {
      return fImage;
   }

   /*
    * @see IContextInformation#getContextDisplayString()
    */
   public String getContextDisplayString()
   {
      return fContextDisplayString;
   }

   /*
    * @see IContextInformationExtension#getContextInformationPosition()
    */
   public int getContextInformationPosition()
   {
      return fPosition;
   }

   /**
    * Sets the context information position.
    * 
    * @param position the new position, or -1 for unknown.
    * @since 3.1
    */
   public void setContextInformationPosition(int position)
   {
      fPosition = position;
   }
}
