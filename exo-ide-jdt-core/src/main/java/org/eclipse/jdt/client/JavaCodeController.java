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

import com.google.gwt.user.client.Timer;

import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Java code controller is used for getting AST and updating all modules, that depend on the received AST.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 10:26:58 AM anya $
 * 
 */
public class JavaCodeController implements EditorFileContentChangedHandler, EditorActiveFileChangedHandler
{
   /**
    * Active file in editor.
    */
   private FileModel activeFile;

   /**
    * Is active Java file in editor.
    */
   private boolean isJava = false;

   public JavaCodeController()
   {
      IDE.addHandler(EditorFileContentChangedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

   }

   private Timer parseCodeTimer = new Timer()
   {
      @Override
      public void run()
      {
         try
         {
            CompilationUnit cunit = parseFile();
            IDE.fireEvent(new UpdateOutlineEvent(cunit));
            cancel();
         }
         catch (Throwable e)
         {
            Dialogs.getInstance().showError(e.getMessage());
         }
      }
   };

   private CompilationUnit parseFile()
   {
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setSource(activeFile.getContent().toCharArray());
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setUnitName(activeFile.getName().substring(0, activeFile.getName().lastIndexOf('.')));
      parser.setResolveBindings(true);
      parser.setNameEnvironment(new DummyNameEnvirement(activeFile.getProject().getId()));
      ASTNode ast = parser.createAST(null);
      CompilationUnit unit = (CompilationUnit)ast;
      return unit;
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler#onEditorFileContentChanged(org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent)
    */
   @Override
   public void onEditorFileContentChanged(EditorFileContentChangedEvent event)
   {
      processCode();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
      isJava = MimeType.APPLICATION_JAVA.equals(activeFile.getMimeType());
      processCode();
   }

   public void processCode()
   {
      if (isJava)
      {
         parseCodeTimer.schedule(2000);
      }
   }
}
