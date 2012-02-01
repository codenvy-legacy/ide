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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.codeassistant.ui.ProposalWidget;
import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.core.dom.Modifier;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * Completion is a reference to a method. This kind of completion might occur in a context like
 * <code>"System.out.pr^();"</code> and complete it to <code>""System.out.println();"</code>.
 * <p>
 * The following additional context information is available for this kind of completion proposal at little extra cost:
 * <ul>
 * <li>{@link #getDeclarationSignature()} - the type signature of the type that declares the method that is referenced</li>
 * <li>{@link #getFlags()} - the modifiers flags of the method that is referenced</li>
 * <li>{@link #getName()} - the simple name of the method that is referenced</li>
 * <li>{@link #getSignature()} - the method signature of the method that is referenced</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  10:54:05 AM 34360 2009-07-22 23:58:59Z evgen $
 *
 */
public class MethodRef extends ProposalWidget
{

   /**
    * @param proposal
    */
   public MethodRef(CompletionProposal proposal)
   {
      super(proposal);
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getImage(int)
    */
   @Override
   protected ImageResource getImage(int flags)
   {
      if (Modifier.isPublic(flags))
         return JavaClientBundle.INSTANCE.publicMethod();
      if (Modifier.isPrivate(flags))
         return JavaClientBundle.INSTANCE.privateMethod();
      if (Modifier.isProtected(flags))
         return JavaClientBundle.INSTANCE.protectedMethod();
      return JavaClientBundle.INSTANCE.defaultMethod();
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getTypeSignature()
    */
   @Override
   protected String getTypeSignature()
   {
      return String.valueOf(Signature.getSignatureSimpleName(Signature.getReturnType(proposal.getSignature())));
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getClassSignature()
    */
   @Override
   protected String getClassSignature()
   {
      return String.valueOf(Signature.getSignatureSimpleName(proposal.getDeclarationSignature()));
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getName()
    */
   @Override
   public String getName()
   {
      StringBuilder params = new StringBuilder();
      params.append('(');
      char[][] parameterNames = proposal.findParameterNames(null);
      char[][] parameterTypes = Signature.getParameterTypes(proposal.getSignature());
      for (int i = 0; i < parameterTypes.length; i++)
      {
         params.append(Signature.getSignatureSimpleName(parameterTypes[i])).append(' ').append(parameterNames[i]);
         if (i < parameterTypes.length - 1)
            params.append(", ");
      }
      params.append(')');
      return String.valueOf(proposal.getName()) + params.toString();
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getDecription()
    */
   @Override
   public Widget getDecription()
   {
      return null;
   }

}
