/**
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.ideall.client.component;

import org.exoplatform.gwtframework.ui.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.smartgwt.component.Label;
import org.exoplatform.ideall.client.BuildNumber;
import org.exoplatform.ideall.client.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

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

   private final int LOGO_WIDTH = 200;

   private final int LOGO_HEIGHT = 75;

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   private final String ABOUT = "About";

   private final String VERSION;

   private final String REVISION;

   private final String BUILD_TIME;

   private final String WIKI;

   private final String SRC;

   private final String COPYRIGHT = "(c) ";

   private final String COMPANY_NAME = "eXo Platform SAS";

   private static final String NAME = "IDEall";

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
      super(eventBus, WINDOW_WIDTH, WINDOW_HEIGHT);

      BuildNumber buildNumber = GWT.create(BuildNumber.class);
      
      REVISION = "Revision: " + buildNumber.buildNumber();
      VERSION = "Version: " + buildNumber.version();
      BUILD_TIME = "Build Time: " + buildNumber.buildTime();
      WIKI = buildNumber.wiki();
      SRC = buildNumber.source();
      
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
      logoLayout.setAlign(Alignment.RIGHT);
      Img logoImage = new Img();
      logoImage.setSrc(Images.Logos.ABOUT_LOGO);
      logoImage.setHeight(LOGO_HEIGHT);
      logoImage.setWidth(LOGO_WIDTH);
      logoLayout.addMember(logoImage);
      return logoLayout;
   }

   private DynamicForm createButtonLayout()
   {
      okButton = new IButton(OK);
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

      DynamicForm buttonForm = new DynamicForm();

      //buttonForm.setWidth(BUTTON_WIDTH);
      buttonForm.setMargin(10);
      buttonForm.setLayoutAlign(Alignment.CENTER);

      ToolbarItem toolbar = new ToolbarItem();
      toolbar.setWidth(BUTTON_WIDTH);
      toolbar.setButtons(okButton);
      buttonForm.setFields(toolbar);
      buttonForm.setAutoWidth();

      return buttonForm;
   }

   private VLayout createInfoLayout()
   {
      VLayout infoLayout = new VLayout();
      infoLayout.setWidth100();
      infoLayout.setAutoHeight();

      Label infoLabel = new Label();
      infoLabel.setAlign(Alignment.LEFT);
      infoLabel.setCanSelectText(true);
      infoLabel.setValue("<h3>" + NAME + "</h3>" + "<b>" + VERSION + "</b>" + "<br>" + YEAR + "&nbsp;" + COMPANY_NAME
         + "&nbsp;" + COPYRIGHT + "<br>" + INFO + "<br><br>" + "<b>" + "<a href=\"" + WIKI
         + " \" target=\"_blank\" >Wiki</a>" + "<br>" + "<b>"   + "<a href=\"" + SRC
         + " \" target=\"_blank\">Code Source</a>" + "<br>" + "</b>" + "<b>" + REVISION + "</b>" + "<br>" + "<b>"
         + BUILD_TIME + "</b>");
      infoLayout.addMember(infoLabel);
      return infoLayout;
   }
}
