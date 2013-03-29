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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.api.outline.OutlineModel;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.parser.Parser;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistProcessor;
import com.codenvy.ide.texteditor.api.reconciler.Reconciler;

/**
 * This class bundles the configuration space of a editor view. Instances of
 * this class are passed to the <code>configure</code> method of
 * <code>TextEditorPartView</code>.
 * <p>
 * Each method in this class get as argument the source viewer for which it
 * should provide a particular configuration setting such as a presentation
 * reconciler. Based on its specific knowledge about the returned object, the
 * configuration might share such objects or compute them according to some
 * rules.</p>
 * <p>
 * Clients should subclass and override just those methods which must be
 * specific to their needs.</p>
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class TextEditorConfiguration
{

   /**
    * 
    */
   public TextEditorConfiguration()
   {
   }
   
   /**
    * Returns the visual width of the tab character. This implementation always
    * returns 3.
    *
    * @param view the view to be configured by this configuration
    * @return the tab width
    */
   public int getTabWidth(TextEditorPartView view)
   {
      return 3;
   }

   /**
    * Returns the undo manager for the given text view. This implementation
    * always returns a new instance of <code>DefaultUndoManager</code> whose
    * history length is set to 25.
    *
    * @param view the text view to be configured by this configuration
    * @return an undo manager or <code>null</code> if no undo/redo should not be supported
    */
   public UndoManager getUndoManager(TextEditorPartView view)
   {
      return new UndoManager(25);
   }

   /**
    * Returns the content formatter ready to be used with the given source viewer.
    * This implementation always returns <code>null</code>.
    *
    * @param view the source viewer to be configured by this configuration
    * @return a content formatter or <code>null</code> if formatting should not be supported
    */
   public ContentFormatter getContentFormatter(TextEditorPartView view)
   {
      return null;
   }

   /**
    * Returns the content assistant processors ready to be used with the given source viewer.
    * This implementation always returns <code>null</code>.
    *
    * @param view the source viewer to be configured by this configuration
    * @return a content assistant or <code>null</code> if content assist should not be supported
    */
   public JsonStringMap<CodeAssistProcessor> getContentAssistantProcessors(TextEditorPartView view)
   {
      return null;
   }

   /**
    * Returns the quick assist assistant ready to be used with the given
    * source viewer.
    * This implementation always returns <code>null</code>.
    *
    * @param view that ext view to be configured by this configuration
    * @return a quick assist assistant or <code>null</code> if quick assist should not be supported
    */
   public QuickAssistProcessor getQuickAssistAssistant(TextEditorPartView view)
   {
      return null;
   }

   /**
    * Returns the auto edit strategies ready to be used with the given text view
    * when manipulating text of the given content type. 
    * 
    * @param view the source viewer to be configured by this configuration
    * @param contentType the content type for which the strategies are applicable
    * @return the auto edit strategies or <code>null</code> if automatic editing is not to be enabled
    */
   public AutoEditStrategy[] getAutoEditStrategies(TextEditorPartView view, String contentType)
   {
      //TODO return default
      return null; //new AutoEditStrategy[]{getAutoIndentStrategy(display, contentType)};
   }

   /**
    * Returns all configured content types for the given text view. This list
    * tells the caller which content types must be configured for the given text view,
    * i.e. for which content types the given view functionalities
    * must be specified. This implementation always returns <code>
    * new String[] { Document.DEFAULT_CONTENT_TYPE }</code>.
    *
    * @param view the source viewer to be configured by this configuration
    * @return the configured content types for the given viewer
    */
   public String[] getConfiguredContentTypes(TextEditorPartView view)
   {
      return new String[]{Document.DEFAULT_CONTENT_TYPE};
   }

   /**
    * Returns the configured partitioning for the given source viewer. The partitioning is
    * used when the querying content types from the source viewer's input document.  This
    * implementation always returns <code>IDocumentExtension3.DEFAULT_PARTITIONING</code>.
    *
    * @param view the source viewer to be configured by this configuration
    * @return the configured partitioning
    * @see #getConfiguredContentTypes(TextEditorPartView)
    */
   public String getConfiguredDocumentPartitioning(TextEditorPartView view)
   {
      return Document.DEFAULT_PARTITIONING;
   }
   
   /**
    * Returns parser for syntax highlight.
    * This implementation always returns <code>null</code>.
    *  @param view the source viewer to be configured by this configuration
    * @return the Parser
    */
   public Parser getParser(TextEditorPartView view)
   {
      return null;
   }
   
   /**
    * Returns the reconciler ready to be used with the given source view.
    * This implementation always returns <code>null</code>.
    *
    * @param view the source view to be configured by this configuration
    * @return a reconciler or <code>null</code> if reconciling should not be supported
    */
   public Reconciler getReconciler(TextEditorPartView view) 
   {
      return null;
   }

   /**
    * Return the outline model.
    * This implementation always returns <code>null</code>.
    *
    * @param view the source view to be configured by this configuration.
    * @return a model that used to build outline tree.
    */
   public OutlineModel getOutline(TextEditorPartView view)
   {
      return null;
   }

}
