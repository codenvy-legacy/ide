package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerClientService;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase.
 * Using <code>"GwtTest*"</code> naming pattern exclude them from running with
 * surefire during the test phase.
 * 
 * If you run the tests using the Maven command line, you will have to 
 * navigate with your browser to a specific url given by Maven. 
 * See http://mojo.codehaus.org/gwt-maven-plugin/user-guide/testing.html 
 * for details.
 */
public class GwtTestJavaDebugExtension extends GWTTestCase
{

   /**
    * Must refer to a valid module that sources this class.
    */
   public String getModuleName()
   {
      return "org.exoplatform.ide.extension.java.jdi.JavaDebugExtensionJUnit";
   }

   /**
    * This test will send a request to the server using the greetServer method in
    * GreetingService and verify the response.
    * @throws RequestException 
    */
   public void testGreetingService() throws RequestException
   {
      // Create the service that we will test.
      DebuggerClientService debuggerClientService = GWT.create(DebuggerClientService.class);
      ServiceDefTarget target = (ServiceDefTarget)debuggerClientService;
      target.setServiceEntryPoint(GWT.getModuleBaseURL() + "JavaDebugExtension/greet");

      // Since RPC calls are asynchronous, we will need to wait for a response
      // after this test method returns. This line tells the test runner to wait
      // up to 10 seconds before timing out.
      delayTestFinish(10000);
      AutoBean<DebuggerInfo> debuggerInfo = DebuggerExtension.AUTO_BEAN_FACTORY.create(DebuggerInfo.class);
      AutoBeanUnmarshaller<DebuggerInfo> unmarshaller = new AutoBeanUnmarshaller<DebuggerInfo>(debuggerInfo);
      // Send a request to the server.
      debuggerClientService.create("localhost", 8000, new AsyncRequestCallback<DebuggerInfo>(unmarshaller)
      {
         @Override
         public void onSuccess(DebuggerInfo result)
         {
            // Verify that the response is correct.
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

            // Now that we have received a response, we need to tell the test runner
            // that the test is complete. You must call finishTest() after an
            // asynchronous test finishes successfully, or the test will time out.
            finishTest();
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            System.err.println("--------------------------------------");
            fail("Request failure: " + exception.getMessage());
         }

         
      });
   }

}
