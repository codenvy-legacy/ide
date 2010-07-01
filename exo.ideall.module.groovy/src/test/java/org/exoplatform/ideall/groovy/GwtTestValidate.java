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
import org.exoplatform.ideall.groovy.event.GroovyValidateResultReceivedEvent;
import org.exoplatform.ideall.groovy.event.GroovyValidateResultReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GwtTestValidate extends Test
{

   private final static String groovyFileContent =
      "// simple groovy script\n" + "import javax.ws.rs.Path;\n" + "import javax.ws.rs.GET;\n"
         + "import javax.ws.rs.PathParam;\n" + "@Path(\"/mine\")\n" + "public class HelloWorld {\n" + "@GET\n"
         + "@Path(\"helloworld/{name}\")\n" + "public String hello(@PathParam(\"name\") String name) {\n"
         + "return \"Hello, \" + name +\"!\";\n" + "}\n" + "}\n";

   public void testValidateSuccessfull()
   {
      String serviceContext = ServletMapping.getURLFor(ServletMapping.VALIDATE_SUCCESSFULL);
      String href = "/somefile.groovy";

      HandlerManager eventBus = new HandlerManager(null);
      GroovyService service = new GroovyServiceImpl(eventBus, serviceContext, null);

      eventBus.addHandler(GroovyValidateResultReceivedEvent.TYPE, new GroovyValidateResultReceivedHandler()
      {
         public void onGroovyValidateResultReceived(GroovyValidateResultReceivedEvent event)
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

      service.validate(href, groovyFileContent);

      sleepTest();
   }

   public void testValidateFailure()
   {
      String serviceContext = ServletMapping.getURLFor(ServletMapping.VALIDATE_FAILURE);

      String href = "/somefile.groovy";

      HandlerManager eventBus = new HandlerManager(null);
      GroovyService service = new GroovyServiceImpl(eventBus, serviceContext, null);

      eventBus.addHandler(GroovyValidateResultReceivedEvent.TYPE, new GroovyValidateResultReceivedHandler()
      {
         public void onGroovyValidateResultReceived(GroovyValidateResultReceivedEvent event)
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

      service.validate(href, groovyFileContent);

   }

}
