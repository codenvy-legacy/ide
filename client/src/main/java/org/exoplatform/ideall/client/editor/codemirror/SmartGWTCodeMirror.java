/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */

package org.exoplatform.ideall.client.editor.codemirror;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Overflow;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SmartGWTCodeMirror extends LockableCanvas
{

   private GWTCodeMirror codeMirror;

   public SmartGWTCodeMirror(HandlerManager eventBus)
   {
      this(eventBus, new CodeMirrorConfig());
   }

   public SmartGWTCodeMirror(HandlerManager eventBus, CodeMirrorConfig configuration)
   {
      super(eventBus);

      codeMirror = new GWTCodeMirror(eventBus, configuration);
      addChild(codeMirror);
      codeMirror.setWidth("100%");
      codeMirror.setHeight("100%");

      setOverflow(Overflow.HIDDEN);

      redraw();
   }

   @Override
   protected void onResized()
   {
      int w = getWidth();
      int h = getHeight();

      if (h < 0)
      {
         h = 0;
      }

      if (w < 0)
      {
         w = 0;
      }

      codeMirror.setWidth(w);
      codeMirror.setHeight(h);
   }

   public void setText(String text)
   {
      codeMirror.setText(text);
   }

   public String getText()
   {
      return codeMirror.getText();
   }

   public void undo()
   {
      codeMirror.undo();
   }

   public void redo()
   {
      codeMirror.redo();
   }

   public void reindentEditor()
   {
      codeMirror.reindentEditor();
   }

   public void replaceText(String text)
   {
      codeMirror.replaceText(text);
   }

   public void setLineNumbers(boolean lineNumbers)
   {
      codeMirror.setLineNumbers(lineNumbers);
   }

   public String getEditorId()
   {
      return codeMirror.getEditorId();
   }

   /**
    * Set focus on CodeMirror editor
    */
   public void setFocus()
   {
      codeMirror.setFocus();
   }

   public boolean hasRedoChanges()
   {
      return codeMirror.hasRedoChanges();
   }

   public boolean hasUndoChanges()
   {
      return codeMirror.hasUndoChanges();
   }

}
