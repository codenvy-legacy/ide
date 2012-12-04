/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.wizard.genericproject;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Contains of resource for generic project's page view.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 *
 */
public interface GenericProjectWizardResource extends ClientBundle
{
   public interface GenericProjectWizardCss extends CssResource
   {
      @ClassName("ide-GenericProjectWizard")
      String genericProjectWizard();

      @ClassName("component")
      String component();
   }

   @Source("org/exoplatform/ide/wizard/GenericProjectWizard.css")
   GenericProjectWizardCss genericProjectWizardCss();

   @Source("org/exoplatform/ide/wizard/images/GenericProjectIcon.png")
   ImageResource genericProjectIcon();
}