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
package com.google.collide.client.editor.gutter;

import com.google.collide.client.MarkLineRenderer;
import com.google.collide.client.Resources;
import com.google.collide.client.editor.Buffer;
import com.google.collide.client.editor.Editor;
import com.google.collide.client.editor.Editor.DocumentListener;
import com.google.collide.client.editor.gutter.Gutter.ClickListener;
import com.google.collide.client.util.Elements;
import com.google.collide.client.util.JsIntegerMap;
import com.google.collide.json.client.JsoArray;
import com.google.collide.shared.document.Document;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.LineFinder;
import com.google.collide.shared.document.LineInfo;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.shared.HandlerRegistration;
import elemental.css.CSSStyleDeclaration;
import elemental.html.Element;

import org.exoplatform.ide.editor.marking.Marker;
import org.exoplatform.ide.editor.marking.ProblemClickEvent;
import org.exoplatform.ide.editor.marking.ProblemClickHandler;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.DocumentEvent;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.IDocumentListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class LeftGutterNotificationManager implements DocumentListener
{
   /**
    * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
    * @version $Id:
    *
    */
   private final class ClickListenerImpl implements ClickListener
   {
      private final ProblemClickHandler handler;

      /**
       * @param handler
       */
      public ClickListenerImpl(ProblemClickHandler handler)
      {
         this.handler = handler;
      }

      @Override
      public void onClick(int y)
      {
         int lineNumber = buffer.convertYToLineNumber(y, true);
         JsoArray<Marker> jsoArray = markers.get(lineNumber);
         Marker[] arr = new Marker[jsoArray.size()];
         for (int i = 0; i < jsoArray.size(); i++)
         {
            arr[i] = jsoArray.get(i);
         }
         handler.onProblemClick(new ProblemClickEvent(arr));
      }
   }

   private Buffer buffer;

   private Gutter gutter;

   private JsIntegerMap<JsoArray<Marker>> markers = JsIntegerMap.<JsoArray<Marker>> create();

   private final JsoArray<Element> elements = JsoArray.create();

   private JsoArray<Integer> highligetLines = JsoArray.create();

   private final Resources res;

   private final Editor editor;

   private MarkLineRenderer markLineRenderer;

   private IDocument document;

   /**
    * @param buffer
    * @param gutter
    */
   public LeftGutterNotificationManager(Editor editor, Gutter gutter, Resources res)
   {
      super();
      this.editor = editor;
      this.buffer = editor.getBuffer();
      this.gutter = gutter;
      this.res = res;
   }

   /**
    * @param problem
    */
   public void addProblem(Marker problem)
   {
      int lineNumber = problem.getLineNumber() - 1;
      if (!markers.hasKey(lineNumber))
         markers.put(lineNumber, JsoArray.<Marker> create());
      markers.get(lineNumber).add(problem);
      StringBuilder message = new StringBuilder();
      JsoArray<Marker> problemList = markers.get(lineNumber);
      boolean hasError = fillMessages(problemList, message);
      Element element = createElement(lineNumber);
      element.setAttribute("title", message.toString());
      element.addClassName(getStyleForLine(problemList, hasError));
      elements.add(element);
      gutter.addUnmanagedElement(element);
      LineInfo line = editor.getDocument().getLineFinder().findLine(lineNumber);
      int length = problem.getEnd() - problem.getStart();
      try
      {
         int lines = document.getNumberOfLines(problem.getStart(), length);
         editor.getRenderer().requestRenderLine(line.line());
         highligetLines.add(line.number());
         Line nextLine = line.line();
         for (int i = 1; i < lines; i++)
         {
            line.moveToNext();
            nextLine = line.line();
            highligetLines.add(line.number());
            editor.getRenderer().requestRenderLine(nextLine);
         }
      }
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @param markerList
    * @param hasError
    * @return
    */
   private String getStyleForLine(JsoArray<Marker> markerList, boolean hasError)
   {
      String markStyle = null;
      if (hasError)
      {
         markStyle = res.notificationCss().markError();
      }
      else
      {
         markStyle = "";
         for (Marker p : markerList.asIterable())
         {
            if (p.isWarning())
            {
               markStyle = res.notificationCss().markWarning();

            }
         }
      }
      return markStyle;
   }

   private Element createElement(int lineNumber)
   {
      Element element = Elements.createDivElement();
      // Line 0 will be rendered as Line 1
      element.getStyle().setTop(buffer.calculateLineTop(lineNumber), CSSStyleDeclaration.Unit.PX);
      return element;
   }

   private boolean fillMessages(JsoArray<Marker> markers, StringBuilder message)
   {
      boolean hasError = false;
      List<String> messages = new ArrayList<String>();

      for (Marker p : markers.asIterable())
      {
         messages.add(p.getMessage());
         if (!hasError && p.isError())
         {
            hasError = true;
         }
      }

      if (messages.size() == 1)
      {
         message.append(markers.get(0).getMessage());
      }
      else
      {
         message.append("Multiple markers at this line<br>");
         for (String m : messages)
         {
            message.append("&nbsp;&nbsp;&nbsp;-&nbsp;").append(m).append("<br>");
         }
      }

      return hasError;
   }

   /**
    * @param problem
    */
   public void unmarkProblem(Marker problem)
   {
      // TODO Auto-generated method stub

   }

   /**
    * 
    */
   public void clear()
   {
      for (int i = 0, n = elements.size(); i < n; i++)
      {
         gutter.removeUnmanagedElement(elements.get(i));
      }

      JsArrayNumber keys = markers.getKeys();
      LineFinder lineFinder = editor.getDocument().getLineFinder();
      for (int i = 0; i < keys.length(); i++)
      {
         double line = keys.get(i);
         markers.erase((int)line);
      }

      markers = JsIntegerMap.<JsoArray<Marker>> create();
      elements.clear();
      markLineRenderer.clear();
      for (Integer i : highligetLines.asIterable())
      {
         try
         {
            editor.getRenderer().requestRenderLine(lineFinder.findLine(i).line());
         }
         catch (IndexOutOfBoundsException e)
         {

         }
      }
      highligetLines = JsoArray.create();
               
   }

   /**
    * @return the gutter
    */
   public Gutter getGutter()
   {
      return gutter;
   }

   /**
    * @param handler
    * @return
    */
   public HandlerRegistration addProblemClickHandler(ProblemClickHandler handler)
   {
      final ClickListenerImpl listener = new ClickListenerImpl(handler);
      gutter.getClickListenerRegistrar().add(listener);
      return new HandlerRegistration()
      {

         @Override
         public void removeHandler()
         {
            gutter.getClickListenerRegistrar().remove(listener);
         }
      };

   }

   /**
    * @return the markers
    */
   public JsIntegerMap<JsoArray<Marker>> getMarkers()
   {
      return markers;
   }

   /**
    * @see com.google.collide.client.editor.Editor.DocumentListener#onDocumentChanged(com.google.collide.shared.document.Document, com.google.collide.shared.document.Document)
    */
   @Override
   public void onDocumentChanged(Document oldDocument, Document newDocument)
   {
      document = newDocument.<IDocument> getTag("IDocument");
      markLineRenderer = new MarkLineRenderer(res.workspaceEditorCss(), this, document);
      editor.addLineRenderer(markLineRenderer);
   }

}
