/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.util;

import org.exoplatform.ide.client.framework.ui.IconImageBundle;

import com.google.gwt.resources.client.ImageResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Oct 24, 2011 evgen $
 *
 */
public class ProjectResolver
{

   private static Map<String, ImageResource> types = new HashMap<String, ImageResource>();

   /**
    * Ruby on Rails
    */
   public static final String RAILS = "Rails";

   /**
    * Java Spring Framework
    */
   public static final String SPRING = "Spring";

   /**
    * eXo Development Framework : Chromattic, Groovy REST 
    */
   public static final String EXO_APP = "eXo App";

   /**
    * Static Web Project: HTML, JS, CSS 
    */
   public static final String STATIC_WEB = "Static Web";

   /**
    * Servlet and JSP API  based project
    */
   public static final String SERVLET_JSP = "Servlet/JSP";

   static
   {
      if (IconImageBundle.INSTANCE != null)
      {
         types.put(RAILS, IconImageBundle.INSTANCE.rubyProject());
         types.put(SPRING, IconImageBundle.INSTANCE.springProject());
         types.put(EXO_APP, IconImageBundle.INSTANCE.defaultProject());
         types.put(STATIC_WEB, IconImageBundle.INSTANCE.defaultProject());
         types.put(SERVLET_JSP, IconImageBundle.INSTANCE.javaProject());
      }
   }

   public static Set<String> getProjectsTypes()
   {
      return types.keySet();
   }

   public static ImageResource getImageForProject(String type)
   {
      if (types.containsKey(type))
      {
         return types.get(type);
      }
      else
         return types.get(EXO_APP);
   }
}
