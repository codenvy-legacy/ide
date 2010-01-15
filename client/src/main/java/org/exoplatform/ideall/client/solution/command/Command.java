/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.solution.command;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class Command
{

   private String id;

   private String title;

   private String icon;

   /**
    * Event which fired by this command.
    */
   private GwtEvent<?> event;

   /**
    * Enabled / Disabled
    */
   private boolean enabled;

   /**
    * Visibility Show / Hidden
    */
   private boolean visible;

   /*
    * Selected state
    */
   private boolean selected;

   private boolean hasDelimiterBefore;

   /**
    * List of command state listeners.
    * Listeners are uses for enabling, disabling, showing or hiding item in menu or toolbar.
    */
   private List<CommandStateListener> stateListeners = new ArrayList<CommandStateListener>();

   public Command(String id)
   {
      this.id = id;
   }

   public Command(String id, String title, String icon, GwtEvent<?> event)
   {
      this.id = id;
      this.title = title;
      this.icon = icon;
      this.event = event;
   }

   public String getId()
   {
      return id;
   }

   public String getTitle()
   {
      return title;
   }

   public String getIcon()
   {
      return icon;
   }

   public GwtEvent<?> getEvent()
   {
      return event;
   }

   public boolean isEnabled()
   {
      return enabled;
   }

   /**
    * Update command enabling.
    * Also this method updates all the CommandStateListeners.
    * 
    * @param enabled
    */
   public void setEnabled(boolean enabled)
   {
      if (this.enabled == enabled)
      {
         return;
      }

      this.enabled = enabled;
      for (CommandStateListener stateListener : stateListeners)
      {
         stateListener.updateCommandEnabling(enabled);
      }
   }

   public boolean isVisible()
   {
      return visible;
   }

   /**
    * Update command visibility.
    * Also this method updates all the CommandStateListeners.
    * 
    * @param visible
    */
   public void setVisible(boolean visible)
   {
      if (this.visible == visible)
      {
         return;
      }

      this.visible = visible;
      for (CommandStateListener stateListener : stateListeners)
      {
         stateListener.updateCommandVisibility(enabled);
      }
   }

   public boolean isSelected()
   {
      return selected;
   }

   /**
    * Update command selecting state.
    * Also this method updates all the CommandStateListeners.

    * @param selected
    */
   public void setSelected(boolean selected)
   {
      if (this.selected == selected)
      {
         return;
      }

      this.selected = selected;
      for (CommandStateListener stateListener : stateListeners)
      {
         stateListener.updateCommandSelectedState(selected);
      }
   }

   public List<CommandStateListener> getStateListeners()
   {
      return stateListeners;
   }

//   public Command enable()
//   {
//      setEnabled(true);
//      return this;
//   }
//
//   public Command disable()
//   {
//      setEnabled(false);
//      return this;
//   }
//
//   public Command show()
//   {
//      setVisible(true);
//      return this;
//   }
//
//   public Command hide()
//   {
//      setVisible(false);
//      return this;
//   }
//
//   public Command select()
//   {
//      setSelected(true);
//      return this;
//   }
//
//   public Command deselect()
//   {
//      setSelected(false);
//      return this;
//   }

   public boolean hasDelimiterBefore()
   {
      return hasDelimiterBefore;
   }
   
   public void setDelimiterBefore(boolean hasDelimiterBefore)
   {
      this.hasDelimiterBefore = hasDelimiterBefore;
   }

}
