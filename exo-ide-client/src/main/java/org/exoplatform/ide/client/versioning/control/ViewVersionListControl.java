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
import org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.versioning.VersionContentPresenter;
import org.exoplatform.ide.client.versioning.event.ShowVersionListEvent;

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
      setEvent(new ShowVersionListEvent());
      setImages(IDEImageBundle.INSTANCE.viewVersions(), IDEImageBundle.INSTANCE.viewVersionsDisabled());
   }
   
   /**
    * @see org.exoplatform.ide.client.versioning.control.VersionControl#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      if (event.getView() instanceof VersionContentPresenter.Display)
      {
         setVisible(true);
         setEnabled(true);
      }
      else
      {
         setVisible(false);
      }
   }
}
