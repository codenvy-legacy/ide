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
package org.exoplatform.ide.extension.java.client.control;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.java.client.JavaClientBundle;
import org.exoplatform.ide.extension.java.client.JavaExtension;
import org.exoplatform.ide.extension.java.client.create.CreateJavaProjectEvent;
import org.exoplatform.ide.extension.java.shared.ProjectType;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Control for creating new java project.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateJavaProject.java Jun 21, 2011 4:13:09 PM vereshchaka $
 *
 */
public class CreateJavaProjectControl extends SimpleControl implements IDEControl
{

   private static Map<ProjectType, String> ids = new HashMap<ProjectType, String>();

   private static Map<ProjectType, String> titles = new HashMap<ProjectType, String>();

   private static Map<ProjectType, String> prompts = new HashMap<ProjectType, String>();

   static
   {
      ids.put(ProjectType.WEB, JavaExtension.LOCALIZATION_CONSTANT.createJavaProjectControlId());
      titles.put(ProjectType.WEB, JavaExtension.LOCALIZATION_CONSTANT.createJavaProjectControlTitle());
      prompts.put(ProjectType.WEB, JavaExtension.LOCALIZATION_CONSTANT.createJavaProjectControlPrompt());

      ids.put(ProjectType.SPRING, JavaExtension.LOCALIZATION_CONSTANT.createJavaSpringProjectControlId());
      titles.put(ProjectType.SPRING, JavaExtension.LOCALIZATION_CONSTANT.createJavaSpringProjectControlTitle());
      prompts.put(ProjectType.SPRING, JavaExtension.LOCALIZATION_CONSTANT.createJavaSpringProjectControlPrompt());
   }

   public CreateJavaProjectControl(ProjectType projectType)
   {
      super(ids.get(projectType));
      setTitle(titles.get(projectType));
      setPrompt(prompts.get(projectType));

      setEvent(new CreateJavaProjectEvent(projectType));

      if (projectType == ProjectType.SPRING)
      {
         setImages(JavaClientBundle.INSTANCE.springProject(), JavaClientBundle.INSTANCE.springProjectDisabled());
      }
      else
      {
         setImages(JavaClientBundle.INSTANCE.javaProject(), JavaClientBundle.INSTANCE.javaProjectDisabled());
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      setVisible(true);
      setEnabled(true);
   }

}
