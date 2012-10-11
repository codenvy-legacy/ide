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
package org.exoplatform.ide.texteditor.api;

import com.google.gwt.user.client.Element;

import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.DocumentImpl;
import org.exoplatform.ide.text.store.TextStoreMutator;
import org.exoplatform.ide.texteditor.Buffer;
import org.exoplatform.ide.texteditor.FocusManager;
import org.exoplatform.ide.texteditor.UndoManager;
import org.exoplatform.ide.texteditor.renderer.LineRenderer;
import org.exoplatform.ide.texteditor.renderer.Renderer;
import org.exoplatform.ide.texteditor.selection.SelectionModel;
import org.exoplatform.ide.util.ListenerRegistrar;

/**
 * A text display connects a text widget with an
 * {@link Document}. The document is used as the
 * widget's text model.
 * A text viewer supports a set of configuration options and plug-ins defining
 * its behavior:
 * <ul>
 * <li>undo manager</li>
 * <li>explicit configuration</li>
 * </ul>
 * A text view provides several text editing functions, some of them are
 * configurable, through a text operation target interface.
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface TextEditorPartDisplay
{

   /**
    * Returns this display element
    * @return element
    */
   Element getElement();
   
   /**
    * Sets this viewer's undo manager.
    *
    * @param undoManager the new undo manager. <code>null</code> is a valid argument.
    */
   void setUndoManager(UndoManager undoManager);
   
   
   /**
    * Configures the source viewer using the given configuration. Prior to 3.0 this
    * method can only be called once.
    *
    * @param configuration the source viewer configuration to be used
    */
   void configure(TextEditorConfiguration configuration);
   

   /**
    * Sets the given document as the text display model and updates the
    * presentation accordingly.
    * @param document
    */
   void setDocument(DocumentImpl document);

   /**
    * Returns the text display input document.
    * @return the document
    */
   public Document getDocument();
   
   /**
    * @param lineRenderer
    */
   void addLineRenderer(LineRenderer lineRenderer);
   
   /**
    * Sets the editable state.
    *
    * @param isReadOnly the read only state
    */

   void setReadOnly(final boolean isReadOnly);

   /**
    * Returns whether the shown text can be manipulated.
    *
    * @return the viewer's readOnly state
    */
   boolean isReadOnly();

   ListenerRegistrar<BeforeTextListener> getBeforeTextListenerRegistrar();

   /**
    * Returns a document mutator that will also notify editor text listeners.
    */
   TextStoreMutator getEditorDocumentMutator();

   //TODO create interfaces for this functions
   FocusManager getFocusManager();

   ListenerRegistrar<KeyListener> getKeyListenerRegistrar();

   Renderer getRenderer();

   SelectionModel getSelection();
   
   Buffer getBuffer();


}