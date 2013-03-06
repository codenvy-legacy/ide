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
package com.codenvy.ide.texteditor.renderer;


import com.codenvy.ide.Resources;
import com.codenvy.ide.text.store.LineInfo;
import com.codenvy.ide.texteditor.Buffer;
import com.codenvy.ide.texteditor.selection.SelectionModel;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.util.ListenerRegistrar;
import com.codenvy.ide.util.dom.Elements;


import elemental.html.Element;

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
    * @param editor
    */
   public CurrentLineHighlighter(Buffer buffer, SelectionModel selection, Resources res)
   {
      this.buffer = buffer;
      listenerRemovers.add(selection.getCursorListenerRegistrar().add(cursorListener));
      lineHighlighter = Elements.createDivElement(res.workspaceEditorBufferCss().line());
      lineHighlighter.addClassName(res.workspaceEditorBufferCss().currentLine());
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
