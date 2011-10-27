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
package org.exoplatform.ide.extension.groovy.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="dnochevnov@gmail.com">Dmytro Nochevnov</a>
 * @version $Id:
 */
public interface GroovyLocalizationConstant extends Messages
{
   
   /*
    * Views
    */
   @Key("view.title.chooseSourcePath")
   String chooseSourcePathViewTitle();
   
   @Key("view.title.configureBuildPath")
   String configureBuildPathTitle();
   
   /*
    * Buttons
    */
   @Key("button.ok")
   String okButton();
   
   @Key("button.cancel")
   String cancelButton();

   @Key("button.add")
   String addButton();

   @Key("button.remove")
   String removeButton();

   @Key("button.save")
   String saveButton();
   
   /*
    * Controls
    */
   
   
   @Key("groovy.classpath.file.create.error")
   String classpathCreationError();
}
