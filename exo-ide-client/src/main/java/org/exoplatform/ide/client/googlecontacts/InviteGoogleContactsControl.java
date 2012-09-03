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
package org.exoplatform.ide.client.googlecontacts;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;

/**
 * Control for inviting user's Google Contacts.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: InviteGoogleContactsControl.java Aug 20, 2012 5:00:18 PM azatsarynnyy $
 *
 */
@RolesAllowed({"administrators", "developers"})
public class InviteGoogleContactsControl extends SimpleControl implements IDEControl
{
   /**
    * Control's title.
    */
   public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.inviteGoogleContactsControlTitle();

   /**
    * Control's identifier.
    */
   public static final String ID = "Help/"+TITLE;

   /**
    * Constructs new control.
    */
   public InviteGoogleContactsControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setEnabled(true);
      setVisible(true);
      setImages(IDEImageBundle.INSTANCE.google(), IDEImageBundle.INSTANCE.googleDisabled());
      setEvent(new InviteGoogleContactsEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
   }
}
