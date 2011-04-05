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
package org.exoplatform.ide.client.application.ui;

import org.exoplatform.ide.client.ui.api.Panel;
import org.exoplatform.ide.client.ui.impl.panel.PanelDirection;
import org.exoplatform.ide.client.ui.impl.perspective.BasePerspective;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DefaultPerspective extends BasePerspective
{

   @Override
   public void buildPerspective()
   {
      Panel navigationPanel = addPanel("navigation", PanelDirection.WEST, 300);
      navigationPanel.acceptType("navigation");

      Panel informationPane = addPanel("information", PanelDirection.EAST, 200);
      informationPane.acceptType("information");

      Panel operationPanel = addPanel("operation", PanelDirection.SOUTH, 150);
      operationPanel.acceptType("operation");

      Panel editorPanel = addPanel("editor", PanelDirection.CENTER, 0);
      editorPanel.acceptType("editor");
   }

}
