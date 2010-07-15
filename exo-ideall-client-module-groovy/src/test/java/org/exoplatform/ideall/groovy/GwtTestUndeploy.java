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

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ideall.client.module.groovy.service.groovy.GroovyService;
import org.exoplatform.ideall.client.module.groovy.service.groovy.GroovyServiceImpl;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.GroovyUndeployResultReceivedEvent;
import org.exoplatform.ideall.client.module.groovy.service.groovy.event.GroovyUndeployResultReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GwtTestUndeploy extends ClientTest
{
   
   public void testUndeploySuccessfull() {
      System.out.println("GwtTestUndeploy.testUndeploySuccessfull()");
      
      String serviceContext = ServletMapping.getURLFor(ServletMapping.UNDEPLOY_SUCCESSFULL);
      
      String fileHref = "http://host:port/rest/jcr/dev-monit/myfile.groovy";
      
      HandlerManager eventBus = new HandlerManager(null);
      
      GroovyService service = new GroovyServiceImpl(eventBus, serviceContext, null);
      
      eventBus.addHandler(GroovyUndeployResultReceivedEvent.TYPE, new GroovyUndeployResultReceivedHandler() {
         public void onGroovyUndeployResultReceived(GroovyUndeployResultReceivedEvent event)
         {
            if (event.getException() == null) {
               finishTest();
            } else {
               fail();
            }
         }
      });
      
      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler() {
         public void onError(ExceptionThrownEvent event)
         {
            fail();
         }
      });
      
      service.undeploy(fileHref);
      
      sleepTest();      
   }
   
   public void testUndeployFailure() {
      System.out.println("GwtTestUndeploy.testUndeployFailure()");
      
      String serviceContext = ServletMapping.getURLFor(ServletMapping.UNDEPLOY_FAILURE);
      
      String fileHref = "http://host:port/rest/jcr/dev-monit/myfile.groovy";
      
      HandlerManager eventBus = new HandlerManager(null);
      
      GroovyService service = new GroovyServiceImpl(eventBus, serviceContext, null);
      
      eventBus.addHandler(GroovyUndeployResultReceivedEvent.TYPE, new GroovyUndeployResultReceivedHandler() {
         public void onGroovyUndeployResultReceived(GroovyUndeployResultReceivedEvent event)
         {
            if (event.getException() == null) {
               fail();
            } else {
               ServerException e = (ServerException)event.getException();
               if (e.getHTTPStatus() != HTTPStatus.CONFLICT) {
                  fail();
               } else {
                  finishTest();
               }
            }
         }
      });
      
      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler() {
         public void onError(ExceptionThrownEvent event)
         {
            fail();
         }
      });
      
      service.undeploy(fileHref);
      
      sleepTest();      
   }

}
