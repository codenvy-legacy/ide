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
package org.exoplatform.ide.texteditor.renderer;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.DocumentImpl;
import org.exoplatform.ide.text.Position;
import org.exoplatform.ide.text.annotation.Annotation;
import org.exoplatform.ide.text.annotation.AnnotationModel;
import org.exoplatform.ide.text.annotation.AnnotationModelEvent;
import org.exoplatform.ide.text.annotation.AnnotationModelListener;
import org.exoplatform.ide.text.store.Line;
import org.exoplatform.ide.text.store.LineFinder;
import org.exoplatform.ide.texteditor.Editor;
import org.exoplatform.ide.util.loging.Log;

import java.util.Iterator;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class AnnotationRenderer implements AnnotationModelListener
{

   private Editor editor;

   private AnnotationModel annotationModel;

   private final JsonStringMap<String> decorations;

   private ErrorRenderer renderer;

   /**
    * @param editor
    */
   public AnnotationRenderer(Editor editor, JsonStringMap<String> decorations)
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
