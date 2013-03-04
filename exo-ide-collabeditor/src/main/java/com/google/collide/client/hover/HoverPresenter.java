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
package com.google.collide.client.hover;

import com.google.collide.client.CollabEditor;
import com.google.collide.client.code.popup.EditorPopupController.PopupRenderer;
import com.google.collide.client.code.popup.EditorPopupController.Remover;
import com.google.collide.client.editor.Editor;
import com.google.collide.client.editor.MouseHoverManager.MouseHoverListener;
import com.google.collide.client.ui.menu.PositionController.VerticalAlign;
import com.google.collide.client.util.logging.Log;
import com.google.collide.json.client.JsoStringMap;
import com.google.collide.shared.document.LineInfo;
import elemental.dom.Element;

import org.exoplatform.ide.editor.client.hover.TextHover;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class HoverPresenter
{

   private JsoStringMap<TextHover> hovers = JsoStringMap.create();

   private final IDocument document;

   private final CollabEditor collabEditor;

   private IRegion currentRegion;

   private Remover currentPopup;

   /**
    * @param collabEditor
    */
   public HoverPresenter(CollabEditor collabEditor, Editor editor, IDocument document)
   {
      this.collabEditor = collabEditor;
      this.document = document;
      editor.getMouseHoverManager().addMouseHoverListener(new MouseHoverListener()
      {

         @Override
         public void onMouseHover(int x, int y, LineInfo lineInfo, int column)
         {
            computateInformation(lineInfo, column);
         }
      });
   }

   /**
    * @param lineInfo
    * @param column
    */
   private void computateInformation(LineInfo lineInfo, int column)
   {
      int offset = 0;
      String contentType = null;
      try
      {
         offset = document.getLineOffset(lineInfo.number()) + column;
         contentType = document.getContentType(offset);
      }
      catch (BadLocationException e)
      {
         Log.error(getClass(), e);
         return;
      }
      TextHover hover = hovers.get(contentType);
      if (hover == null)
         return;
      showHover(hover, offset, lineInfo);
   }

   /**
    * @param hover
    */
   private void showHover(TextHover hover, int offset, LineInfo lineInfo)
   {
      IRegion hoverRegion = hover.getHoverRegion(collabEditor, offset);
      if(hoverRegion == null)
         return;
      
      if (hoverRegion.equals(currentRegion))
      {
         if (currentPopup != null && currentPopup.isVisibleOrPending())
            return;
      }

      currentRegion = hoverRegion;
      if (currentPopup != null)
         currentPopup.remove();

      com.google.gwt.user.client.Element element = hover.getHoverInfo(collabEditor, hoverRegion);
      if (element == null)
         return;
      int lineOffset;
      try
      {
         lineOffset = document.getLineOffset(lineInfo.number());
         currentPopup =
            collabEditor
               .getEditorBundle()
               .getEditorPopupController()
               .showPopup(lineInfo, hoverRegion.getOffset() - lineOffset,
                  (hoverRegion.getOffset() + hoverRegion.getLength()) - lineOffset, null,
                  new PopupRendererImpl((Element)element), null, VerticalAlign.BOTTOM, true, 400);
      }
      catch (BadLocationException e)
      {
         Log.error(getClass(), e);
      }

   }

   public void addHover(String contentType, TextHover hover)
   {
      hovers.put(contentType, hover);
   }

   class PopupRendererImpl implements PopupRenderer
   {

      private Element element;

      /**
       * @param element
       */
      public PopupRendererImpl(Element element)
      {
         this.element = element;
      }

      /**
       * @see com.google.collide.client.code.popup.EditorPopupController.PopupRenderer#renderDom()
       */
      @Override
      public Element renderDom()
      {
         return element;
      }

   }
}
