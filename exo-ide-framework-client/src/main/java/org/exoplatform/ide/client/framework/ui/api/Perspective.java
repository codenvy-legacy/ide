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

import java.util.Map;

import org.exoplatform.ide.client.framework.ui.api.event.HasClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewVisibilityChangedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Perspective extends HasViewVisibilityChangedHandler, 
HasViewOpenedHandler, HasViewClosedHandler, HasClosingViewHandler, IsWidget
{

   /**
    * Opens View.
    * 
    * @param view
    */
   void openView(View view);

   /**
    * Closes View.
    * 
    * @param viewId
    */
   void closeView(String viewId);
   
   /**
    * Returns map of opened views.
    * 
    * @return
    */
   public Map<String, View> getViews();

   /**
    * Returns map of opened panels.
    * 
    * @return
    */
   public Map<String, Panel> getPanels();   

}
