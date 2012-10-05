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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.restart;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: RestartAppServerView.java Sep 28, 2012 4:34:01 PM azatsarynnyy $
 *
 */
public class RestartAppServerView extends ViewImpl implements RestartAppServerPresenter.Display
{
   private static final String ID = "ideRestartAppServerView";

   private static final int WIDTH = 460;

   private static final int HEIGHT = 170;

   private static final String RESTART_BUTTON_ID = "ideRestartAppServerViewRestartButton";

   private static final String CANCEL_BUTTON_ID = "ideRestartAppServerViewCancelButton";

   @UiField
   Label questionLabel;

   @UiField
   ImageButton restartButton;

   @UiField
   ImageButton cancelButton;

   private static StopEnvironmentViewUiBinder uiBinder = GWT.create(StopEnvironmentViewUiBinder.class);

   interface StopEnvironmentViewUiBinder extends UiBinder<Widget, RestartAppServerView>
   {
   }

   public RestartAppServerView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.restartAppServerViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      questionLabel.setIsHTML(true);
      restartButton.setButtonId(RESTART_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.restart.RestartAppServerPresenter.Display#getRestartButton()
    */
   @Override
   public HasClickHandlers getRestartButton()
   {
      return restartButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete.DeleteVersionPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.restart.RestartAppServerPresenter.Display#getRestartQuestion()
    */
   @Override
   public HasValue<String> getRestartQuestion()
   {
      return questionLabel;
   }

}
