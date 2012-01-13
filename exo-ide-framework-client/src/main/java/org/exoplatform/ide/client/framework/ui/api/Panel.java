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
package org.exoplatform.ide.client.framework.ui.api;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;
import java.util.Map;

/**
 * This interface describes a visual component that can display views in tabs or in any other form.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Panel extends HasViews, IsWidget
{

   /**
    * Returns the ID of this panel.
    * 
    * @return id of this panel
    */
   String getPanelId();

   /**
    * Adds a view to the panel.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.HasViews#addView(org.exoplatform.ide.client.framework.ui.api.View)
    */
   void addView(View view);

   /**
    * Removes view from the panel.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.HasViews#removeView(org.exoplatform.ide.client.framework.ui.api.View)
    */
   boolean removeView(View view);

   Map<String, View> getViews();

   List<String> getAcceptedTypes();

   void acceptType(String viewType);

   void setPanelHidden(boolean panelHidden);

   boolean isPanelHidden();

}
