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
package org.exoplatform.ide.extension.samples.client.inviting;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class InviteDevelopersView extends ViewImpl implements org.exoplatform.ide.extension.samples.client.inviting.InviteDevelopersPresenter.Display
{
   
   private static final String ID = "ide.inviteDevelopersView";
   
   private static final String TITLE = "Invite Developers";
   
   private static final int WIDTH = 700;
   
   private static final int HEIGHT = 500;

   private static InviteDevelopersViewUiBinder uiBinder = GWT.create(InviteDevelopersViewUiBinder.class);

   interface InviteDevelopersViewUiBinder extends UiBinder<Widget, InviteDevelopersView>
   {
   }

   @UiField
   ImageButton useGoogleButton;
   
   @UiField
   ImageButton useGitHubButton;
   
   @UiField
   ImageButton inviteButton;
   
   @UiField
   ImageButton closeButton;

   public InviteDevelopersView()
   {
      super(ID, "modal", TITLE, new Image(SamplesClientBundle.INSTANCE.welcome()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      setCloseOnEscape(true);
   }

   @Override
   public HasClickHandlers getUseGoogleButton()
   {
      return useGoogleButton;
   }

   @Override
   public HasClickHandlers getUseGitHubButton()
   {
      return useGitHubButton;
   }

   @Override
   public HasClickHandlers getInviteButton()
   {
      return inviteButton;
   }

   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

}
