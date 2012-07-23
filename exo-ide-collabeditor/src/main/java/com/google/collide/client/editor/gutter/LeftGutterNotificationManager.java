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

import org.exoplatform.ide.editor.marking.Marker;

import com.google.collide.json.shared.JsonArray;
import com.google.collide.shared.util.JsonCollections;

import com.google.collide.client.editor.Buffer;
import com.google.collide.client.util.Elements;
import com.google.collide.client.util.JsIntegerMap;
import com.google.collide.json.client.JsoArray;
import elemental.css.CSSStyleDeclaration;
import elemental.html.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class LeftGutterNotificationManager
{
   private Buffer buffer;

   private Gutter gutter;
   
   private JsIntegerMap<JsoArray<Marker>> markers;
   private final JsonArray<Element> elements = JsonCollections.createArray();

   private final GutterNotificationResources res;

   /**
    * @param buffer
    * @param gutter
    */
   public LeftGutterNotificationManager(Buffer buffer, Gutter gutter, GutterNotificationResources res)
   {
      super();
      this.buffer = buffer;
      this.gutter = gutter;
      this.res = res;
   }

   /**
    * @param problem
    */
   public void addProblem(Marker problem)
   {
      if (!markers.hasKey(problem.getLineNumber()))
         markers.put(problem.getLineNumber(), JsoArray.<Marker>create());
      markers.get(problem.getLineNumber()).add(problem);
      StringBuilder message = new StringBuilder();
      JsoArray<Marker> problemList = markers.get(problem.getLineNumber());
      boolean hasError = fillMessages(problemList, message);
      Element element = createElement(problem.getLineNumber());
      element.setAttribute("title", message.toString());
      element.addClassName(getStyleForLine(problemList, hasError));
      elements.add(element);
      gutter.addUnmanagedElement(element);
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
         markStyle = res.notificationCss().markBreakpoint();
         for (Marker p : markerList.asIterable())
         {
            if (p.isWarning())
            {
               markStyle = res.notificationCss().markWarning();

            }
            if (p.isCurrentBreakPoint())
            {
               markStyle = res.notificationCss().markBreakpointCurrent();
               break;
            }
         }
      }
      return markStyle;
   }

   private Element createElement(int lineNumber) {
      Element element = Elements.createDivElement();
      // Line 0 will be rendered as Line 1
      element.getStyle().setTop(buffer.calculateLineTop(lineNumber -1), CSSStyleDeclaration.Unit.PX);
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
      for (int i = 0, n = elements.size(); i < n; i++) {
         gutter.removeUnmanagedElement(elements.get(i));         
      }
   }

}
