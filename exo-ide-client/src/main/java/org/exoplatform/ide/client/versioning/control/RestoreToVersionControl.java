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
package org.exoplatform.ide.client.versioning.control;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.versioning.event.RestoreToVersionEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 29, 2010 $
 *
 */
@RolesAllowed({"administrators", "developers"})
public class RestoreToVersionControl extends VersionControl implements ShowVersionContentHandler
{

   private static final String ID = "File/Restore to Version";

   private final String TITLE = "Restore To Version";

   private final String PROMPT = "Restore To Version";

   private Version version;

   /**
    * @param id
    * @param eventBus
    */
   public RestoreToVersionControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setEvent(new RestoreToVersionEvent());
      setImages(IDEImageBundle.INSTANCE.restoreVersion(), IDEImageBundle.INSTANCE.restoreVersionDisabled());
   }
   
   /**
    * @see org.exoplatform.ide.client.versioning.control.VersionControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ShowVersionContentEvent.TYPE, this);
      super.initialize(eventBus);
   }

   /**
    * @see org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler#onShowVersionContent(org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent)
    */
   public void onShowVersionContent(ShowVersionContentEvent event)
   {
      version = event.getVersion();
      boolean isEnabled =
         (version.getProperty(ItemProperty.SUCCESSOR_SET) != null && version.getProperty(ItemProperty.SUCCESSOR_SET)
            .getChildProperties().size() > 0);
      setEnabled(isEnabled);
   }

}
