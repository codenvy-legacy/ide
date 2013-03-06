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
package com.codenvy.ide.texteditor.renderer;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.DocumentImpl;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.text.annotation.Annotation;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.text.annotation.AnnotationModelEvent;
import com.codenvy.ide.text.annotation.AnnotationModelListener;
import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.text.store.LineFinder;
import com.codenvy.ide.texteditor.TextEditorViewImpl;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.util.loging.Log;


import java.util.Iterator;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class AnnotationRenderer implements AnnotationModelListener
{

   private TextEditorViewImpl editor;

   private AnnotationModel annotationModel;

   private final JsonStringMap<String> decorations;

   private ErrorRenderer renderer;

   /**
    * @param editor
    */
   public AnnotationRenderer(TextEditorViewImpl editor, JsonStringMap<String> decorations)
   {
      super();
      this.editor = editor;
      this.decorations = decorations;
      renderer = new ErrorRenderer();
      editor.addLineRenderer(renderer);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void modelChanged(AnnotationModelEvent event)
   {

      JsonArray<AnnotationCode> annotations = JsonCollections.createArray();
      for (Iterator<Annotation> iterator = annotationModel.getAnnotationIterator(); iterator.hasNext();)
      {
         Annotation annotation = iterator.next();
         //only annotation with decoration
         if (decorations.containsKey(annotation.getType()))
         {
            Position position = annotationModel.getPosition(annotation);
            AnnotationCode ac =
               new AnnotationCode(getDocumentPosition(position.offset), getDocumentPosition(position.length
                  + position.offset -1), decorations.get(annotation.getType()));
            annotations.add(ac);
         }
      }
      onAnnotationsChanged(annotations);
   }

   private DocumentPosition getDocumentPosition(int offset)
   {

      try
      {
         int lineNumber = editor.getDocument().getLineOfOffset(offset);
         return new DocumentPosition(lineNumber, offset - editor.getDocument().getLineOffset(lineNumber));
      }
      catch (BadLocationException e)
      {
         Log.error(getClass(), e);
      }
      return null;
   }

   private void onAnnotationsChanged(JsonArray<AnnotationCode> newErrors)
   {
      if (editor.getDocument() == null)
      {
         return;
      }
      JsonArray<Line> linesToRender = JsonCollections.createArray();
      getLinesOfErrorsInViewport(renderer.getCodeErrors(), linesToRender);
      getLinesOfErrorsInViewport(newErrors, linesToRender);
      //      positionMigrator.reset();
      renderer.setCodeErrors(newErrors);

      for (int i = 0; i < linesToRender.size(); i++)
      {
         editor.getRenderer().requestRenderLine(linesToRender.get(i));
      }
      editor.getRenderer().renderChanges();
   }

   private void getLinesOfErrorsInViewport(JsonArray<AnnotationCode> errors, JsonArray<Line> lines)
   {
      LineFinder lineFinder = ((DocumentImpl)editor.getDocument()).getTextStore().getLineFinder();
      int topLineNumber = editor.getViewport().getTopLineNumber();
      int bottomLineNumber = editor.getViewport().getBottomLineNumber();
      for (int i = 0; i < errors.size(); i++)
      {
         AnnotationCode error = errors.get(i);
         for (int j = error.getStart().getLineNumber(); j <= error.getEnd().getLineNumber(); j++)
         {
            if (j >= topLineNumber && j <= bottomLineNumber)
            {
               lines.add(lineFinder.findLine(j).line());
            }
         }
      }
   }

   /**
    * @param annotationModel
    */
   public void setMode(AnnotationModel annotationModel)
   {
      this.annotationModel = annotationModel;
      annotationModel.addAnnotationModelListener(this);
   }

}
