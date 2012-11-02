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
package org.exoplatform.ide.texteditor;


import com.google.gwt.user.client.ui.AbstractImagePrototype;
import elemental.html.Element;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.mvp.CompositeView;
import org.exoplatform.ide.text.Position;
import org.exoplatform.ide.text.TextUtilities;
import org.exoplatform.ide.text.annotation.Annotation;
import org.exoplatform.ide.text.annotation.AnnotationModel;
import org.exoplatform.ide.text.annotation.AnnotationModelEvent;
import org.exoplatform.ide.text.annotation.AnnotationModelListener;
import org.exoplatform.ide.texteditor.api.TextEditorPartDisplay;
import org.exoplatform.ide.texteditor.gutter.Gutter;
import org.exoplatform.ide.util.ListenerRegistrar.Remover;

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

   private final Gutter display;

   private final TextEditorPartDisplay editor;
   
   private InternalListener listener;
   
   private JsonArray<Element> elements;
   

   /**
    * @param leftNotificationGutter
    */
   public VerticalRuler(Gutter leftNotificationGutter, TextEditorPartDisplay editor)
   {
      this.display = leftNotificationGutter;
      this.editor = editor;
      listener = new InternalListener();
      elements = JsonCollections.createArray();
   }

   /**
    * 
    */
   private void update()
   {
      for(Element e : elements.asIterable())
      {
         display.removeUnmanagedElement(e);
      }
      elements.clear();
      
      for (Iterator<Annotation> iterator = model.getAnnotationIterator(); iterator.hasNext();)
      {
         Annotation annotation = iterator.next();
         if(annotation.getImage() == null)
            continue;
         Mark m = new Mark(annotation);
         Position position = model.getPosition(annotation);
         int lineNumber = getLineNumberForPosition(position);
         m.setTopPosition(editor.getBuffer().calculateLineTop(lineNumber), "px");
         display.addUnmanagedElement(m.getElement());
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
