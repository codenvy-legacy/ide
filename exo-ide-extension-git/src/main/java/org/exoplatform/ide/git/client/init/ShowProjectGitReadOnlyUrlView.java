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
package org.exoplatform.ide.git.client.init;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * UI for initializing the repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 24, 2011 10:35:37 AM anya $
 *
 */
public class ShowProjectGitReadOnlyUrlView extends ViewImpl implements ShowProjectGitReadOnlyUrlPresenter.Display
{

   public static final String ID = "ideGitUrlView";

   /*Elements IDs*/

   private static final String OK_BUTTON_ID = "ideGitUrlOkButton";
   
   private static final String GIT_URL_FIELD_ID = "ideGitUrlField";

   @UiField
   TextInput gitUrl;
   
   @UiField
   ImageButton closeButton;

   interface ShowGitUrlViewUiBinder extends UiBinder<Widget, ShowProjectGitReadOnlyUrlView>
   {
   }

   private static ShowGitUrlViewUiBinder uiBinder = GWT.create(ShowGitUrlViewUiBinder.class);

   public ShowProjectGitReadOnlyUrlView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.projectReadOnlyGitUrlPrompt(), null, 475, 125, false);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));

      
      gitUrl.setName(GIT_URL_FIELD_ID);
      closeButton.setButtonId(OK_BUTTON_ID);
      
   }

   @Override
   public HasValue<String> getGitUrl()
   {
      return gitUrl;
   }

   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

  
}
