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
package org.exoplatform.ide.editor;

import org.exoplatform.ide.part.PartPresenter;

/**
 * An editor is a visual component.
 * It is typically used to edit or browse a document or input object. The input 
 * is identified using an <code>EditorInput</code>.  Modifications made 
 * in an editor part follow an open-save-close lifecycle model
 * <p>
 * An editor is document or input-centric.  Each editor has an input, and only
 * one editor can exist for each editor input within a page.
 * </p>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface EditorPartPresenter extends PartPresenter
{

   /**
    * Initializes this editor with the given editor site and input.
    * <p>
    * This method is automatically called shortly after the part is instantiated.
    * It marks the start of the part's lifecycle. 
    * <p>
    * Implementors of this method must examine the editor input object type to
    * determine if it is understood.  If not, the implementor must throw
    * a <code>PartInitException</code>
    * </p>
    * @param input the editor input
    * @exception EditorInitException if this editor was not initialized successfully
    */
   public void init(EditorInput input) throws EditorInitException;

   /**
    * Saves the contents of this editor.
    */
   public void doSave();

   /**
    * Saves the contents of this part to another object.
    */
   public void doSaveAs();

   /**
    * Returns whether the contents of this part have changed since the last save
    * operation. 
    * @return <code>true</code> if the contents have been modified and need
    *   saving, and <code>false</code> if they have not changed since the last
    *   save
    */
   public boolean isDirty();

   /**
    * Returns whether the "Save As" operation is supported by this part.
    *
    * @return <code>true</code> if "Save As" is supported, and <code>false</code>
    *  if not supported
    */
   public boolean isSaveAsAllowed();

   /**
    * Returns whether the contents of this part should be saved when the part
    * is closed.
    *
    * @return <code>true</code> if the contents of the part should be saved on
    *   close, and <code>false</code> if the contents are expendable
    */
   public boolean isSaveOnCloseNeeded();
}
