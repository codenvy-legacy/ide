/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide;

import com.thoughtworks.selenium.DefaultSelenium;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CapturingSelenium extends DefaultSelenium implements MethodRule
{
   public CapturingSelenium(String serverHost, int serverPort, String browserStartCommand, String browserURL)
   {
      super(serverHost, serverPort, browserStartCommand, browserURL);
   }

   public Statement apply(final Statement base, FrameworkMethod method, Object target) 
   {
      final String name = method.getMethod().getDeclaringClass().getName() + "." + method.getName();
      
       return new Statement() {

           @Override
           public void evaluate() throws Throwable {
               try {
                   base.evaluate();
               }
               catch (Exception e) {
                  captureScreenshot("tmp/screenshot/" + name + ".png");
                   throw new RuntimeException(e);
               }
           }
       };
   }


}
