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
package org.exoplatform.ide.texteditor.api.quickassist;

import org.exoplatform.ide.text.annotation.Annotation;
import org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal;

/**
 * Quick assist processor for quick fixes and quick assists.
 * <p>
 * A processor can provide just quick fixes, just quick assists
 * or both.
 * </p>
 * <p>
 * This interface can be implemented by clients.</p>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface QuickAssistProcessor
{

   /**
    * Returns a list of quick assist and quick fix proposals for the
    * given invocation context.
    *
    * @param invocationContext the invocation context
    * @return an array of completion proposals or <code>null</code> if no proposals are available
    */
   CompletionProposal[] computeQuickAssistProposals(QuickAssistInvocationContext invocationContext);

   /**
    * Returns the reason why this quick assist processor
    * was unable to produce any completion proposals.
    *
    * @return an error message or <code>null</code> if no error occurred
    */
   String getErrorMessage();

   /** 
    * Tells whether this processor has a fix for the given annotation.
    * <p>
    * <strong>Note:</strong> This test must be fast and optimistic i.e. it is OK to return
    * <code>true</code> even though there might be no quick fix.
    * </p>
    *
    * @param annotation the annotation
    * @return <code>true</code> if the assistant has a fix for the given annotation
    */
   boolean canFix(Annotation annotation);

   /**
    * Tells whether this assistant has assists for the given invocation context.
    *
    * @param invocationContext the invocation context
    * @return <code>true</code> if the assistant has a fix for the given annotation
    */
   boolean canAssist(QuickAssistInvocationContext invocationContext);

}
