/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.exoplatform.ide.editor.shared.text;


/**
 * A document rewrite session.
 */
public class DocumentRewriteSession
{

   private DocumentRewriteSessionType fSessionType;

   /**
    * Prohibit package external object creation.
    *
    * @param sessionType the type of this session
    */
   protected DocumentRewriteSession(DocumentRewriteSessionType sessionType)
   {
      fSessionType = sessionType;
   }

   /**
    * Returns the type of this session.
    *
    * @return the type of this session
    */
   public DocumentRewriteSessionType getSessionType()
   {
      return fSessionType;
   }

   /*
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return new StringBuffer().append(hashCode()).toString();
   }
}
