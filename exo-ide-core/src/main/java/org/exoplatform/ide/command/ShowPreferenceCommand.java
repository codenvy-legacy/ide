/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.command;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.preferences.PreferencesAgentImpl;
import org.exoplatform.ide.preferences.PreferencesPresenter;

/**
 * Command to show preferences dialog.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowPreferenceCommand implements Command
{
   private final Resources resources;

   private final PreferencesAgentImpl agent;

   /**
    * Create command.
    * 
    * @param resources
    * @param agent
    */
   @Inject
   public ShowPreferenceCommand(Resources resources, PreferencesAgentImpl agent)
   {
      this.resources = resources;
      this.agent = agent;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void execute()
   {
      PreferencesPresenter dialog = new PreferencesPresenter(resources, agent);
      dialog.showPreferences();
   }
}