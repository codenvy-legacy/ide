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
package org.exoplatform.ide.extension.heroku.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;

/**
 * Control for grouping all Heroku controls.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 31, 2011 3:32:33 PM anya $
 *
 */
public class HerokuControlsGroup extends SimpleControl implements IDEControl
{
   /**
    * Control ID.
    */
   public static final String ID = "PaaS/Heroku";

   /**
    * Control's title.
    */
   public static final String TITLE = "Heroku";

   /**
   * Control's prompt, when user hovers the mouse on it.
   */
   public static final String PROMPT = "Heroku";

   public HerokuControlsGroup()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(HerokuClientBundle.INSTANCE.heroku(), HerokuClientBundle.INSTANCE.herokuDisabled());
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
