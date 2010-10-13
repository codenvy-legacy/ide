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

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewPreviousVersionEvent;
import org.exoplatform.ide.client.module.vfs.api.Version;
import org.exoplatform.ide.client.module.vfs.property.ItemProperty;
import org.exoplatform.ide.client.versioning.event.ShowVersionEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionHandler;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 29, 2010 $
 *
 */
public class ViewPreviousVersionControl extends VersionControl implements ShowVersionHandler
{
   private static final String ID = "View/Older Version";

   private final String TITLE = "Older Version";

   private final String PROMPT = "View Older Version";

   private Version version;

   /**
    * @param id
    * @param eventBus
    */
   public ViewPreviousVersionControl(HandlerManager eventBus)
   {
      super(ID, eventBus);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setEvent(new ViewPreviousVersionEvent());
      setImages(IDEImageBundle.INSTANCE.viewOlderVersion(), IDEImageBundle.INSTANCE.viewOlderVersionDisabled());

      eventBus.addHandler(ShowVersionEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.versioning.event.ShowVersionHandler#onShowVersion(org.exoplatform.ide.client.versioning.event.ShowVersionEvent)
    */
   public void onShowVersion(ShowVersionEvent event)
   {
      version = event.getVersion();
      boolean isEnabled =
         (version.getProperty(ItemProperty.PREDECESSOR_SET) != null && version
            .getProperty(ItemProperty.PREDECESSOR_SET).getChildProperties().size() > 0);
      setEnabled(isEnabled);
   }
}
