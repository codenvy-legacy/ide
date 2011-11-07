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
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.stack.ChangeApplicationStackEvent;

/**
 * Control for changing stack (deployment environment) of the Heroku application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 28, 2011 5:58:35 PM anya $
 *
 */
public class ChangeApplicationStackConrol extends SimpleControl implements IDEControl
{
   public ChangeApplicationStackConrol()
   {
      super(HerokuExtension.LOCALIZATION_CONSTANT.changeStackControlId());
      setTitle(HerokuExtension.LOCALIZATION_CONSTANT.changeStackControlTitle());
      setPrompt(HerokuExtension.LOCALIZATION_CONSTANT.changeStackControlPrompt());
      setImages(HerokuClientBundle.INSTANCE.changeStack(), HerokuClientBundle.INSTANCE.changeStackDisabled());
      setEvent(new ChangeApplicationStackEvent());
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
