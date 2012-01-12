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
package org.exoplatform.ide.client.framework.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */
@RolesAllowed({"administrators", "developers"})
public class NewItemControl extends SimpleControl implements IDEControl
{

   private String mimeType;

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

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   public void initialize()
   {
   }

   public NewItemControl(String id, String title, String prompt, String icon, GwtEvent<?> event)
   {
      super(id);

      setTitle(title);
      setPrompt(prompt);
      setIcon(icon);
      setEvent(event);
   }

   public NewItemControl(String id, String title, String prompt, String icon, String mimeType)
   {
      super(id);

      setTitle(title);
      setPrompt(prompt);
      setIcon(icon);
      this.mimeType = mimeType;
   }

   public NewItemControl(String id, String title, String prompt, String icon, String mimeType,
      boolean hasDelimiterBefore)
   {
      this(id, title, prompt, icon, mimeType);
      setDelimiterBefore(hasDelimiterBefore);
   }

   public NewItemControl(String id, String title, String prompt, ImageResource icon, ImageResource disabledIcon,
      String mimeType)
   {
      super(id);

      setTitle(title);
      setPrompt(prompt);
      setNormalImage(icon);
      setDisabledImage(disabledIcon);
      this.mimeType = mimeType;
   }

   public String getMimeType()
   {
      return mimeType;
   }

   public void setMimeType(String mimeType)
   {
      this.mimeType = mimeType;
   }
}
