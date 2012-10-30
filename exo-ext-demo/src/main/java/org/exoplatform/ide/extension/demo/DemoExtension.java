/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.extension.demo;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.api.ui.menu.MainMenuAgent;
import org.exoplatform.ide.extension.Extension;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
@Extension(title = "Demo extension", id = "ide.ext.demo", version = "2.0.0")
public class DemoExtension
{

   @Inject
   public DemoExtension(MainMenuAgent menuAgent)
   {
      menuAgent.addMenuItem("DemoExtension", new Command()
      {
         @Override
         public void execute()
         {
            alert("From Demo Extension");
         }

      });
   }

   public static native void alert(String msg) /*-{
		$wnd.alert(msg);
   }-*/;

}
