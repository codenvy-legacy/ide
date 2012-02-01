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
package org.eclipse.jdt.client.codeassistant.ui;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.internal.codeassist.InternalCompletionProposal;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Jan 26, 2012 11:59:49 AM evgen $
 *
 */
public class SimpleProposalWidget extends ProposalWidget
{

   /**
    * @param proposal
    */
   public SimpleProposalWidget(CompletionProposal proposal)
   {
      super(proposal);

      Image i = new Image(JavaClientBundle.INSTANCE.blankImage());
      i.setHeight("16px");
      grid.setWidget(0, 0, i);
      String name = getProposalName();
      Label nameLabel = new Label(name, false);
      grid.setWidget(0, 1, nameLabel);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
   }

   /**
    * @param proposal
    * @return
    */
   private String getProposalName()
   {
      String name;
      if (proposal.getName() == null)
         name = String.valueOf(((InternalCompletionProposal)proposal).getTypeName());
      else
         name = String.valueOf(proposal.getName());
      return name;
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getName()
    */
   @Override
   public String getName()
   {
      return getProposalName();
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getDecription()
    */
   @Override
   public Widget getDecription()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#setSelectedStyle()
    */
   @Override
   public void setSelectedStyle()
   {
      setStyleName(JavaClientBundle.INSTANCE.css().selectedItem());
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#setDefaultStyle()
    */
   @Override
   public void setDefaultStyle()
   {
      setStyleName(JavaClientBundle.INSTANCE.css().item());
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getImage(int)
    */
   @Override
   protected ImageResource getImage(int flags)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getTypeSignature()
    */
   @Override
   protected String getTypeSignature()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getClassSignature()
    */
   @Override
   protected String getClassSignature()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
