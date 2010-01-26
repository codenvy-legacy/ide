/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client;

import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.util.KeyCallback;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class IDEApplication implements EntryPoint
{

   public void onModuleLoad()
   {
      KeyIdentifier debugKey = new KeyIdentifier();
      debugKey.setCtrlKey(true);
      debugKey.setKeyName("D");
      Page.registerKey(debugKey, new KeyCallback()
      {
         public void execute(String keyName)
         {
            SC.showConsole();
         }
      });

      new IDE();
   }

}
