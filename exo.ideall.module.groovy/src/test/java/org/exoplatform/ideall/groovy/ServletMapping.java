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

import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ServletMapping
{

   private static final String TEST_SERVLET_MAPPING = "/test_servlet";
   
   public static final String VALIDATE_SUCCESSFULL = "validate_groovy_successfull";
   
   public static final String VALIDATE_FAILURE = "validate_groovy_failure";
   
   public static final String DEPLOY_SUCCESSFULL = "deploy_groovy_successfull";
   
   public static final String DEPLOY_FAILURE = "deploy_groovy_failure";
   
   public static final String UNDEPLOY_SUCCESSFULL = "undeploy_groovy_successfull";
   
   public static final String UNDEPLOY_FAILURE = "undeploy_groovy_failure";
   
   public static final String GETOUTPUT_SUCCESSFULL = "get_output_successfull";
   
   public static final String GETOUTPUT_CUSTOM_STATUS = "get_output_custom_status";
   
   public static String getURLFor(String mapping) {
      return "http://" + Window.Location.getHost() + TEST_SERVLET_MAPPING + "/" + mapping;
   }

}
