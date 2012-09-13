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

import com.google.collide.client.CollabEditor;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.editor.gutter.Gutter;
import com.google.collide.client.editor.gutter.Gutter.Position;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;
import org.exoplatform.ide.editor.java.hover.JavaTypeHover;
import org.exoplatform.ide.editor.text.Document;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaEditor extends CollabEditor
{

   private BreakpointGutterManager breakPointManager;

   /**
    * @param mimeType
    */
   public JavaEditor(String mimeType)
   {
      super(mimeType);
      Gutter gutter =
         editor.createGutter(false, Position.LEFT, CollabEditorExtension.get().getContext().getResources()
            .workspaceEditorCss().leftGutterBase());
      breakPointManager = new BreakpointGutterManager(gutter, editor.getBuffer(), JavaClientBundle.INSTANCE);
      editorBundle.getAutocompleter().addLanguageSpecificAutocompleter(new JavaAutocompleter());
   }

   /**
    * @return the breakPointManager
    */
   public BreakpointGutterManager getBreakPointManager()
   {
      return breakPointManager;
   }
   
   /**
    * @see com.google.collide.client.CollabEditor#setText(java.lang.String)
    */
   @Override
   public void setText(String text)
   {
      super.setText(text);
      getHoverPresenter().addHover(Document.DEFAULT_CONTENT_TYPE, new JavaTypeHover(IDE.eventBus()));
   }
   
   /**
    * @see com.google.collide.client.CollabEditor#getCursorOffsetLeft()
    */
   @Override
   public int getCursorOffsetLeft()
   {
      return super.getCursorOffsetLeft() + breakPointManager.getGutter().getWidth();
   }

}
