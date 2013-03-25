/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.google.collide.client.editor.renderer;

import com.codenvy.ide.client.util.Elements;
import com.google.collide.client.editor.Buffer;
import com.google.collide.client.editor.ViewportModel;
import com.google.collide.client.editor.folding.FoldMarker;
import com.google.collide.client.editor.folding.FoldingManager;
import com.google.collide.client.editor.gutter.Gutter;
import elemental.css.CSSStyleDeclaration;
import elemental.dom.Node;
import elemental.dom.NodeList;
import elemental.html.Element;

import org.exoplatform.ide.json.client.JsIntegerMap;

/**
 * A renderer for the fold markers in the left gutter.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FoldMarkRenderer.java Mar 12, 2013 2:14:05 PM azatsarynnyy $
 *
 */
public class FoldMarkRenderer
{
   private final Buffer buffer;

   private final Gutter gutter;

   private int previousBottomLineNumber = -1;

   private int previousTopLineNumber = -1;

   private JsIntegerMap<Element> lineNumberToElementCache;

   private final ViewportModel viewport;

   private final FoldingManager foldingManager;

   public FoldMarkRenderer(Buffer buffer, Gutter gutter, ViewportModel viewport, FoldingManager foldingManager)
   {
      this.buffer = buffer;
      this.gutter = gutter;
      this.lineNumberToElementCache = JsIntegerMap.create();
      this.viewport = viewport;
      this.foldingManager = foldingManager;
   }

   void renderImpl(int updateBeginLineNumber)
   {
      int topLineNumber = viewport.getTopLineNumber();
      int bottomLineNumber = viewport.getBottomLineNumber();

      if (previousBottomLineNumber == -1 || topLineNumber > previousBottomLineNumber
         || bottomLineNumber < previousTopLineNumber)
      {

         if (previousBottomLineNumber > -1)
         {
            garbageCollectLines(previousTopLineNumber, previousBottomLineNumber);
         }

         fillOrUpdateLines(topLineNumber, bottomLineNumber);
      }
      else
      {
         /*
          * The viewport was shifted and part of the old viewport will be in the
          * new viewport.
          */
         // first garbage collect any lines that have gone off the screen
         if (previousTopLineNumber < topLineNumber)
         {
            // off the top
            garbageCollectLines(previousTopLineNumber, topLineNumber - 1);
         }

         if (previousBottomLineNumber > bottomLineNumber)
         {
            // off the bottom
            garbageCollectLines(bottomLineNumber + 1, previousBottomLineNumber);
         }

         /*
          * Re-create any line numbers that are now visible or have had their
          * positions shifted.
          */
         if (previousTopLineNumber > topLineNumber)
         {
            // new lines at the top
            fillOrUpdateLines(topLineNumber, previousTopLineNumber - 1);
         }

         if (updateBeginLineNumber >= 0 && updateBeginLineNumber <= bottomLineNumber)
         {
            // lines updated in the middle; redraw everything below
            fillOrUpdateLines(updateBeginLineNumber, bottomLineNumber);
         }
         else
         {
            // only check new lines scrolled in from the bottom
            if (previousBottomLineNumber < bottomLineNumber)
            {
               fillOrUpdateLines(previousBottomLineNumber, bottomLineNumber);
            }
         }
      }

      previousTopLineNumber = viewport.getTopLineNumber();
      previousBottomLineNumber = viewport.getBottomLineNumber();
   }

   void render()
   {
      renderImpl(-1);
   }

   /**
    * Re-render all line numbers including and after lineNumber to account for
    * spacer movement.
    */
   void renderLineAndFollowing(int lineNumber)
   {
      renderImpl(lineNumber);
   }

   private void fillOrUpdateLines(int beginLineNumber, int endLineNumber)
   {
      for (int i = beginLineNumber; i <= endLineNumber; i++)
      {
         if (buffer.modelLine2VisibleLine(i) == -1)
         {
            garbageCollectLines(i, i);
            continue;
         }

         FoldMarker foldMarker = foldingManager.findFoldMarker(i, false);
         if (foldMarker == null)
         {
            garbageCollectLines(i, i);
            continue;
         }
         if (!foldMarker.isCollapsed())
         {
            foldMarker = foldingManager.findFoldMarker(i, true);
            if (foldMarker == null)
            {
               garbageCollectLines(i, i);
               continue;
            }
         }

         Element lineElement = lineNumberToElementCache.get(i);
         if (lineElement != null)
         {
            updateElementPosition(lineElement, i, foldMarker);
         }
         else
         {
            Element element = createElement(i, foldMarker);
            lineNumberToElementCache.put(i, element);
            gutter.addUnmanagedElement(element);
         }
      }
   }

   private void updateElementPosition(Element foldMarkElement, int lineNumber, FoldMarker foldMarker)
   {
      final int lineHeight = buffer.getEditorLineHeight();
      final int elementHeight = 9;
      final int freeSpaceAbove = (lineHeight - elementHeight) / 2;
      foldMarkElement.getStyle().setTop(buffer.calculateLineTop(lineNumber) + freeSpaceAbove,
         CSSStyleDeclaration.Unit.PX);

      NodeList childNodes = foldMarkElement.getChildNodes();
      for (int i = 0; i < childNodes.getLength(); i++)
      {
         foldMarkElement.removeChild(childNodes.item(i));
      }

      foldMarkElement.appendChild((Node)foldMarker.getImage().getElement());
   }

   private Element createElement(int lineNumber, FoldMarker foldMarker)
   {
      Element element = Elements.createDivElement();
      element.appendChild((Node)foldMarker.getImage().getElement());

      element.getStyle().setHeight(buffer.getEditorLineHeight() + "px");
      element.getStyle().setPosition("absolute");
      element.getStyle().setCursor("pointer");

      final int lineHeight = buffer.getEditorLineHeight();
      final int elementHeight = 9;
      final int freeSpaceAbove = (lineHeight - elementHeight) / 2;

      element.getStyle().setTop(buffer.calculateLineTop(lineNumber) + freeSpaceAbove, "px");

      return element;
   }

   private void garbageCollectLines(int beginLineNumber, int endLineNumber)
   {
      for (int i = beginLineNumber; i <= endLineNumber; i++)
      {
         Element lineElement = lineNumberToElementCache.get(i);
         if (lineElement != null)
         {
            gutter.removeUnmanagedElement(lineElement);
            lineNumberToElementCache.erase(i);
         }
         else
         {
            // don't throws exception because line may be folded in this case
            continue;
            //            throw new IndexOutOfBoundsException("Tried to garbage collect line number " + i
            //               + " when it does not exist.");
         }
      }
   }

   /**
    * Once torn down, this instance cannot be used again.
    */
   void teardown()
   {
   }

}
