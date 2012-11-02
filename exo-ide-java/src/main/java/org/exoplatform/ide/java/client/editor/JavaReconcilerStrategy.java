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

import com.google.gwt.core.shared.GWT;

import org.exoplatform.ide.editor.TextEditorPartPresenter;
import org.exoplatform.ide.java.client.JavaAutoBeanFactory;
import org.exoplatform.ide.java.client.NameEnvironment;
import org.exoplatform.ide.java.client.core.IProblemRequestor;
import org.exoplatform.ide.java.client.core.compiler.IProblem;
import org.exoplatform.ide.java.client.core.dom.AST;
import org.exoplatform.ide.java.client.core.dom.ASTNode;
import org.exoplatform.ide.java.client.core.dom.ASTParser;
import org.exoplatform.ide.java.client.core.dom.CompilationUnit;
import org.exoplatform.ide.java.client.internal.compiler.env.INameEnvironment;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Resource;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.Region;
import org.exoplatform.ide.text.annotation.AnnotationModel;
import org.exoplatform.ide.texteditor.api.reconciler.DirtyRegion;
import org.exoplatform.ide.texteditor.api.reconciler.ReconcilingStrategy;
import org.exoplatform.ide.util.loging.Log;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaReconcilerStrategy implements ReconcilingStrategy
{

   private Document document;

   private INameEnvironment nameEnvironment;

   private final TextEditorPartPresenter editor;

   private File file;

   /**
    * @param activeFile 
    * @param editor 
    * 
    */
   public JavaReconcilerStrategy(TextEditorPartPresenter editor)
   {
      this.editor = editor;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDocument(Document document)
   {
      this.document = document;
      file = editor.getEditorInput().getFile();
      nameEnvironment =
         new NameEnvironment(file.getProject().getId(), GWT.<JavaAutoBeanFactory> create(JavaAutoBeanFactory.class),
            "rest");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void reconcile(DirtyRegion dirtyRegion, Region subRegion)
   {
      parse();
   }

   /**
    * 
    */
   private void parse()
   {
      AnnotationModel annotationModel = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
      if (annotationModel == null)
         return;
      IProblemRequestor problemRequestor = null;
      if (annotationModel instanceof IProblemRequestor)
      {
         problemRequestor = (IProblemRequestor)annotationModel;
         problemRequestor.beginReporting();
      }
      try
      {
         ASTParser parser = ASTParser.newParser(AST.JLS3);
         parser.setSource(document.get());
         parser.setKind(ASTParser.K_COMPILATION_UNIT);
         parser.setUnitName(file.getName().substring(0, file.getName().lastIndexOf('.')));
         parser.setResolveBindings(true);
         parser.setNameEnvironment(nameEnvironment);
         ASTNode ast = parser.createAST();
         CompilationUnit unit = (CompilationUnit)ast;
         IProblem[] problems = unit.getProblems();
         for (IProblem p : problems)
         {
            problemRequestor.acceptProblem(p);
         }
         IProblem[] tasks = (IProblem[])unit.getProperty("tasks");
         if (tasks != null)
         {
            for (IProblem p : tasks)
            {
               problemRequestor.acceptProblem(p);
            }
         }
      }
      catch (Exception e)
      {
         Log.error(getClass(), e);
      }
      finally
      {
         if (problemRequestor != null)
            problemRequestor.endReporting();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void reconcile(Region partition)
   {
      parse();
   }

}
