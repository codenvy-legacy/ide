/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.BuildNumber;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */
public class AboutForm extends DialogWindow
{

   private static final int WINDOW_WIDTH = 300;

   private static final int WINDOW_HEIGHT = 355;
   
   private static final String ID = "ideAboutForm";
   
   private static final String OK_BUTTON_ID = "ideAboutFormOkButton";
   
   private final int LOGO_WIDTH = 200;

   private final int LOGO_HEIGHT = 75;

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   private final String ABOUT = "About";

   private final String VERSION;

   private final String REVISION;

   private final String BUILD_TIME;

   private final String COPYRIGHT = "(c) ";

   private final String COMPANY_NAME = "eXo Platform SAS";

   private static final String NAME = "eXo IDE";

   private static final String YEAR = "2009-2010";

   private final String OK = "Ok";

   private IButton okButton;

   private static final String INFO = "";

   //      "This program is free software; you can redistribute it and/or"
   //                                  + "modify it under the terms of the GNU Affero General Public License"
   //                                  + "as published by the Free Software Foundation; either version 3"
   //                                  + "of the License, or (at your option) any later version.";  

   public AboutForm(HandlerManager eventBus)//, String name, String version, String year, String info, String built)
   {
      super(eventBus, WINDOW_WIDTH, WINDOW_HEIGHT, ID);

      BuildNumber buildNumber = GWT.create(BuildNumber.class);
      
      REVISION = "Revision: " + buildNumber.buildNumber();
      VERSION = "Version: " + buildNumber.version();
      BUILD_TIME = "Build Time: " + buildNumber.buildTime();
      setShowMinimizeButton(false);
      setTitle(ABOUT);

      VLayout centerLayout = new VLayout();
      centerLayout.setWidth100();
      centerLayout.setHeight100();
      centerLayout.setMargin(20);
      centerLayout.setMembersMargin(15);

      centerLayout.addMember(createLogoLayout());
      centerLayout.addMember(createInfoLayout());

      centerLayout.addMember(createButtonLayout());
      addItem(centerLayout);

      show();

      okButton.focus();
   }

   private HLayout createLogoLayout()
   {
      HLayout logoLayout = new HLayout();
      logoLayout.setWidth100();
      logoLayout.setHeight(LOGO_HEIGHT);
      logoLayout.setAlign(Alignment.CENTER);
      Img logoImage = new Img();
      logoImage.setSrc(Images.Logos.ABOUT_LOGO);
      logoImage.setHeight(LOGO_HEIGHT);
      logoImage.setWidth(LOGO_WIDTH);
      logoLayout.addMember(logoImage);
      return logoLayout;
   }

   private HLayout createButtonLayout()
   {
      okButton = new IButton(OK);
      okButton.setID(OK_BUTTON_ID);
      okButton.setIcon(Images.Buttons.OK);
      okButton.setWidth(BUTTON_WIDTH);
      okButton.setHeight(BUTTON_HEIGHT);

      okButton.addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            destroy();
         }

      });

      HLayout hLayout = new HLayout();
      hLayout.setLayoutTopMargin(20);
      hLayout.setHeight(BUTTON_HEIGHT);
      hLayout.setAutoWidth();
      hLayout.setLayoutAlign(Alignment.CENTER);
      hLayout.addMember(okButton);

      return hLayout;
   }

   private VLayout createInfoLayout()
   {
      VLayout infoLayout = new VLayout();
      infoLayout.setWidth100();
      infoLayout.setAutoHeight();

      Label infoLabel = new Label();
      infoLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
 //     infoLabel.setCanSelectText(true);
      infoLabel.getElement().setInnerHTML("<h3>" + NAME + "</h3>" + "<b>" + VERSION + "</b>" + "<br>" + YEAR + "&nbsp;" + COMPANY_NAME
         + "&nbsp;" + COPYRIGHT + "<br>" + INFO + "<br><br>" + "<b>" + REVISION + "</b>" + "<br>" + "<b>"
         + BUILD_TIME + "</b>");
      infoLayout.addMember(infoLabel);
      return infoLayout;
   }
}
