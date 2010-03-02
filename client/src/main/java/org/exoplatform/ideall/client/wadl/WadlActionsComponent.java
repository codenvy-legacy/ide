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
package org.exoplatform.ideall.client.wadl;

import org.exoplatform.ideall.client.application.component.AbstractApplicationComponent;
import org.exoplatform.ideall.client.model.wadl.WadlService;
import org.exoplatform.ideall.client.model.wadl.event.WadlServiceOutputReceiveHandler;
import org.exoplatform.ideall.client.model.wadl.event.WadlServiceOutputReceivedEvent;
import org.exoplatform.ideall.client.wadl.event.PreviewWadlOutputEvent;
import org.exoplatform.ideall.client.wadl.event.PreviewWadlOutputHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class WadlActionsComponent extends AbstractApplicationComponent implements PreviewWadlOutputHandler,
   WadlServiceOutputReceiveHandler

{

   public WadlActionsComponent()
   {
      super(new WadlActionsComponentInitializer());
   }

   @Override
   protected void registerHandlers()
   {
      handlers.addHandler(PreviewWadlOutputEvent.TYPE, this);
      handlers.addHandler(WadlServiceOutputReceivedEvent.TYPE, this);

   }

   public void onPreviewWadlOutput(PreviewWadlOutputEvent event)
   {
	  String content = context.getActiveFile().getContent();
      int indStart = content.indexOf("\"");
      int indEnd = content.indexOf("\"", indStart + 1);
      String url = "/rest" + content.substring(indStart + 1, indEnd);
	  WadlService.getInstance().getWadl(url);
   }

   public void onWadlServiceOutputReceived(WadlServiceOutputReceivedEvent event)
   {
      new WadlServiceGetForm(eventBus, context, event.getApplication());
   }

}
