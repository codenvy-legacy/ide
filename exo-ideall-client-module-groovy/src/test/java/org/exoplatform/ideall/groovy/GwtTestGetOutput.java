/**
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ideall.groovy;

import java.util.ArrayList;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ideall.client.module.groovy.service.RestServiceOutput;
import org.exoplatform.ideall.client.module.groovy.service.SimpleParameterEntry;
import org.exoplatform.ideall.client.module.groovy.service.groovy.GroovyService;
import org.exoplatform.ideall.client.module.groovy.service.groovy.GroovyServiceImpl;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.RestServiceOutputReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GwtTestGetOutput extends ClientTest
{

   public void testGetOutputSuccessfull()
   {
      System.out.println("GwtTestGwtOutput.testGetOutputSuccessfull()");

      String serviceContext = "";

      String url = ServletMapping.getURLFor(ServletMapping.GETOUTPUT_SUCCESSFULL) + "/jcr/dev-monit/myfile.groovy";
      String method = "POST";

      HandlerManager eventBus = new HandlerManager(null);

      GroovyService service = new GroovyServiceImpl(eventBus, serviceContext, null);

      eventBus.addHandler(RestServiceOutputReceivedEvent.TYPE, new RestServiceOutputReceivedHandler()
      {
         public void onRestServiceOutputReceived(RestServiceOutputReceivedEvent event)
         {
            if (event.getException() == null)
            {
               finishTest();
            }
            else
            {
               fail();
            }
         }
      });

      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail();
         }
      });

      service.getOutput(url, method, new ArrayList<SimpleParameterEntry>(), new ArrayList<SimpleParameterEntry>(),
         "test body");

      sleepTest();
   }

   public void testGetOutputCustomStatus()
   {
      System.out.println("GwtTestGwtOutput.testGetOutputFailure()");

      String serviceContext = "";

      String url = ServletMapping.getURLFor(ServletMapping.GETOUTPUT_CUSTOM_STATUS) + "/jcr/dev-monit/myfile.groovy";
      String method = "POST";

      HandlerManager eventBus = new HandlerManager(null);

      GroovyService service = new GroovyServiceImpl(eventBus, serviceContext, null);

      eventBus.addHandler(RestServiceOutputReceivedEvent.TYPE, new RestServiceOutputReceivedHandler()
      {
         public void onRestServiceOutputReceived(RestServiceOutputReceivedEvent event)
         {
            if (event.getException() != null)
            {
               assertEquals(((ServerException)event.getException()).getHTTPStatus(), HTTPStatus.LOCKED);
               finishTest();
            }
            else
            {
               RestServiceOutput output = event.getOutput();
               assertEquals(output.getResponse().getStatusCode(), HTTPStatus.LOCKED);
               finishTest();
            }
         }
      });

      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail();
         }
      });

      service.getOutput(url, method, new ArrayList<SimpleParameterEntry>(), new ArrayList<SimpleParameterEntry>(),
         "test body");

      sleepTest();
   }

}
