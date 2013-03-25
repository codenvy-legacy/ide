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

import com.codenvy.ide.api.outline.OutlineModel;
import com.codenvy.ide.editor.TextEditorPartPresenter;
import com.codenvy.ide.java.client.JavaClientBundle;
import com.codenvy.ide.java.client.editor.outline.JavaNodeRenderer;
import com.codenvy.ide.java.client.editor.outline.OutlineModelUpdater;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.parser.Parser;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistProcessor;
import com.codenvy.ide.texteditor.api.reconciler.Reconciler;
import com.codenvy.ide.texteditor.api.reconciler.ReconcilerImpl;
import com.codenvy.ide.texteditor.parser.BasicTokenFactory;
import com.codenvy.ide.texteditor.parser.CmParser;
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

   private TextEditorPartPresenter javaEditor;

   private JavaCodeAssistProcessor codeAssistProcessor;

   private JavaReconcilerStrategy reconcilerStrategy;

   private OutlineModel outlineModel;


   public JavaEditorConfiguration(UserActivityManager manager, JavaClientBundle resources, TextEditorPartPresenter javaEditor)
   {
      super();
      this.manager = manager;
      this.javaEditor = javaEditor;
      outlineModel = new OutlineModel(new JavaNodeRenderer(resources));
      reconcilerStrategy = new JavaReconcilerStrategy(javaEditor);
   }

   private static native CmParser getParserForMime(String mime) /*-{
      conf = $wnd.CodeMirror.defaults;
      return $wnd.CodeMirror.getMode(conf, mime);
   }-*/;


   /**
    * {@inheritDoc}
    */
   @Override
   public Parser getParser(TextEditorPartView view)
   {
      CmParser parser = getParserForMime("text/x-java");
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
   public JsonStringMap<CodeAssistProcessor> getContentAssistantProcessors(TextEditorPartView view)
   {

      JsonStringMap<CodeAssistProcessor> map = JsonCollections.createStringMap();
      map.put(Document.DEFAULT_CONTENT_TYPE, getOrCreateCodeAssistProcessor());
      return map;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public QuickAssistProcessor getQuickAssistAssistant(TextEditorPartView view)
   {
      JavaCorrectionAssistant assistant = new JavaCorrectionAssistant(javaEditor, reconcilerStrategy);
      assistant.install(view);
      ((TextEditorViewImpl)view).setQuickAssistAssistant(assistant);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OutlineModel getOutline(TextEditorPartView view)
   {
      new OutlineModelUpdater(outlineModel, reconcilerStrategy);
      return outlineModel;
   }
}
