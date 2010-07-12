/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ideall.plugin.groovy;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;
import org.exoplatform.ideall.plugin.groovy.event.DeployGroovyScriptEvent;
import org.exoplatform.ideall.plugin.groovy.event.PreviewWadlOutputEvent;
import org.exoplatform.ideall.plugin.groovy.event.SetAutoloadEvent;
import org.exoplatform.ideall.plugin.groovy.event.UndeployGroovyScriptEvent;
import org.exoplatform.ideall.plugin.groovy.event.ValidateGroovyScriptEvent;

import com.google.gwt.event.shared.HandlerManager;
import com.sun.net.ssl.internal.www.protocol.https.Handler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class GroovyPluginEventHandler
{

   private HandlerManager eventBus;
   
   private AbstractApplicationContext context;
   
   private Handlers handlers;
   
   public GroovyPluginEventHandler(HandlerManager eventBus, AbstractApplicationContext context) {
      this.eventBus = eventBus;
      this.context = context;
      
      handlers = new Handlers(eventBus);
      
      handlers.addHandler(ValidateGroovyScriptEvent.TYPE, this);
      handlers.addHandler(GroovyValidateResultReceivedEvent.TYPE, this);

      handlers.addHandler(DeployGroovyScriptEvent.TYPE, this);
      handlers.addHandler(GroovyDeployResultReceivedEvent.TYPE, this);

      handlers.addHandler(UndeployGroovyScriptEvent.TYPE, this);
      handlers.addHandler(GroovyUndeployResultReceivedEvent.TYPE, this);

      handlers.addHandler(RestServiceOutputReceivedEvent.TYPE, this);

      handlers.addHandler(SetAutoloadEvent.TYPE, this);

      handlers.addHandler(PreviewWadlOutputEvent.TYPE, this);
      handlers.addHandler(WadlServiceOutputReceivedEvent.TYPE, this);      
   }
   
}
