/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ideall.client.framework.control;

import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class NewItemControl extends SimpleControl
{

   public NewItemControl(String id, String title, String prompt, ImageResource icon, ImageResource disabledIcon,
      GwtEvent<?> event)
   {
      super(id);

      setTitle(title);
      setPrompt(prompt);
      setNormalImage(icon);
      setDisabledImage(disabledIcon);
      setEvent(event);
   }

   public NewItemControl(String id, String title, String prompt, String icon, GwtEvent<?> event)
   {
      super(id);

      setTitle(title);
      setPrompt(prompt);
      setIcon(icon);
      setEvent(event);
   }

}
