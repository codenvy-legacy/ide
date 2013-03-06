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

import com.codenvy.ide.texteditor.api.TextEditorPartView;

/**
 * Context information for quick fix and quick assist processors.
 * <p>
 * This interface can be implemented by clients.</p>
 *
 */
public interface QuickAssistInvocationContext
{

   /**
    * Returns the offset where quick assist was invoked.
    *
    * @return the invocation offset or <code>-1</code> if unknown
    */
   int getOffset();

   /**
    * Returns the length of the selection at the invocation offset.
    *
    * @return the length of the current selection or <code>-1</code> if none or unknown
    */
   int getLength();

   /**
    * Returns the editor for this context.
    *
    * @return the text editor or <code>null</code> if not available
    */
   TextEditorPartView getTextEditor();
}
