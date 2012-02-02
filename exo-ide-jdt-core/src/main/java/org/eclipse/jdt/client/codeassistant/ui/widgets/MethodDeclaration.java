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

import org.eclipse.jdt.client.core.CompletionProposal;

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
public class MethodDeclaration extends MethodRef
{

   /** @param proposal */
   public MethodDeclaration(CompletionProposal proposal)
   {
      super(proposal);
   }

   /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getClassSignature() */
   @Override
   protected String getClassSignature()
   {
      return String.valueOf(proposal.getDeclarationKey());
   }

   /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getDecription() */
   @Override
   public Widget getDecription()
   {
      return null;
   }

}
