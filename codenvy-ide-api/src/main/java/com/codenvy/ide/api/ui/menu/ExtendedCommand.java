/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.api.ui.menu;

import com.codenvy.ide.api.expressions.Expression;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;

/**
 * Extended command is a derivative from Command, that also provides a Tooltip, 
 * Icon and Expressions controling the visibility and 'canExecute' flag. 
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ExtendedCommand extends Command
{

   /**
    * {@inheritDoc}
    */
   @Override
   public void execute();

   /**
    * Command can provide a Image. It will be displayed in
    * UI components related to this command.
    *
    * @return associated image
    */
   public ImageResource getIcon();

   /**
    * Command can provide a ToolTip. It will be displayed in
    * UI components related to this command.
    * 
    * @return tooltip
    */
   public String getToolTip();

   /**
    * @return a Core Expression, it's result will be used
    *         to determine Command's context, i.e. if in current IDE state (opened project, file etc.) this command has sense.
    */
   public Expression inContext();

   /**
    * @return a Core Expression, it's result will be used
    *         to determine Command's execute state.
    *         In UI this expression used for show enabled/disabled menu command, key binding uses for determine if this command
    *         mey execute if keys pressed.
    */
   public Expression canExecute();
}
