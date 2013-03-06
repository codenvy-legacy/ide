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
package com.codenvy.ide.java.client.editor;

import com.codenvy.ide.java.client.editor.outline.OutlineModelUpdater;

import com.codenvy.ide.outline.OutlineModel;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistant;
import com.codenvy.ide.texteditor.api.parser.Parser;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistAssistant;
import com.codenvy.ide.texteditor.api.reconciler.Reconciler;
import com.codenvy.ide.texteditor.api.reconciler.ReconcilerImpl;
import com.codenvy.ide.texteditor.codeassistant.CodeAssistantImpl;
import com.codenvy.ide.texteditor.parser.BasicTokenFactory;
import com.codenvy.ide.texteditor.parser.CmParser;
import com.codenvy.ide.texteditor.parser.CodeMirror2;

import com.codenvy.ide.util.executor.BasicIncrementalScheduler;
import com.codenvy.ide.util.executor.UserActivityManager;


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

   private JavaReconcilerStrategy reconcilerStrategy;

   private OutlineModel outlineModel;

   /**
    * @param manager
    * @param activrProjectId 
    */
   public JavaEditorConfiguration(UserActivityManager manager)
   {
      super();
      this.manager = manager;
      outlineModel = new OutlineModel();
   }

   /**
    * @see com.codenvy.ide.texteditor.api.TextEditorConfiguration#getParser()
    */
   @Override
   public Parser getParser(TextEditorPartView view)
   {
      CmParser parser = CodeMirror2.getParserForMime("text/x-java");
      parser.setNameAndFactory("clike", new BasicTokenFactory());
      return parser;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Reconciler getReconciler(TextEditorPartView view)
   {
      BasicIncrementalScheduler scheduler = new BasicIncrementalScheduler(manager, 50, 100);
      ReconcilerImpl reconciler = new ReconcilerImpl(Document.DEFAULT_PARTITIONING, scheduler);
      reconciler.addReconcilingStrategy(Document.DEFAULT_CONTENT_TYPE, reconcilerStrategy);
      return reconciler;
   }

   /**
    * @param javaEditor
    */
   public void setEditor(JavaEditor javaEditor)
   {
      reconcilerStrategy = new JavaReconcilerStrategy(javaEditor);
      this.javaEditor = javaEditor;
   }

   private JavaCodeAssistProcessor getOrCreateCodeAssistProcessor()
   {
      if (codeAssistProcessor == null)
      {
         codeAssistProcessor = new JavaCodeAssistProcessor(
         //TODO configure doc context
            "rest/ide/code-assistant/java/class-doc?fqn=", reconcilerStrategy);
      }
      return codeAssistProcessor;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CodeAssistant getContentAssistant(TextEditorPartView view)
   {
      CodeAssistantImpl impl = new CodeAssistantImpl();
      impl.setCodeAssistantProcessor(Document.DEFAULT_CONTENT_TYPE, getOrCreateCodeAssistProcessor());
      return impl;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public QuickAssistAssistant getQuickAssistAssistant(TextEditorPartView view)
   {
      return new JavaCorrectionAssistant(javaEditor, reconcilerStrategy);
   }

   public OutlineModel getOutlineModel()
   {
      new OutlineModelUpdater(outlineModel, reconcilerStrategy);
      return outlineModel;
   }
}
