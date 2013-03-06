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
package com.codenvy.ide.texteditor.api.quickassist;


/**
 * Allows an annotation to tell whether there are quick fixes
 * for it and to cache that state.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface QuickFixableAnnotation
{

   /**
    * Sets whether there are quick fixes available for
    * this annotation.
    *
    * @param state <code>true</code> if there are quick fixes available, false otherwise
    */
   void setQuickFixable(boolean state);

   /**
    * Tells whether the quick fixable state has been set.
    * <p>
    * Normally this means {@link #setQuickFixable(boolean)} has been
    * called at least once but it can also be hard-coded, e.g. always
    * return <code>true</code>.
    * </p>
    *
    * @return <code>true</code> if the state has been set
    */
   boolean isQuickFixableStateSet();

   /**
    * Tells whether there are quick fixes for this annotation.
    * <p>
    * <strong>Note:</strong> This method must only be called
    * if {@link #isQuickFixableStateSet()} returns <code>true</code>.</p>
    *
    * @return <code>true</code> if this annotation offers quick fixes
    */
   boolean isQuickFixable();

}
