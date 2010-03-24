/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ideall.client.model.discovery;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.ideall.client.model.discovery.event.EntryPointsReceivedEvent;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;



/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class MockEntryPointSeviceImpl extends MockEntryPointService
{
   private HandlerManager eventBus;
   
   public MockEntryPointSeviceImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }
   
   @Override
   public void getEntryPoints()
   {
      Loader.getInstance().show();
      
      new Timer() {

         @Override
         public void run()
         {
            Loader.getInstance().hide();
            
            List<String> entryPoint = new ArrayList<String>(); 
            entryPoint.add("dsalf ansdfk dsank fas ");
            entryPoint.add("dslf dlf asf as ;las");
            
            eventBus.fireEvent(new EntryPointsReceivedEvent(entryPoint));
            
         }
         
      }.schedule(2000);
   }
}

