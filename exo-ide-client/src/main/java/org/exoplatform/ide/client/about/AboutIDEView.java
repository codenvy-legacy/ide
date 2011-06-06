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
package org.exoplatform.ide.client.about;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.BuildNumber;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */
public class AboutIDEView extends ViewImpl implements org.exoplatform.ide.client.about.AboutIDEPresenter.Display
{
   
   private static final String ID = "ideAboutView";

   private static final int WIDTH = 280;

   private static final int HEIGHT = 345;

   private static final String OK_BUTTON_ID = "ideAboutViewOkButton";

   private final int LOGO_WIDTH = 200;

   private final int LOGO_HEIGHT = 75;

   private final String VERSION;

   private final String REVISION;

   private final String BUILD_TIME;

   private final String COPYRIGHT = IDE.PREFERENCES_CONSTANT.aboutCopyright();

   private final String COMPANY_NAME = IDE.PREFERENCES_CONSTANT.aboutCompanyName();

   private static final String NAME = IDE.PREFERENCES_CONSTANT.aboutIdeName();

   private static final String YEAR = IDE.PREFERENCES_CONSTANT.aboutYear();
   
   private static final String TITLE = IDE.PREFERENCES_CONSTANT.aboutTitle();

   private ImageButton okButton;

   public AboutIDEView()
   {
      super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.about()), WIDTH, HEIGHT, false);
      BuildNumber buildNumber = GWT.create(BuildNumber.class);
      REVISION = IDE.PREFERENCES_CONSTANT.aboutRevision() + buildNumber.buildNumber();
      VERSION = IDE.PREFERENCES_CONSTANT.aboutVersion() + buildNumber.version();
      BUILD_TIME = IDE.PREFERENCES_CONSTANT.aboutBuildTime() + buildNumber.buildTime();

      VerticalPanel centerLayout = new VerticalPanel();
      centerLayout.setWidth("100%");
      centerLayout.setHeight("100%");
      centerLayout.setSpacing(15);
      centerLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      centerLayout.add(createLogoLayout());
      centerLayout.add(createInfoLayout());

      centerLayout.add(createButtonLayout());
      add(centerLayout);

      okButton.focus();
   }

   private HorizontalPanel createLogoLayout()
   {
      HorizontalPanel logoLayout = new HorizontalPanel();
      logoLayout.setWidth("100%");
      logoLayout.setHeight(LOGO_HEIGHT + "px");
      Image logoImage = new Image();
      logoImage.setUrl(Images.Logos.ABOUT_LOGO);
      logoImage.setHeight(LOGO_HEIGHT + "px");
      logoImage.setWidth(LOGO_WIDTH + "px");
      logoLayout.add(logoImage);
      return logoLayout;
   }

   private HorizontalPanel createButtonLayout()
   {
      okButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.okButton(), "ok");
      okButton.setId(OK_BUTTON_ID);

      HorizontalPanel hLayout = new HorizontalPanel();
      hLayout.setSpacing(10);
      hLayout.setHeight("22px");
      hLayout.add(okButton);
      return hLayout;
   }

   private VerticalPanel createInfoLayout()
   {
      VerticalPanel infoLayout = new VerticalPanel();
      infoLayout.setWidth("100%");
      infoLayout.setHeight("100%");

      Label infoLabel = new Label();
      infoLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      //     infoLabel.setCanSelectText(true);
      infoLabel.getElement().setInnerHTML(
         "<h3>" + NAME + "</h3>" + "<b>" + VERSION + "</b>" + "<br>" + YEAR + "&nbsp;" + COMPANY_NAME + "&nbsp;"
            + COPYRIGHT + "<br><br>" + "<b>" + REVISION + "</b>" + "<br>" + "<b>" + BUILD_TIME + "</b>");
      infoLayout.add(infoLabel);
      return infoLayout;
   }

   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

}
