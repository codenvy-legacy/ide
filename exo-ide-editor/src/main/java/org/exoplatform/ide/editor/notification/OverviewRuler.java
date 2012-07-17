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
package org.exoplatform.ide.editor.notification;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.codemirror.CodeMirrorClientBundle;
import org.exoplatform.ide.editor.marking.Marker;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:23:13 PM Mar 21, 2012 evgen $
 * 
 */
public class OverviewRuler extends Composite implements MouseDownHandler
{
   private LayoutPanel panel;

   private LayoutPanel ruler;

   //private CodeMirror codeMirror;
   
   private Editor editor;

   private Mark bottomMark;

   private List<Marker> errors = new ArrayList<Marker>();

   private List<Marker> warnings = new ArrayList<Marker>();

   /**
    * 
    */
   public OverviewRuler(Editor editor)
   {
      //this.codeMirror = codeMirror;
      this.editor = editor;
      
      panel = new LayoutPanel();
      ruler = new LayoutPanel();
      initWidget(panel);
      panel.add(ruler);
      panel.setWidgetTopBottom(ruler, 0, Unit.PX, 13, Unit.PX);
      setWidth("100%");
      setHeight("100%");
      setStyleName(CodeMirrorClientBundle.INSTANCE.css().overviewPanel());
      bottomMark = new Mark();
      panel.add(bottomMark);
      panel.setWidgetBottomHeight(bottomMark, 2, Unit.PX, 10, Unit.PX);
      panel.setWidgetLeftRight(bottomMark, 2, Unit.PX, 2, Unit.PX);
   }

   /**
    * @param problem
    * @param message
    */
   public void addProblem(Marker problem, String message)
   {
      if (!(problem.isError() || problem.isWarning()))
         return;

      int lastLineNumber = editor.getNumberOfLines();
      int problemY = (100 * problem.getLineNumber()) / lastLineNumber;

      Mark mark = new Mark(message, getStyleName(problem), problem.getLineNumber());
      mark.addDomHandler(this, MouseDownEvent.getType());
      ruler.add(mark);
      ruler.setWidgetTopHeight(mark, problemY, Unit.PCT, 5, Unit.PX);
      ruler.setWidgetLeftRight(mark, 2, Unit.PX, 2, Unit.PX);
      if (problem.isError())
      {
         errors.add(problem);
      }
      
      if (problem.isWarning())
      {
         warnings.add(problem);
      }

      if (!errors.isEmpty())
      {
         bottomMark.setMessage("Errors: " + errors.size());
         bottomMark.setStyleName(CodeMirrorClientBundle.INSTANCE.css().overviewBottomMarkError());
      }
      else if (!warnings.isEmpty())
      {
         bottomMark.setMessage("Warnings: " + warnings.size());
         bottomMark.setStyleName(CodeMirrorClientBundle.INSTANCE.css().overviewBottomMarkWarning());
      }
   }

   /**
    * @see com.google.gwt.event.dom.client.MouseDownHandler#onMouseDown(com.google.gwt.event.dom.client.MouseDownEvent)
    */
   @Override
   public void onMouseDown(MouseDownEvent event)
   {
      editor.setCursorPosition(((Mark)event.getSource()).lineNumber, 1);
      editor.setFocus();
   }

   /**
    * @param problem
    * @return
    */
   private String getStyleName(Marker problem)
   {
      if (problem.isError())
      {
         return CodeMirrorClientBundle.INSTANCE.css().overviewMarkError();
      }
      
      if (problem.isWarning())
      {
         return CodeMirrorClientBundle.INSTANCE.css().overviewMarkWarning();
      }
      
      // default
      return CodeMirrorClientBundle.INSTANCE.css().overviewMarkError();
   }

   /**
    * 
    */
   public void clearProblems()
   {
      ruler.clear();
      warnings.clear();
      errors.clear();
      bottomMark.getElement().removeAttribute("class");
      bottomMark.getElement().removeAttribute("title");
   }

   private static class Mark extends Composite implements MouseOutHandler, MouseOverHandler
   {
      private HTML widget;

      private NotificationWidget notification;

      private int lineNumber;

      /**
       * 
       */
      public Mark()
      {
         widget = new HTML();
         initWidget(widget);
         addDomHandler(this, MouseOutEvent.getType());
         addDomHandler(this, MouseOverEvent.getType());
      }

      /**
       * 
       */
      public Mark(String message, String style, int lineNumber)
      {
         this();
         this.lineNumber = lineNumber;
         setStyleName(style);
         setMessage(message);
      }

      /**
       * @param message
       */
      public void setMessage(String message)
      {
         widget.getElement().setAttribute("title", message);
      }

      /**
       * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
       */
      @Override
      public void onMouseOver(MouseOverEvent event)
      {
         if (widget.getElement().hasAttribute("title") && widget.getElement().getAttribute("title").isEmpty())
            return;
         if (notification == null)
            notification = new NotificationWidget(getElement());
      }

      /**
       * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
       */
      @Override
      public void onMouseOut(MouseOutEvent event)
      {
         if (notification != null)
            notification.destroy();
         notification = null;
      }
   }

}
