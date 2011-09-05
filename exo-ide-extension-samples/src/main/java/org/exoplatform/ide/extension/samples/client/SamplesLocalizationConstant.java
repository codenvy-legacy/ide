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

import com.google.gwt.i18n.client.Constants;

/**
 * Interface to represent the constants contained in resource bundle:
 *      'IdeSamplesLocalizationConstant.properties'.
 * <p/>
 * Localization message for forms from start page view.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdeSamplesLocalizationConstant.java Aug 25, 2011 5:57:11 PM vereshchaka $
 *
 */
public interface SamplesLocalizationConstant extends Constants
{
   /*
    * Buttons
    */
   @Key("button.import")
   String importButton();
   
   @Key("button.cancel")
   String cancelButton();
   
   @Key("button.create")
   String createButton();
   
   /*
    * WelcomeView
    */
   @Key("welcome.aboutIde")
   String aboutIde();
   
   @Key("welcome.tutorial.title")
   String tutorialTitle();
   
   @Key("welcome.tutorial.text")
   String tutorialText();
   
   @Key("welcome.sample.title")
   String sampleTitle();
   
   @Key("welcome.sample.text")
   String sampleText();

}
