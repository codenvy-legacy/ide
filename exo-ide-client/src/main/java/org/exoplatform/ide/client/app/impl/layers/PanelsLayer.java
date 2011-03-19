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

package org.exoplatform.ide.client.app.impl.layers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.ide.client.app.impl.Layer;
import org.exoplatform.ide.client.app.impl.Panel;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PanelsLayer extends Layer
{

   private Map<String, Panel> panels = new HashMap<String, Panel>();

   public Panel addPanel(String panelId, String[] acceptableTypes)
   {
      Panel panel = new Panel(panelId, acceptableTypes);
      add(panel);
      panels.put(panel.getPanelId(), panel);
      return panel;
   }

   public Map<String, Panel> getPanels()
   {
      return panels;
   }

   public List<Panel> getPanelsAsList()
   {
      return new ArrayList<Panel>(panels.values());
   }

}
