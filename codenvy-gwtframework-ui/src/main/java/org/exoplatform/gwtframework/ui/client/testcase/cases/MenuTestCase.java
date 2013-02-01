/**
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.exoplatform.gwtframework.ui.client.testcase.cases;

import org.exoplatform.gwtframework.ui.client.menu.MenuBar;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;
import org.exoplatform.gwtframework.ui.client.testcase.ShowCaseImageBundle;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuTestCase extends TestCase
{

   @Override
   public void draw()
   {
      //border: 1px solid rgb(204, 204, 204);
      DOM.setStyleAttribute(testCasePanel().getElement(), "border", "none");
      MenuBar menu = new MenuBar();
      testCasePanel().add(menu);

      MenuItem fileItem = menu.addItem("File");
      {

         MenuItem newItem = fileItem.addItem("New");
         newItem.setHotKey("Ctrl+N");

         newItem.addItem("XML File", new Command()
         {
            public void execute()
            {
               Window.alert("File/New/XML File Item Selected");
            }
         });

         newItem.addItem("HTML File");
         newItem.addItem(null);
         newItem.addItem("File from template...");

         fileItem.addItem(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.search()), "Search");
         MenuItem saveAllItem = fileItem.addItem(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.remove()), "Delete");
         saveAllItem.setEnabled(false);
         
         fileItem.addItem(null);
         fileItem.addItem(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.ok()), "Refresh");
      }

      MenuItem helpItem = menu.addItem("Help");
      helpItem.addItem("About");
      helpItem.setCommand(new Command()
      {
         public void execute()
         {
            Window.alert("Help/About Item Selected");
         }
      });
      
      System.out.println("= MENU DUMP ============================");
      System.out.println("" + menu);
      System.out.println("========================================");
   }

}
