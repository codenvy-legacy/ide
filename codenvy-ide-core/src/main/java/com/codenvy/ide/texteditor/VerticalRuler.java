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
package com.codenvy.ide.texteditor;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.text.TextUtilities;
import com.codenvy.ide.text.annotation.Annotation;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.text.annotation.AnnotationModelEvent;
import com.codenvy.ide.text.annotation.AnnotationModelListener;
import com.codenvy.ide.texteditor.api.TextEditorOperations;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.gutter.Gutter;
import com.codenvy.ide.texteditor.gutter.Gutter.ClickListener;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.mvp.CompositeView;
import com.codenvy.ide.util.ListenerRegistrar.Remover;
import com.codenvy.ide.util.loging.Log;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import elemental.html.Element;


import java.util.Iterator;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class VerticalRuler
{

   class InternalListener implements AnnotationModelListener
   {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modelChanged(AnnotationModelEvent event)
      {
         update();
      }

   }

   class Mark extends CompositeView<Annotation>
   {
      /**
       * 
       */
      public Mark(Annotation annotation)
      {
         setElement((Element)AbstractImagePrototype.create(annotation.getImage()).createElement());
         getElement().getStyle().setZIndex(annotation.getLayer());
         getElement().getStyle().setPosition("absolute");
         getElement().getStyle().setWidth("15px");
         getElement().getStyle().setLeft("0px");
         getElement().setTitle(annotation.getText());
      }

      public void setTopPosition(int top, String unit)
      {
         getElement().getStyle().setTop(top, unit);
      }
   }

   private AnnotationModel model;

   private Remover remover;

   private final Gutter view;

   private final TextEditorPartView editor;

   private InternalListener listener;

   private JsonArray<Element> elements;

   /**
    * @param leftNotificationGutter
    */
   public VerticalRuler(Gutter leftNotificationGutter, TextEditorPartView editor)
   {
      this.view = leftNotificationGutter;
      this.editor = editor;
      listener = new InternalListener();
      elements = JsonCollections.createArray();
      view.getClickListenerRegistrar().add(new ClickListener()
      {

         @Override
         public void onClick(int y)
         {

            TextEditorPartView editor = VerticalRuler.this.editor;
            if (editor.canDoOperation(TextEditorOperations.QUICK_ASSIST))
            {
               int lineNumber = editor.getBuffer().convertYToLineNumber(y, true);
               try
               {
                  int offset = editor.getDocument().getLineOffset(lineNumber);
                  editor.getSelection().setCursorPosition(offset);
                  editor.doOperation(TextEditorOperations.QUICK_ASSIST);
               }
               catch (BadLocationException e)
               {
                  Log.error(getClass(), e);
               }
            }
         }
      });
   }

   /**
    * 
    */
   private void update()
   {
      for (Element e : elements.asIterable())
      {
         view.removeUnmanagedElement(e);
      }
      elements.clear();

      for (Iterator<Annotation> iterator = model.getAnnotationIterator(); iterator.hasNext();)
      {
         Annotation annotation = iterator.next();
         if (annotation.getImage() == null)
            continue;
         Mark m = new Mark(annotation);
         Position position = model.getPosition(annotation);
         int lineNumber = getLineNumberForPosition(position);
         m.setTopPosition(editor.getBuffer().calculateLineTop(lineNumber), "px");
         view.addUnmanagedElement(m.getElement());
         elements.add(m.getElement());
      }
   }

   /**
    * @param position
    */
   private int getLineNumberForPosition(Position position)
   {
      return TextUtilities.getLineLineNumber(editor.getDocument(), position.getOffset());
   }

   /**
    * @param annotationModel
    */
   public void setModel(AnnotationModel annotationModel)
   {
      if (model != annotationModel)
      {
         if (remover != null)
         {
            remover.remove();
         }
         model = annotationModel;
         remover = model.addAnnotationModelListener(listener);
      }
   }

}
