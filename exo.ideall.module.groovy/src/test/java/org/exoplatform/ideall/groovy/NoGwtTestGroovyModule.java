package org.exoplatform.ideall.groovy;

import com.google.gwt.event.shared.HandlerManager;

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

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class NoGwtTestGroovyModule extends Test
{
   
   public void testGroovyModule() {
      System.out.println("alalal!");
      
      System.out.println("GwtTestGroovyModule.testGroovyModule()");
      
      HandlerManager eventBus = new HandlerManager(null);
      
      String url = ServletMapping.getURLFor(ServletMapping.VALIDATE_SUCCESSFULL);
      
      System.out.println("try connect to: " + url);
      
      long start = System.currentTimeMillis();
      
      GroovyService service = new GroovyServiceImpl(eventBus, url, null);
      service.deploy("/dev-monit/");
      
      long time = System.currentTimeMillis() - start;
      System.out.println();
   }

}
