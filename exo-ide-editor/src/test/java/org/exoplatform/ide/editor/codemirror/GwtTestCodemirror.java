/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.editor.codemirror;

import java.util.HashMap;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GwtTestCodemirror Feb 10, 2011 11:59:57 AM evgen $
 *
 */
public class GwtTestCodemirror extends Base
{

   private HandlerManager eventBus;

   private Editor editor;

   private CodeMirrorPlainTextProduser produser;

   private File file;

   private HashMap<String, Object> params;

   private final String CONTENT = "aaaaaaaaaaaaaaa";

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#gwtSetUp()
    */
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventBus = new HandlerManager(null);
      file = new File("");

      file.setContent(CONTENT);
   }

   private void createEditor()
   {
      RootPanel.get().clear();

      params = new HashMap<String, Object>();
      params.put(CodeMirrorParams.IS_READ_ONLY, false);
      params.put(CodeMirrorParams.IS_SHOW_LINE_NUMER, true);

      produser = new CodeMirrorPlainTextProduser(MimeType.TEXT_PLAIN, "Codemirror plain text editor", "txt", true);

      try
      {
         editor = produser.createEditor(file, eventBus, params);
         RootPanel.get().add(editor);
      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }

   }

   public void testGetText()
   {
//
//      eventBus.addHandler(EditorInitializedEvent.TYPE, new EditorInitializedHandler()
//      {
//         @Override
//         public void onEditorInitialized(EditorInitializedEvent event)
//         {
//            System.out
//               .println("GwtTestCodemirror.testGetText().new EditorInitializedHandler() {...}.onEditorInitialized()");
//            //            editor.setText(CONTENT);
//            //            System.out.println("setContent-----------------------");
//            System.out.println(editor.getText());
//            assertEquals(CONTENT, editor.getText());
//            finishTest();
//         }
//      });
//      createEditor();
//      //      new Timer()
//      //      {
//      //
//      //         @Override
//      //         public void run()
//      //         {
//      //            // TODO Auto-generated method stub
//      //            //            System.out.println("content - " + editor.getText());
//      //            //            assertEquals(CONTENT, editor.getText());
//      //
//      //         }
//      //      }.schedule(3000);
//      delayTestFinish(50000);
   }
}
