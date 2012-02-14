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

import com.google.gwt.core.client.Scheduler.RepeatingCommand;

import com.google.gwt.core.client.Scheduler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Timer;

import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.event.CancelParseEvent;
import org.eclipse.jdt.client.event.CancelParseHandler;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Java code controller is used for getting AST and updating all modules, that depend on the received AST.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 10:26:58 AM anya $
 * 
 */
public class JavaCodeController implements EditorFileContentChangedHandler, EditorActiveFileChangedHandler,
   CancelParseHandler, EditorFileOpenedHandler
{
   /**
    * Active file in editor.
    */
   private FileModel activeFile;

   private boolean needReparse = false;

   private int problemCount = 0;

   private CodeMirror editor;

   public JavaCodeController()
   {
      IDE.addHandler(EditorFileContentChangedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      IDE.addHandler(CancelParseEvent.TYPE, this);
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);

   }

   /** @return */
   private CompilationUnit parseFile()
   {
      if (editor == null)
         return null;
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setSource(editor.getText());
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setUnitName(activeFile.getName().substring(0, activeFile.getName().lastIndexOf('.')));
      parser.setResolveBindings(true);
      parser.setNameEnvironment(new DummyNameEnvironment(activeFile.getProject().getId()));
      ASTNode ast = parser.createAST(null);
      CompilationUnit unit = (CompilationUnit)ast;
      return unit;
   }

   /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
         return;
      if (event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA))
      {
         activeFile = event.getFile();
         if (event.getEditor() instanceof CodeMirror)
         {
            editor = (CodeMirror)event.getEditor();
            if (needReparse)
            {
               timer.cancel();
               timer.schedule(2000);
            }
         }
         else
            editor = null;
      }
      else
      {
         activeFile = null;
         editor = null;
      }
   }
   

   private Timer timer = new Timer()
   {

      @Override
      public void run()
      {
         Scheduler.get().scheduleIncremental(com);
      }
   };

   private void asyncParse()
   {
      GWT.runAsync(new RunAsyncCallback()
      {

         @Override
         public void onSuccess()
         {
            CompilationUnit unit = parseFile();
            if (needReparse)
            {
               IProblem[] problems = unit.getProblems();
               if (problems.length == problemCount)
               {
                  needReparse = false;
                  problemCount = 0;
               }
               else
               {
                  problemCount = unit.getProblems().length;
                  timer.schedule(1000);
                  return;
               }

            }
            IDE.fireEvent(new UpdateOutlineEvent(unit));
            if (unit.getProblems().length == 0 || editor == null)
               return;

            int length = activeFile.getContent().split("\n").length;
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
            reason.printStackTrace();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      needReparse = true;
   }

   /**
    * @see org.eclipse.jdt.client.event.CancelParseHandler#onCancelParse(org.eclipse.jdt.client.event.CancelParseEvent)
    */
   @Override
   public void onCancelParse(CancelParseEvent event)
   {
      timer.cancel();
   }

   RepeatingCommand com = new RepeatingCommand()
   {
      
      @Override
      public boolean execute()
      {
         asyncParse();
         return false;
      }
   };
   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler#onEditorFileContentChanged(org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent)
    */
   @Override
   public void onEditorFileContentChanged(EditorFileContentChangedEvent event)
   {
      timer.cancel();
      if (editor != null)
      {
         timer.schedule(2000);
      }
   }
}
