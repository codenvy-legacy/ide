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
package org.exoplatform.ide.editor.java;

import com.google.collide.client.Resources;

import com.google.collide.client.CollabEditorExtension;

import com.google.collide.client.util.Elements;

import com.google.collide.client.editor.Buffer;
import com.google.collide.client.editor.gutter.Gutter;
import com.google.collide.client.editor.gutter.Gutter.ClickListener;
import com.google.collide.client.util.JsIntegerMap;
import com.google.collide.shared.util.ListenerRegistrar.Remover;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import elemental.html.Element;

import org.exoplatform.ide.editor.java.Breakpoint.Type;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class BreakpointGutterManager
{

   private final JavaClientBundle bundle;

   private final Gutter gutter;

   private final Buffer buffer;

   private JsIntegerMap<Breakpoint> breakpoints = JsIntegerMap.create();

   private JsIntegerMap<Element> breakpointsElement = JsIntegerMap.create();

   private Element currentDebugLine;
   
   private Element currentDebugHighlighter;

   /**
    * @param gutter
    * @param buffer
    * @param instance
    */
   public BreakpointGutterManager(Gutter gutter, Buffer buffer, JavaClientBundle bundle)
   {
      this.gutter = gutter;
      this.buffer = buffer;
      this.bundle = bundle;
      gutter.setWidth(14);
      Resources resources = CollabEditorExtension.get().getContext().getResources();
      currentDebugHighlighter = Elements.createDivElement(resources.workspaceEditorBufferCss().line());
      currentDebugHighlighter.addClassName(resources.workspaceEditorBufferCss().currentLine());
      currentDebugHighlighter.getStyle().setBackgroundColor("#ffc8c8");
      currentDebugHighlighter.getStyle().setTop(0, "PX");
   }

   public Remover addLineClickListener(final ClickListener listener)
   {
      return gutter.getClickListenerRegistrar().add(new ClickListener()
      {

         @Override
         public void onClick(int y)
         {
            int lineNumber = buffer.convertYToLineNumber(y, true);
            listener.onClick(lineNumber + 1);
         }
      });
   }

   public void setBreakpoint(Breakpoint breakpoint)
   {
      if (breakpointsElement.hasKey(breakpoint.getLineNumber()))
      {
         gutter.removeUnmanagedElement(breakpointsElement.remove(breakpoint.getLineNumber()));
      }
      breakpoints.put(breakpoint.getLineNumber(), breakpoint);
      Image i = createImege(breakpoint.getType());
      Element element = (Element)i.getElement();
      element.getStyle().setHeight(buffer.getEditorLineHeight() + "px");
      element.getStyle().setPosition("absolute");
      element.getStyle().setTop(buffer.convertLineNumberToY(breakpoint.getLineNumber() - 1), "px");
      breakpointsElement.put(breakpoint.getLineNumber(), element);
      gutter.addUnmanagedElement(element);
   }

   public void setCurrentDebugLine(Breakpoint bp)
   {
      if (currentDebugLine != null)
         gutter.removeUnmanagedElement(currentDebugLine);
      Image i = createImege(bp.getType());

      currentDebugLine = (Element)i.getElement();
      currentDebugLine.getStyle().setHeight(buffer.getEditorLineHeight() + "px");
      currentDebugLine.getStyle().setPosition("absolute");
      int top = buffer.convertLineNumberToY(bp.getLineNumber() - 1);
      currentDebugLine.getStyle().setTop(top + 3, "px");
      currentDebugLine.getStyle().setRight("0px");
      currentDebugLine.getStyle().setWidth("10px");
      currentDebugLine.getStyle().setHeight("11px");
      gutter.addUnmanagedElement(currentDebugLine);
      currentDebugHighlighter.getStyle().setTop(top, "px");
      buffer.addUnmanagedElement(currentDebugHighlighter);
   }

   /**
    * @param type
    * @return
    */
   private Image createImege(Type type)
   {
      ImageResource res;
      switch (type)
      {
         case BREAKPOINT :
            res = bundle.breakpoint();
            break;

         case CURRENT :
            res = bundle.breakpointCurrent();
            break;

         //TODO add images for other breakpoint type 

         default :
            res = bundle.breakpoint();
            break;
      }
      return new Image(res);
   }

   public void removeBreakpoint(int line)
   {
      if (breakpointsElement.hasKey(line))
      {
         gutter.removeUnmanagedElement(breakpointsElement.remove(line));
         breakpoints.remove(line);
      }
   }

   /**
    * @param bp
    */
   public void removeCurrentDebugLine(Breakpoint bp)
   {
      if (currentDebugLine != null)
         gutter.removeUnmanagedElement(currentDebugLine);
      currentDebugLine = null;
      buffer.removeUnmanagedElement(currentDebugHighlighter);
   }
   
   /**
    * @return the gutter
    */
   public Gutter getGutter()
   {
      return gutter;
   }

}
