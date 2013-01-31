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
package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GwtTestCombobox extends GwtComponentTest
{

   private final String COMBOBOX_NAME = "My Test Combobox";

   private String innerHtml;

   public void testCreationButton()
   {
      ComboBoxField combobox = new ComboBoxField();
      combobox.setName(COMBOBOX_NAME);
      RootPanel.get().add(combobox);
      String innerhtml = Document.get().getBody().getInnerHTML();
      assertTrue(innerhtml.contains(COMBOBOX_NAME));
   }

   /**
    * After click on combobox,
    * select list must appear.
    */
   public void testClickEvent()
   {
      final String[] listTypes1 = {"value 1", "value 2", "value 3", "value 4", "value 5"};

      ComboBoxField combobox = new ComboBoxField();
      combobox.setName(COMBOBOX_NAME);
      combobox.setValueMap(listTypes1);

      RootPanel.get().add(combobox);
      combobox.image.getElement()
         .dispatchEvent(Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false));

      Timer timer = new Timer()
      {
         public void run()
         {
            innerHtml = Document.get().getBody().getInnerHTML();
            assertTrue(innerHtml.contains("value 1"));
            assertTrue(innerHtml.contains("value 2"));
            assertTrue(innerHtml.contains("value 3"));
            assertTrue(innerHtml.contains("value 4"));
            assertTrue(innerHtml.contains("value 5"));
            finishTest();
         }
      };
      // Set a delay period significantly longer than the
      // event is expected to take.
      delayTestFinish(500);
      // Schedule the event and return control to the test system.
      timer.schedule(100);
   }
}
