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

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.client.framework.ui.IconImageBundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 24, 2011 evgen $
 * 
 */
public class ProjectResolver
{

   private static Map<String, ImageResource> types = new HashMap<String, ImageResource>();

   /**
    * List contains sorted project types.
    */
   private static List<String> sortedTypes = new ArrayList<String>();

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
   public static final String EXO_APP = "eXo";

   /**
    * Static Web Project: HTML, JS, CSS
    */
   public static final String STATIC_WEB = "Javascript";

   /**
    * Servlet and JSP API based project
    */
<<<<<<< HEAD
   public static final String SERVLET_JSP = "Java Web";

   /**
    * PHP Project
    */
   public static final String PHP = "PHP";

   /**
    * Empty Project
    */
   public static final String UNDEFINED = "Undefined";

   public static final String SERVLET_JSP = "Servlet/JSP";
   
   public static final String APP_ENGINE_JAVA = "App Engine Java";
   
   public static final String APP_ENGINE_PYTHON = "App Engine Python";

   static
   {
      if (IconImageBundle.INSTANCE != null)
      {
         types.put(RAILS, IconImageBundle.INSTANCE.rubyProject());
         types.put(SPRING, IconImageBundle.INSTANCE.springProject());
         types.put(EXO_APP, IconImageBundle.INSTANCE.exoProject());
         types.put(STATIC_WEB, IconImageBundle.INSTANCE.jsProject());
         types.put(SERVLET_JSP, IconImageBundle.INSTANCE.javaProject());
         types.put(PHP, IconImageBundle.INSTANCE.phpProject());
         types.put(UNDEFINED, IconImageBundle.INSTANCE.defaultProject());
         types.put(APP_ENGINE_JAVA, IconImageBundle.INSTANCE.javaProject());
      }

      sortedTypes.add(UNDEFINED);
      sortedTypes.add(EXO_APP);
      sortedTypes.add(STATIC_WEB);
      sortedTypes.add(SERVLET_JSP);
      sortedTypes.add(PHP);
      sortedTypes.add(RAILS);
      sortedTypes.add(SPRING);
      sortedTypes.add(APP_ENGINE_JAVA);
   }

   public static Set<String> getProjectsTypes()
   {
      return types.keySet();
   }

   /**
    * Returns index of project type.
    * 
    * @param type project type
    * @return index of project type
    */
   public static int getIndexOfProjectType(String type)
   {
      if (sortedTypes.contains(type))
      {
         return sortedTypes.indexOf(type);
      }
      else
      {
         return -1;
      }
   }

   public static ImageResource getImageForProject(String type)
   {
      if (types.containsKey(type))
      {
         return types.get(type);
      }
      else
         return types.get(UNDEFINED);
   }
}
