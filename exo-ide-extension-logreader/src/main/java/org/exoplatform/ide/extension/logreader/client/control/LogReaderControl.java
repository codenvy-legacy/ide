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
package org.exoplatform.ide.extension.logreader.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.logreader.client.LogReaderClientBundle;
import org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderEvent;

/**
 * Control to open LogReader View
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class LogReaderControl extends SimpleControl implements IDEControl
{
   /**
    * Control ID.
    */
   public static final String ID = "View/Log";

   /**
    * Control's title.
    */
   public static final String TITLE = "Log";

   /**
    * Control's prompt, when user hovers the mouse on it.
    */
   public static final String PROMPT = "Show Log form";

   public LogReaderControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(LogReaderClientBundle.INSTANCE.logReader(), LogReaderClientBundle.INSTANCE.logReaderDisabled());
      setEvent(new ShowLogReaderEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);
      setEnabled(true);
   }

}
