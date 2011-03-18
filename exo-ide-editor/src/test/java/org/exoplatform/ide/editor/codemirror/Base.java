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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id: Base Feb 25, 2011 10:59:00 AM evgen $
 *
 */
public class Base extends GWTTestCase
{

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
    */
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.editor.EditorTest";
   }

   /**
    * constants
    */
   static final int CODEMIRROR_LOADING_PERIOD_MILISEC = 2000;
   static final int CODEMIRROR_TEXT_PARSING_PERIOD_MILISEC = 3000;   
   static final int DELAY_TEST_FINISH_MILISEC = 30000;

   /**
    * 
    * @param key
    */
//   public native void keyPress(int keyCode, boolean isCtrlKey, boolean isAltKey, boolean isShiftKey, String editorId) /*-{
//   
//       var editor = $doc.getElementById(editorId);
//       if ($doc.createEventObject) {        //Internet Explorer
//           var keyClickEvent = $doc.createEventObject();   // http://javascript.gakaa.com/document-createeventobject-4-0-5-.aspx
//           editor.fireEvent("ondblclick", keyClickEvent);
//       } else {
//           // Firefox, Safari, Opera
//           var keyboardEvent = $doc.createEvent("KeyboardEvent");   // http://help.dottoro.com/ljnxhosf.php
//           keyboardEvent.initKeyEvent("keypress", true, true, $wnd, isCtrlKey, isAltKey, isShiftKey, keyCode);  // http://help.dottoro.com/ljtxvfef.php
//           editor.dispatchEvent(keyboardEvent);
//       }
//   
//   }-*/;
   
   
   public void keyPress(int keyCode, boolean ctrlKey, boolean altKey, boolean shiftKey, String elementId)
   {
      Document.get().getElementById(elementId).dispatchEvent(Document.get().createKeyPressEvent(ctrlKey, altKey, shiftKey, false, keyCode, 0));
   }
   
}