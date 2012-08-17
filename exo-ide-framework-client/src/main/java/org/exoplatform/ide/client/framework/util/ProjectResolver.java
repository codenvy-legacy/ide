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

import org.exoplatform.ide.client.framework.project.Language;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.ui.IconImageBundle;

import java.util.ArrayList;
import java.util.Arrays;
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

   private static Map<Language, List<ProjectType>> projectTypes = new HashMap<Language, List<ProjectType>>();

   private static Map<ProjectType, ImageResource> projectImages = new HashMap<ProjectType, ImageResource>();

   private static Map<Language, ImageResource> languageImages = new HashMap<Language, ImageResource>();

   /**
    * List contains sorted project types.
    */
   private static List<String> sortedTypes = new ArrayList<String>();

   /**
    * Ruby on Rails
    */
   @Deprecated
   public static final String RAILS = "Rails";

   /**
    * Java Spring Framework
    */
   @Deprecated
   public static final String SPRING = "Spring";

   /**
    * eXo Development Framework : Chromattic, Groovy REST
    */
   @Deprecated
   public static final String EXO_APP = "eXo";

   /**
    * Static Web Project: HTML, JS, CSS
    */
   @Deprecated
   public static final String STATIC_WEB = "Javascript";

   /**
    * Servlet and JSP API based project
    */
   @Deprecated
   public static final String SERVLET_JSP = "Java Web";

   /**
    * PHP Project
    */
   @Deprecated
   public static final String PHP = "PHP";

   /**
    * Empty Project
    */
   public static final String UNDEFINED = "Undefined";

   @Deprecated
   public static final String APP_ENGINE_JAVA = "App Engine Java";

   @Deprecated
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
         types.put(APP_ENGINE_JAVA, IconImageBundle.INSTANCE.gaeJavaProject());
         types.put(APP_ENGINE_PYTHON, IconImageBundle.INSTANCE.gaePythonProject());

         projectImages.put(ProjectType.JAVA, IconImageBundle.INSTANCE.javaProject());
         projectImages.put(ProjectType.SPRING, IconImageBundle.INSTANCE.springProject());
         projectImages.put(ProjectType.JSP, IconImageBundle.INSTANCE.jspProject());
         projectImages.put(ProjectType.EXO, IconImageBundle.INSTANCE.exoProject());
         projectImages.put(ProjectType.JAVASCRIPT, IconImageBundle.INSTANCE.jsProject());
         projectImages.put(ProjectType.PHP, IconImageBundle.INSTANCE.phpProject());
         projectImages.put(ProjectType.RUBY, IconImageBundle.INSTANCE.rubyProject());
         projectImages.put(ProjectType.RUBY_ON_RAILS, IconImageBundle.INSTANCE.rubyProject());
         projectImages.put(ProjectType.GAE_JAVA, IconImageBundle.INSTANCE.gaeJavaProject());
         projectImages.put(ProjectType.GAE_PYTHON, IconImageBundle.INSTANCE.gaePythonProject());
      }

      projectTypes.put(Language.JAVA,
         Arrays.asList(ProjectType.GAE_JAVA, ProjectType.JAVA, ProjectType.JSP, ProjectType.SPRING));
      projectTypes.put(Language.GROOVY, Arrays.asList(ProjectType.EXO));
      projectTypes.put(Language.JAVASCRIPT, Arrays.asList(ProjectType.JAVASCRIPT));
      projectTypes.put(Language.PHP, Arrays.asList(ProjectType.PHP));
      projectTypes.put(Language.NODE_JS, Arrays.asList(ProjectType.NODE_JS));
      projectTypes.put(Language.PYTHON, Arrays.asList(ProjectType.GAE_PYTHON));
      projectTypes.put(Language.RUBY, Arrays.asList(ProjectType.RUBY, ProjectType.RUBY_ON_RAILS));

      languageImages.put(Language.JAVA, IconImageBundle.INSTANCE.javaType());
      languageImages.put(Language.PHP, IconImageBundle.INSTANCE.phpType());
      languageImages.put(Language.RUBY, IconImageBundle.INSTANCE.rubyType());
      languageImages.put(Language.PYTHON, IconImageBundle.INSTANCE.pythonType());
      languageImages.put(Language.GROOVY, IconImageBundle.INSTANCE.groovyType());
      languageImages.put(Language.JAVASCRIPT, IconImageBundle.INSTANCE.jsType());

      sortedTypes.add(UNDEFINED);
      sortedTypes.add(EXO_APP);
      sortedTypes.add(STATIC_WEB);
      sortedTypes.add(SERVLET_JSP);
      sortedTypes.add(PHP);
      sortedTypes.add(RAILS);
      sortedTypes.add(SPRING);
      sortedTypes.add(APP_ENGINE_JAVA);
      sortedTypes.add(APP_ENGINE_PYTHON);
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

   /**
    * @param type
    * @return
    * @use {@link #getImageForProject(ProjectType)}
    */
   @Deprecated
   public static ImageResource getImageForProject(String type)
   {
      if (types.containsKey(type))
      {
         return types.get(type);
      }
      else
         return types.get(UNDEFINED);
   }

   public static ImageResource getImageForProject(ProjectType type)
   {
      if (projectImages.containsKey(type))
      {
         return projectImages.get(type);
      }
      else
         return types.get(UNDEFINED);
   }

   public static ImageResource getImageForLanguage(Language language)
   {
      if (languageImages.containsKey(language))
      {
         return languageImages.get(language);
      }
      else
         return null;
   }

   public static List<ProjectType> getProjectTypesByLanguage(Language language)
   {
      return projectTypes.get(language);
   }

}
