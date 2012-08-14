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

import com.google.collide.client.editor.NotificationWidget;

import com.google.collide.client.MarkLineRenderer;
import com.google.collide.client.Resources;
import com.google.collide.client.editor.Buffer;
import com.google.collide.client.editor.Editor;
import com.google.collide.client.editor.Editor.DocumentListener;
import com.google.collide.client.editor.gutter.Gutter.ClickListener;
import com.google.collide.client.util.Elements;
import com.google.collide.client.util.JsIntegerMap;
import com.google.collide.json.client.JsoArray;
import com.google.collide.mvp.CompositeView;
import com.google.collide.shared.document.Document;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.LineFinder;
import com.google.collide.shared.document.LineInfo;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.shared.HandlerRegistration;
import elemental.css.CSSStyleDeclaration;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Element;

import org.exoplatform.ide.editor.marking.Marker;
import org.exoplatform.ide.editor.marking.ProblemClickEvent;
import org.exoplatform.ide.editor.marking.ProblemClickHandler;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.IDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class NotificationManager implements DocumentListener
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

   private Gutter leftGutter;

   private JsIntegerMap<JsoArray<Marker>> markers = JsIntegerMap.<JsoArray<Marker>> create();

   private final JsoArray<Element> elements = JsoArray.create();

   private JsoArray<Integer> highligetLines = JsoArray.create();

   private final Resources res;

   private final Editor editor;

   private MarkLineRenderer markLineRenderer;

   private IDocument document;

   private final Gutter overviewGutter;

   private NotificationMark bottomMark;

   private int errors, warnings;

   private JsoArray<NotificationMark> overviewMarks = JsoArray.<NotificationManager.NotificationMark> create();

   /**
    * @param buffer
    * @param gutter
    * @param overviewGutter 
    */
   public NotificationManager(Editor editor, Gutter gutter, Gutter overviewGutter, Resources res)
   {
      super();
      this.editor = editor;
      this.overviewGutter = overviewGutter;
      this.buffer = editor.getBuffer();
      this.leftGutter = gutter;
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
      NotificationMark m = new NotificationMark(res.notificationCss());
      m.setTopPosition(buffer.calculateLineTop(lineNumber), "px");
      m.setMessage(message.toString());
      m.setStyleName(getStyleForLine(problemList, hasError));
      elements.add(m.getElement());
      leftGutter.addUnmanagedElement(m.getElement());
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
      addOverviewMark(problem, message.toString());

   }

   /**
    * @param problem
    * @param string
    */
   private void addOverviewMark(Marker problem, String string)
   {
      NotificationMark mark = new NotificationMark(problem, string, res.notificationCss(), editor);
      mark.setTopPosition((100 * problem.getLineNumber()) / document.getNumberOfLines(), "%");
      overviewGutter.addUnmanagedElement(mark.getElement());
      overviewMarks.add(mark);
      if (problem.isError())
      {
         errors++;
      }

      if (problem.isWarning())
      {
         warnings++;
      }

      if (errors != 0)
      {
         bottomMark.setMessage("Errors: " + errors);
         bottomMark.setStyleName(res.notificationCss().overviewBottomMarkError());
      }
      else if (warnings != 0)
      {
         bottomMark.setMessage("Warnings: " + warnings);
         bottomMark.setStyleName(res.notificationCss().overviewBottomMarkWarning());
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

//   private Element createElement(int lineNumber)
//   {
//      Element element = Elements.createDivElement();
//      // Line 0 will be rendered as Line 1
//      element.getStyle().setTop(buffer.calculateLineTop(lineNumber), CSSStyleDeclaration.Unit.PX);
//      return element;
//   }

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
         leftGutter.removeUnmanagedElement(elements.get(i));
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
         catch (IndexOutOfBoundsException ignore)
         {
         }
      }
      errors = 0;
      warnings = 0;
      highligetLines = JsoArray.create();
      for (NotificationMark m : overviewMarks.asIterable())
      {
         overviewGutter.removeUnmanagedElement(m.getElement());
      }
      bottomMark.getElement().removeAttribute("class");
      bottomMark.getElement().removeAttribute("title");
   }

   /**
    * @return the gutter
    */
   public Gutter getLeftGutter()
   {
      return leftGutter;
   }

   /**
    * @param handler
    * @return
    */
   public HandlerRegistration addProblemClickHandler(ProblemClickHandler handler)
   {
      final ClickListenerImpl listener = new ClickListenerImpl(handler);
      leftGutter.getClickListenerRegistrar().add(listener);
      return new HandlerRegistration()
      {

         @Override
         public void removeHandler()
         {
            leftGutter.getClickListenerRegistrar().remove(listener);
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
      bottomMark = new NotificationMark(res.notificationCss());
      bottomMark.getElement().getStyle().setBottom(2, "px");
      overviewGutter.addUnmanagedElement(bottomMark.getElement());
   }

   private static class NotificationMark extends CompositeView<Marker> implements EventListener
   {
      private final NotificationCss css;

      private Editor editor;

      private NotificationWidget notification;

      /**
       * 
       */
      public NotificationMark(NotificationCss css)
      {
         this.css = css;
         Element element = Elements.createDivElement();
         setElement(element);
         element.addEventListener(Event.MOUSEOUT, this, false);
         element.addEventListener(Event.MOUSEOVER, this, false);
         element.addEventListener(Event.MOUSEDOWN, this, false);
      }

      /**
       * 
       */
      public NotificationMark(Marker marker, String message, NotificationCss css, Editor editor)
      {
         this(css);
         this.editor = editor;
         setMessage(message);
         setStyleName(getStyleName(marker));
         setDelegate(marker);
      }

      public void setStyleName(String style)
      {
         getElement().setAttribute("class", style);
      }

      /**
       * @param message
       */
      public void setMessage(String message)
      {
         getElement().setAttribute("title", message);
      }

      /**
       * @see elemental.events.EventListener#handleEvent(elemental.events.Event)
       */
      @Override
      public void handleEvent(Event evt)
      {
         if (evt.getType().equals(Event.MOUSEDOWN))
         {
            if (getDelegate() != null)
            {
               LineInfo lineInfo = editor.getDocument().getLineFinder().findLine(getDelegate().getLineNumber() - 1);
               editor.getSelection().setCursorPosition(lineInfo, 0);
            }
         }
         if (evt.getType().equals(Event.MOUSEOVER))
         {
            if (getElement().hasAttribute("title") && getElement().getAttribute("title").isEmpty())
               return;
            if (notification == null)
               notification = new NotificationWidget((com.google.gwt.user.client.Element)getElement(), css.popupNotification());
         }
         if (evt.getType().equals(Event.MOUSEOUT))
         {
            if (notification != null)
               notification.destroy();
            notification = null;
         }

      }

      /**
       * @param problem
       * @return
       */
      private String getStyleName(Marker problem)
      {
         if (problem.isError())
         {
            return css.overviewMarkError();
         }

         if (problem.isWarning())
         {
            return css.overviewMarkWarning();
         }

         // default
         return css.overviewMarkError();
      }

      public void setTopPosition(int top, String unit)
      {
         getElement().getStyle().setTop(top, unit);
      }
   }
}
