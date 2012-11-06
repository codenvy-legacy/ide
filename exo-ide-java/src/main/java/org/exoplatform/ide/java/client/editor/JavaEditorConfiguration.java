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
package org.exoplatform.ide.java.client.editor;

import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.texteditor.api.TextEditorConfiguration;
import org.exoplatform.ide.texteditor.api.TextEditorPartDisplay;
import org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistant;
import org.exoplatform.ide.texteditor.api.parser.Parser;
import org.exoplatform.ide.texteditor.api.reconciler.Reconciler;
import org.exoplatform.ide.texteditor.api.reconciler.ReconcilerImpl;
import org.exoplatform.ide.texteditor.codeassistant.CodeAssistantImpl;
import org.exoplatform.ide.texteditor.parser.BasicTokenFactory;
import org.exoplatform.ide.texteditor.parser.CmParser;
import org.exoplatform.ide.texteditor.parser.CodeMirror2;
import org.exoplatform.ide.util.executor.BasicIncrementalScheduler;
import org.exoplatform.ide.util.executor.UserActivityManager;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaEditorConfiguration extends TextEditorConfiguration
{

   private UserActivityManager manager;

   private JavaEditor javaEditor;

   private JavaCodeAssistProcessor codeAssistProcessor;

   /**
    * @param manager
    * @param activrProjectId 
    */
   public JavaEditorConfiguration(UserActivityManager manager)
   {
      super();
      this.manager = manager;
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.TextEditorConfiguration#getParser()
    */
   @Override
   public Parser getParser(TextEditorPartDisplay display)
   {
      CmParser parser = CodeMirror2.getParserForMime("text/x-java");
      parser.setNameAndFactory("clike", new BasicTokenFactory());
      return parser;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Reconciler getReconciler(TextEditorPartDisplay display)
   {
      BasicIncrementalScheduler scheduler = new BasicIncrementalScheduler(manager, 50, 100);
      ReconcilerImpl reconciler = new ReconcilerImpl(Document.DEFAULT_PARTITIONING, scheduler);
      reconciler.addReconcilingStrategy(Document.DEFAULT_CONTENT_TYPE, new JavaReconcilerStrategy(javaEditor,
         getOrCreateCodeAssistProcessor()));
      return reconciler;
   }

   /**
    * @param javaEditor
    */
   public void setEditor(JavaEditor javaEditor)
   {
      this.javaEditor = javaEditor;
   }

   private JavaCodeAssistProcessor getOrCreateCodeAssistProcessor()
   {
      if (codeAssistProcessor == null)
      {
         codeAssistProcessor = new JavaCodeAssistProcessor(
         //TODO configure doc context
            "rest/ide/code-assistant/java/class-doc?fqn=");
      }
      return codeAssistProcessor;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CodeAssistant getContentAssistant(TextEditorPartDisplay display)
   {
      CodeAssistantImpl impl = new CodeAssistantImpl();
      impl.setCodeAssistantProcessor(Document.DEFAULT_CONTENT_TYPE, getOrCreateCodeAssistProcessor());
      return impl;
   }
}
