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
package com.google.collide.client.editor.renderer;

import com.google.collide.client.editor.Buffer;
import com.google.collide.client.editor.Buffer.Resources;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.client.util.Elements;
import com.google.collide.json.shared.JsonArray;
import com.google.collide.shared.document.LineInfo;
import com.google.collide.shared.util.JsonCollections;
import com.google.collide.shared.util.ListenerRegistrar;
import elemental.dom.Element;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CurrentLineHighlighter
{
   private int activeLineNumber;

   private final JsonArray<ListenerRegistrar.Remover> listenerRemovers = JsonCollections.createArray();

   private final Element lineHighlighter;

   private final SelectionModel.CursorListener cursorListener = new SelectionModel.CursorListener()
   {

      @Override
      public void onCursorChange(LineInfo lineInfo, int column, boolean isExplicitChange)
      {
         if (activeLineNumber == lineInfo.number())
            return;
         activeLineNumber = lineInfo.number();
         updateActiveLine();
      }
   };

   private final Buffer buffer;

   /**
    * @param buffer
    * @param selection
    * @param res
    */
   public CurrentLineHighlighter(Buffer buffer, SelectionModel selection, Resources res)
   {
      this.buffer = buffer;
      listenerRemovers.add(selection.getCursorListenerRegistrar().add(cursorListener));
      lineHighlighter = Elements.createDivElement(res.workspaceEditorBufferCss().line());
      lineHighlighter.getClassList().add(res.workspaceEditorBufferCss().currentLine());
      lineHighlighter.getStyle().setTop(0, "PX");
      buffer.addUnmanagedElement(lineHighlighter);
   }

   /**
    * 
    */
   private void updateActiveLine()
   {
      lineHighlighter.getStyle().setTop(buffer.calculateLineTop(activeLineNumber), "PX");
   }

   public void teardown()
   {
      for (int i = 0, n = listenerRemovers.size(); i < n; i++)
      {
         listenerRemovers.get(i).remove();
      }
   }
}
