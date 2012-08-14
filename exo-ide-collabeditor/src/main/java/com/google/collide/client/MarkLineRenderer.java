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
package com.google.collide.client;

import com.google.collide.client.editor.Editor;
import com.google.collide.client.editor.Editor.Css;
import com.google.collide.client.editor.gutter.LeftGutterNotificationManager;
import com.google.collide.client.editor.renderer.LineRenderer;
import com.google.collide.client.util.JsIntegerMap;
import com.google.collide.json.client.JsoArray;
import com.google.collide.shared.document.Line;

import org.exoplatform.ide.editor.marking.Marker;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.IDocument;
import org.waveprotocol.wave.model.util.Pair;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class MarkLineRenderer implements LineRenderer
{

   private final Css editorCss;

   private final LeftGutterNotificationManager notificationManager;

   private final IDocument document;

   private int renderedChars;

   private JsoArray<Integer> chunkLenght;

   private JsoArray<String> chunkStyle;

   private int index = 0;

   private JsIntegerMap<Pair<JsoArray<Integer>, JsoArray<String>>> multilineMarks = JsIntegerMap.create();

   /**
    * @param leftGutterNotificationManager 
    * @param renderer 
    * @param iDocument 
    * 
    */
   public MarkLineRenderer(Editor.Css editorCss, LeftGutterNotificationManager leftGutterNotificationManager,
      IDocument document)
   {
      this.editorCss = editorCss;
      this.notificationManager = leftGutterNotificationManager;
      this.document = document;
   }

   /**
    * @see com.google.collide.client.editor.renderer.LineRenderer#renderNextChunk(com.google.collide.client.editor.renderer.LineRenderer.Target)
    */
   @Override
   public void renderNextChunk(Target target)
   {
      if (renderedChars == -1)
      {
         target.render(1, null);
         return;
      }
      target.render(chunkLenght.get(index), chunkStyle.get(index));
      index++;
      if (index >= chunkLenght.size())
         renderedChars = -1;
   }

   /**
    * @see com.google.collide.client.editor.renderer.LineRenderer#resetToBeginningOfLine(com.google.collide.shared.document.Line, int)
    */
   @Override
   public boolean resetToBeginningOfLine(Line line, int lineNumber)
   {
      index = 0;
      chunkLenght = JsoArray.create();
      chunkStyle = JsoArray.create();
      
      if(multilineMarks.hasKey(lineNumber))
      {
         Pair<JsoArray<Integer>, JsoArray<String>> pair = multilineMarks.get(lineNumber);
         chunkLenght = pair.first;
         chunkStyle = pair.second;
         renderedChars = 0;
         return true;
      }

      if (notificationManager.getMarkers().hasKey(lineNumber))
      {
         JsoArray<Marker> array = notificationManager.getMarkers().get(lineNumber);
         int chars = 0;
         for (Marker m : array.asIterable())
         {
            try
            {
               int length = m.getEnd() - m.getStart();
               int lines = document.getNumberOfLines(m.getStart(), length);
               String style = m.isError() ? editorCss.lineRendererError() : editorCss.lineWarning();
               if (lines == 1)
               {
                  int lineOffset = document.getLineOfOffset(m.getStart());
                  int posInLine = m.getStart() - document.getLineOffset(lineOffset);
                  renderedChars = 0;
                  chunkLenght.add(posInLine - chars);
                  chars = posInLine + length + 1;
                  chunkStyle.add(null);
                  chunkLenght.add(length + 1);
                  chunkStyle.add(style);
               }
               else
               {
                  int lineOffset = document.getLineOfOffset(m.getStart());
                  int posInLine = m.getStart() - document.getLineOffset(lineOffset);
                  renderedChars = 0;
                  chunkLenght.add(posInLine - chars);
                  chars = posInLine + length + 1;
                  chunkStyle.add(null);
                  chunkLenght.add(length);
                  chunkStyle.add(style);
                  int endLine = document.getLineOfOffset(m.getEnd());
                  Line nextLine = line;
                  for(int i = 1; i < lines; i++)
                  {
                     nextLine = nextLine.getNextLine();
                     int currentLine = lineOffset + i;
                     if(currentLine != endLine)
                     {
                        JsoArray<Integer> chunkSize = JsoArray.create();
                        chunkSize.add(nextLine.length() - 1);
                        JsoArray<String> chunkClass = JsoArray.create();
                        chunkClass.add(style);
                        multilineMarks.put(currentLine, new Pair<JsoArray<Integer>, JsoArray<String>>(chunkSize,chunkClass));
                     }
                     else
                     {
                        
                        JsoArray<Integer> chunkSize = JsoArray.create();
                        chunkSize.add(m.getEnd() - document.getLineOffset(endLine));
                        JsoArray<String> chunkClass = JsoArray.create();
                        chunkClass.add(style);
                        multilineMarks.put(currentLine, new Pair<JsoArray<Integer>, JsoArray<String>>(chunkSize,chunkClass));
                     }
                  }
               }

            }
            catch (BadLocationException e)
            {
               e.printStackTrace();
            }
         }
         chunkLenght.add(line.length() - chars);
         chunkStyle.add(null);
         return true;
      }
      else
         return false;
   }

   /**
    * @see com.google.collide.client.editor.renderer.LineRenderer#shouldLastChunkFillToRight()
    */
   @Override
   public boolean shouldLastChunkFillToRight()
   {
      return false;
   }

   /**
    * 
    */
   public void clear()
   {
      multilineMarks = JsIntegerMap.create();
   }
}
