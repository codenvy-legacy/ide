/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.codeassistant;

import org.eclipse.jdt.client.runtime.IProgressMonitor;

public class ProposalInfo
{

   private boolean fJavadocResolved = false;

   private String fJavadoc = null;

   /**
    * Gets the text for this proposal info formatted as HTML, or <code>null</code> if no text is available.
    * 
    * @param monitor a progress monitor
    * @return the additional info text
    */
   public final String getInfo(IProgressMonitor monitor)
   {
      if (!fJavadocResolved)
      {
         fJavadocResolved = true;
         fJavadoc = computeInfo(monitor);
      }
      return fJavadoc;
   }

   /**
    * Gets the text for this proposal info formatted as HTML, or <code>null</code> if no text is available.
    * 
    * @param monitor a progress monitor
    * @return the additional info text
    */
   private String computeInfo(IProgressMonitor monitor)
   {
      // try {
      // final IJavaElement javaElement= getJavaElement();
      // if (javaElement instanceof IMember) {
      // IMember member= (IMember) javaElement;
      // return extractJavadoc(member, monitor);
      // }
      // } catch (JavaModelException e) {
      // JavaPlugin.log(e);
      // }

      // TODO search javadoc
      return null;
   }

   // /**
   // * Extracts the javadoc for the given <code>IMember</code> and returns it
   // * as HTML.
   // *
   // * @param member the member to get the documentation for
   // * @param monitor a progress monitor
   // * @return the javadoc for <code>member</code> or <code>null</code> if
   // * it is not available
   // * @throws JavaModelException if accessing the javadoc fails
   // */
   // private String extractJavadoc(IMember member, IProgressMonitor monitor) throws JavaModelException {
   // if (member != null) {
   // return JavadocContentAccess2.getHTMLContent(member, true);
   // }
   // return null;
   // }

}
