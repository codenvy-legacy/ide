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
package org.exoplatform.ide.client.module.navigation.control.versioning;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewVersionListEvent;
import org.exoplatform.ide.client.panel.event.PanelOpenedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.versioning.VersionContentForm;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 12, 2010 $
 *
 */
@RolesAllowed({"administrators", "developers"})
public class ViewVersionListControl extends VersionControl
{

   private static final String ID = "View/Version...";

   private final String TITLE = "Version...";

   private final String PROMPT = "View Item Version...";

   /**
    * @param id
    * @param eventBus
    */
   public ViewVersionListControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setEvent(new ViewVersionListEvent());
      setImages(IDEImageBundle.INSTANCE.viewVersions(), IDEImageBundle.INSTANCE.viewVersionsDisabled());
   }
   
   public void onPanelSelected(PanelSelectedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getPanelId()))
      {
         setVisible(true);
         setEnabled(true);
      }
   }

   public void onPanelOpened(PanelOpenedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getPanelId()))
      {
         setVisible(true);
         setEnabled(true);
      }
   }
}
