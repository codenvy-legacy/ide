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
package org.eclipse.jdt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.event.CancelParseEvent;
import org.eclipse.jdt.client.event.CancelParseHandler;
import org.eclipse.jdt.client.event.ShowAstEvent;
import org.eclipse.jdt.client.event.ShowAstHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 20, 2012 1:28:01 PM evgen $
 */
public class AstPresenter implements EditorActiveFileChangedHandler, ShowAstHandler, ViewClosedHandler,
   EditorContentChangedHandler, CancelParseHandler
{

   public interface Display extends IsView
   {
      String id = "AstView";

      void drawAst(CompilationUnit cUnit);
   }

   private FileModel currentFile;

   private CodeMirror editor;

   private Display display;

   /**
    * 
    */
   public AstPresenter(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ShowAstEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(EditorContentChangedEvent.TYPE, this);
      eventBus.addHandler(CancelParseEvent.TYPE, this);
   }

   /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      currentFile = event.getFile();
      if (event.getEditor() instanceof CodeMirror)
         editor = (CodeMirror)event.getEditor();
      else
         editor = null;
   }

   /** @see org.eclipse.jdt.client.event.ShowAstHandler#onShowAst(org.eclipse.jdt.client.event.ShowAstEvent) */
   @Override
   public void onShowAst(ShowAstEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
      }
      if (currentFile != null)
      {
         CompilationUnit unit = parseFile();
         display.drawAst(unit);
      }
   }

   /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent) */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
         display = null;
   }

   /** @see org.exoplatform.ide.editor.api.event.EditorContentChangedHandler#onEditorContentChanged(org.exoplatform.ide.editor.api.event.EditorContentChangedEvent) */
   @Override
   public void onEditorContentChanged(EditorContentChangedEvent event)
   {
      timer.cancel();
      timer.schedule(2000);

   }

   /** @return */
   private CompilationUnit parseFile()
   {
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setSource(currentFile.getContent().toCharArray());
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setUnitName(currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')));
      parser.setEnvironment(new String[]{"fersf"}, new String[]{"wfer"}, new String[]{"UTF-8"}, true);
      parser.setResolveBindings(true);
      parser.setNameEnvironment(new DummyNameEnvirement(currentFile.getProject().getId()));
      ASTNode ast = parser.createAST(null);
      CompilationUnit unit = (CompilationUnit)ast;
      return unit;
   }


   private Timer timer = new Timer()
   {

      @Override
      public void run()
      {
         GWT.runAsync(new RunAsyncCallback()
         {
            
            
            @Override
            public void onSuccess()
            {
               CompilationUnit unit = parseFile();
               if (unit.getProblems().length == 0 || editor == null)
                  return;

               int length = currentFile.getContent().split("\n").length;
               for (int i = 1; i <= length; i++)
               {
                  editor.clearErrorMark(i);
               }
               for (IProblem p : unit.getProblems())
               {
                  int sourceLineNumber = p.getSourceLineNumber();
                  if (sourceLineNumber == 0)
                     sourceLineNumber = 1;
                  editor.setErrorMark(sourceLineNumber, p.getMessage());
               }
            }            
            
            @Override
            public void onFailure(Throwable reason)
            {
               // TODO Auto-generated method stub
               reason.printStackTrace();
            }
         });
        
       }
   };

   /**
    * @see org.eclipse.jdt.client.event.CancelParseHandler#onCancelParse(org.eclipse.jdt.client.event.CancelParseEvent)
    */
   @Override
   public void onCancelParse(CancelParseEvent event)
   {
      timer.cancel();
   }

}
