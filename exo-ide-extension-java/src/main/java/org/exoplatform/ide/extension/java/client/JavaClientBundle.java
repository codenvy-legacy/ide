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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Java client resources (images).
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JavaClientBundle.java Jun 21, 2011 4:26:42 PM vereshchaka $
 *
 */
public interface JavaClientBundle extends ClientBundle
{
   JavaClientBundle INSTANCE = GWT.<JavaClientBundle> create(JavaClientBundle.class);
   
   @Source("org/exoplatform/ide/extension/java/images/java-project.png")
   ImageResource javaProject();
   
   @Source("org/exoplatform/ide/extension/java/images/java-project_Disabled.png")
   ImageResource javaProjectDisabled();
   
   @Source("org/exoplatform/ide/extension/java/images/spring.png")
   ImageResource springProject();
   
   @Source("org/exoplatform/ide/extension/java/images/spring_Disabled.png")
   ImageResource springProjectDisabled();

   @Source("org/exoplatform/ide/extension/java/images/ok.png")
   ImageResource okButton();
   
   @Source("org/exoplatform/ide/extension/java/images/ok_Disabled.png")
   ImageResource okButtonDisabled();
   
   @Source("org/exoplatform/ide/extension/java/images/cancel.png")
   ImageResource cancelButton();
   
   @Source("org/exoplatform/ide/extension/java/images/cancel_Disabled.png")
   ImageResource cancelButtonDisabled();
   
   /*
    * For Project Explorer
    */
   
   @Source("org/exoplatform/ide/extension/java/images/empty-java-package.png")
   ImageResource emptyJavaPackage();

   @Source("org/exoplatform/ide/extension/java/images/jar-library.png")
   ImageResource jarLibrary();   

   @Source("org/exoplatform/ide/extension/java/images/java-class.png")
   ImageResource javaClass();

   @Source("org/exoplatform/ide/extension/java/images/java-file.png")
   ImageResource javaFile();

   @Source("org/exoplatform/ide/extension/java/images/java-package.png")
   ImageResource javaPackage();
   
   @Source("org/exoplatform/ide/extension/java/images/java-packages.png")
   ImageResource javaPackages();

   @Source("org/exoplatform/ide/extension/java/images/libraries.png")
   ImageResource libraries();

}
