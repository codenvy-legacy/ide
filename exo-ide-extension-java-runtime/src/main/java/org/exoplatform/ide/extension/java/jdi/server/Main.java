/*
 * Copyright (C) 2012 eXo Platform SAS.
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
 */
package org.exoplatform.ide.extension.java.jdi.server;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
/*
 * Copyright (C) 2012 eXo Platform SAS.
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
 */

import org.exoplatform.ide.extension.java.jdi.server.model.BreakPointImpl;
import org.exoplatform.ide.extension.java.jdi.server.model.LocationImpl;
import org.exoplatform.ide.extension.java.jdi.server.model.VariablePathImpl;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.DebugApplicationInstance;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Main
{
   private static final Log logger = ExoLogger.getLogger(Main.class);

   public static void main(String[] args) throws Exception
   {

      for (InetAddress a : InetAddress.getAllByName("cfet.exoplatform.org")){
         System.out.println(a.getHostAddress());
      }
/*
    long st = System.currentTimeMillis();
      ApplicationRunner runner = new CloudfoundryApplicationRunner(
         "http://api.cloudfoundry.com",
         "andrew00x@gmail.com",
         "UoPSax-25",
         2
      );
      ApplicationInstance app = runner.runApplication(new URL("http://172.19.20.13/~andrew/aaa/pico-book-service.war"));
      long e = System.currentTimeMillis();
      System.out.println((e-st)/1000);
      System.out.println(app);

      runner.stopApplication(app.getName());

      if (true)return;
*/

//      Thread.sleep(10000);

//      Debugger d = Debugger.newInstance(app.getDebugHost(), app.getDebugPort());
//      Debugger d = Debugger.newInstance("localhost", 8000);
//      try
//      {
//         logger.info(d.getVmName());
//         logger.info(d.getVmVersion());
//         logger.info(d.getBreakPoints());
//         d.addBreakPoint(new BreakPointImpl(new LocationImpl("org.everrest.sample.book.BookService", 51)/*, "id != null && id.equals(\"101\")"*/));
//         logger.info(d.getBreakPoints());
//
//         BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
//         try
//         {
//            String s;
//            while (!"exit".equals(s=r.readLine()))
//            {
//               if ("resume".equals(s))
//               {
//                  d.resume();
//               }
//            }
//         }
//         catch (IOException e1)
//         {
//            logger.error(e1.getMessage(), e1);
//         }
//
//         d.setValue(new VariablePathImpl(Arrays.asList("book", "author")),
//            "\"\" + book"
//            //+"+\"<===>\" + bookStorage().getAll().toArray()[0].getId()"
////            "\"some one\"" +
////               "+\"<>\"+ (book.author.equals(\"aaa\")) " +
////               "+\"<>\" + bookStorage " +
////               "+\"<>\" + bookStorage().getAll().iterator().next().getAuthor() " +
////               "+\"<>\"+ bookStorage()"
//         );
//         d.setValue(new VariablePathImpl(Arrays.asList("this", "bookStorage")), "null");
//
//         logger.info(d.dumpStackFrame());
//         logger.error(">>>>>>>>>>>>> "+d.getValue(new VariablePathImpl(Arrays.asList("this", "bookStorage", "idCounter"))));
//
//         System.err.println(d.getBreakPoints());
//         d.deleteAllBreakPoints();
//         System.err.println(d.getBreakPoints());
//      }
//      catch (Exception e2)
//      {
//         e2.printStackTrace();
//      }
//      finally
//      {
//         Thread.sleep(2000);
////         d.resume();
//         d.disconnect();
//      }
   }
}
