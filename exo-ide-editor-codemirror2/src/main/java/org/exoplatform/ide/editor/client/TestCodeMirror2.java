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
package org.exoplatform.ide.editor.client;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.codemirror2.CodeMirror2;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class TestCodeMirror2 implements EntryPoint
{

   @Override
   public void onModuleLoad()
   {
      
      AbsolutePanel panel = new AbsolutePanel();
      RootPanel.get().add(panel, 50, 50);
      panel.setSize("500px", "350px");
      panel.getElement().getStyle().setBackgroundColor("#AAEEFF");
      
      CodeMirror2 editor = new CodeMirror2(MimeType.TEXT_XML);
      editor.setSize("100%", "100%");
      panel.add(editor);
      
   }

}
