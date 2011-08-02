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
package org.exoplatform.ide.extension.java.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JavaLocalizationConstant.java Jun 21, 2011 4:31:30 PM vereshchaka $
 *
 */
public interface JavaLocalizationConstant extends Messages
{
   /*
    * Buttons
    */
   @Key("java.button.create")
   String createButton();

   @Key("java.button.cancel")
   String cancelButton();

   /*
    * Controls.
    */
   @Key("control.create.javaProject.id")
   String createJavaProjectControlId();

   @Key("control.create.javaProject.title")
   String createJavaProjectControlTitle();

   @Key("control.create.javaProject.prompt")
   String createJavaProjectControlPrompt();
   
   @Key("control.create.javaSpringProject.id")
   String createJavaSpringProjectControlId();
   
   @Key("control.create.javaSpringProject.title")
   String createJavaSpringProjectControlTitle();

   @Key("control.create.javaSpringProject.prompt")
   String createJavaSpringProjectControlPrompt();

   /*
    * CreateApplicationView
    */
   @Key("javaProject.view.title")
   String createJavaProjectViewTitle();

   @Key("javaProject.view.nameField.title")
   String createJavaProjectNameFieldTitle();

   @Key("javaProject.view.nameField.defaultName")
   String createJavaProjectDefaultName();

   /*
    * Messages
    */
   @Key("javaProject.create.success")
   String createJavaProjectSuccess(String projectName);
   
   @Key("javaProject.clean.success")
   String cleanJavaProjectSuccess(String project);
}
