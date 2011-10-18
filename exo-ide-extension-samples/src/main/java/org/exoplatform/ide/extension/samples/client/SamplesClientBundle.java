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
package org.exoplatform.ide.extension.samples.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Samples client resources (images).
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesClientBundle.java Sep 2, 2011 12:33:49 PM vereshchaka $
 *
 */
public interface SamplesClientBundle extends ClientBundle
{
   SamplesClientBundle INSTANCE = GWT.<SamplesClientBundle> create(SamplesClientBundle.class);
   
   /**
    * Css resources for project wizard.
    */
   @Source("org/exoplatform/ide/extension/samples/client/wizard.css")
   Style css();
   
   /*
    * Buttons
    */
   @Source("org/exoplatform/ide/extension/samples/images/buttons/ok.png")
   ImageResource ok();

   @Source("org/exoplatform/ide/extension/samples/images/buttons/ok_Disabled.png")
   ImageResource okDisabled();
   
   @Source("org/exoplatform/ide/extension/samples/images/buttons/add.png")
   ImageResource add();

   @Source("org/exoplatform/ide/extension/samples/images/buttons/add_Disabled.png")
   ImageResource addDisabled();
   
   @Source("org/exoplatform/ide/extension/samples/images/buttons/cancel.png")
   ImageResource cancel();

   @Source("org/exoplatform/ide/extension/samples/images/buttons/cancel_Disabled.png")
   ImageResource cancelDisabled();
   
   @Source("org/exoplatform/ide/extension/samples/images/buttons/next.png")
   ImageResource next();

   @Source("org/exoplatform/ide/extension/samples/images/buttons/next_Disabled.png")
   ImageResource nextDisabled();
   
   @Source("org/exoplatform/ide/extension/samples/images/buttons/back.png")
   ImageResource back();

   @Source("org/exoplatform/ide/extension/samples/images/buttons/back_Disabled.png")
   ImageResource backDisabled();
   
   /*
    * Start page images
    */
   @Source("org/exoplatform/ide/extension/samples/images/tutorial.png")
   ImageResource welcomeTutorial();
   
   @Source("org/exoplatform/ide/extension/samples/images/samples.png")
   ImageResource welcomeSamples();
   
   @Source("org/exoplatform/ide/extension/samples/images/eXo-IDE-Logo.png")
   ImageResource ideLogo();
   
   @Source("org/exoplatform/ide/extension/samples/images/project.png")
   ImageResource welcomeProject();
   
   public interface Style extends CssResource
   {
      String table();
      
      String itemsTree();
      
      String labelTitle();
      
      String labelDescription();
      
      String wizard();
      
      String topBox();
      
      String footer();
      
      String labelValue();
      
      String labelSubtitle();
      
      String cloudfoundryTable();
      
      String cloudbeesTable();
      
      String visibleTable();
      
      String hiddenTable();
      
      String loginLable();
      
      String bottomBox();

      String leftFloat();
      
      String innerBox();

      String right();

      String middle();

      String newFolderDivInput();

      String label();
   }

}
