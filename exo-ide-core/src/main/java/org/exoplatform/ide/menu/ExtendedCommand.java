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
package org.exoplatform.ide.menu;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.core.expressions.Expression;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public interface ExtendedCommand extends Command
{

   /**
    * Command should be uniquely identified by it's id.
    * 
    * @return String, Command Id 
    */
   public String getUniqueId();
   
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
   public Image getIcon();

   /**
    * @return a Core Expression, it's result will be used
    * to determine Command's Visibility
    */
   public Expression isVisible();

   /**
    * @return a Core Expression, it's result will be used
    * to determine Command's enabled state
    */
   public Expression isEnabled();
}
