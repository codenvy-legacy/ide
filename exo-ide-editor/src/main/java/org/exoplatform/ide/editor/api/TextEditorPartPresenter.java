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
package org.exoplatform.ide.editor.api;


/**
 * Interface to a text editor. This interface defines functional extensions to
 * <code>EditorPartPresenter</code> as well as the configuration capabilities of a text editor.
 * <p>
 * Text editors are configured with an <code>DocumentProvider</code> which delivers a textual
 * presentation (<code>Document</code>) of the editor's input. The editor works on the document and
 * forwards all input element related calls, such as <code>save</code>, to the document provider.
 * The provider also delivers the input's annotation model which is used by the editor's vertical
 * ruler.
 * </p>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface TextEditorPartPresenter extends EditorPartPresenter
{
   /**
    * Returns this text editor's document provider.
    *
    * @return the document provider or <code>null</code> if none, e.g. after closing the editor
    */
   DocumentProvider getDocumentProvider();
   
   /**
    * Closes this text editor after optionally saving changes.
    *
    * @param save <code>true</code> if unsaved changed should be saved, and
    *   <code>false</code> if unsaved changed should be discarded
    */
   void close(boolean save);

   /**
    * Returns whether the text in this text editor can be changed by the user.
    *
    * @return <code>true</code> if it can be edited, and <code>false</code> if it is read-only
    */
   boolean isEditable();

   /**
    * Abandons all modifications applied to this text editor's input element's
    * textual presentation since the last save operation.
    */
   void doRevertToSaved();
   
   /**
    * Returns this text editor's selection provider. Repeated calls to this
    * method return the same selection provider.
    *
    * @return the selection provider
    */
   SelectionProvider getSelectionProvider();

   /**
    * Selects and reveals the specified range in this text editor.
    *
    * @param offset the offset of the selection
    * @param length the length of the selection
    */
   void selectAndReveal(int offset, int length);
}
