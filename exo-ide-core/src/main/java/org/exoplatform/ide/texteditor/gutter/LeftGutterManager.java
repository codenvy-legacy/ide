// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.exoplatform.ide.texteditor.gutter;

import org.exoplatform.ide.text.store.DocumentModel;
import org.exoplatform.ide.texteditor.Buffer;
import org.exoplatform.ide.util.ListenerRegistrar.Remover;

public class LeftGutterManager
{

   private final Buffer buffer;

   private DocumentModel document;

   private DocumentModel.LineCountListener lineCountListener = new DocumentModel.LineCountListener()
   {
      @Override
      public void onLineCountChanged(DocumentModel document, int lineCount)
      {
         updateWidthFromLineCount(lineCount);
      }
   };

   private final Gutter gutter;

   private Remover lineCountListenerRemover;

   public LeftGutterManager(Gutter gutter, Buffer buffer)
   {
      this.buffer = buffer;
      this.gutter = gutter;
   }

   public Gutter getGutter()
   {
      return gutter;
   }

   public void handleDocumentChanged(DocumentModel newDocument)
   {

      if (lineCountListenerRemover != null)
      {
         lineCountListenerRemover.remove();
      }

      this.document = newDocument;

      lineCountListenerRemover = document.getLineCountListenerRegistrar().add(lineCountListener);
      updateWidthFromLineCount(document.getLineCount());
   }

   private void updateWidthFromLineCount(int lineCount)
   {
      /*
       * We want to know how many digits are in the current line count (hence the
       * log)
       */
      int width = (int)(((float)Math.log10(lineCount) + 1) * buffer.getEditorCharacterWidth());
      gutter.setWidth(width);
   }
}
