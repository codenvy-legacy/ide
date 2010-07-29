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
package org.exoplatform.ide.groovy;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService;
import org.exoplatform.ide.client.module.groovy.service.groovy.GroovyServiceImpl;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.groovy.event.GroovyDeployResultReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GwtTestDeploy extends ClientTest
{

   private final static String groovyFileContent =
      "// simple groovy script\n" + "import javax.ws.rs.Path;\n" + "import javax.ws.rs.GET;\n"
         + "import javax.ws.rs.PathParam;\n" + "@Path(\"/mine\")\n" + "public class HelloWorld {\n" + "@GET\n"
         + "@Path(\"helloworld/{name}\")\n" + "public String hello(@PathParam(\"name\") String name) {\n"
         + "return \"Hello, \" + name +\"!\";\n" + "}\n" + "}\n";

   public void testDeploySuccessfull()
   {
      System.out.println("GwtTestDeploy.testDeploySuccessfull()");

      String serviceContext = ServletMapping.getURLFor(ServletMapping.DEPLOY_SUCCESSFULL);

      String fileHref = "http://host:port/rest/jcr/dev-monit/myfile.groovy";

      HandlerManager eventBus = new HandlerManager(null);

      GroovyService service = new GroovyServiceImpl(eventBus, serviceContext, null);

      eventBus.addHandler(GroovyDeployResultReceivedEvent.TYPE, new GroovyDeployResultReceivedHandler()
      {
         public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
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

      service.deploy(fileHref);

      sleepTest();
   }

   public void testDeployFailure()
   {
      System.out.println("GwtTestDeploy.testDeployFailure()");

      String serviceContext = ServletMapping.getURLFor(ServletMapping.DEPLOY_FAILURE);

      String fileHref = "http://host:port/rest/jcr/dev-monit/myfile.groovy";

      HandlerManager eventBus = new HandlerManager(null);

      GroovyService service = new GroovyServiceImpl(eventBus, serviceContext, null);

      eventBus.addHandler(GroovyDeployResultReceivedEvent.TYPE, new GroovyDeployResultReceivedHandler()
      {
         public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
         {
            if (event.getException() != null)
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

      service.deploy(fileHref);

      sleepTest();
   }

}
