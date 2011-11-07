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

import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.create.CreateApplicationEvent;

/**
 * Control for creating new application on Heroku.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 26, 2011 2:27:45 PM anya $
 *
 */
public class CreateApplicationControl extends AbstractHerokuControl
{

   public CreateApplicationControl()
   {
      super(HerokuExtension.LOCALIZATION_CONSTANT.createApplicationControlId());
      setTitle(HerokuExtension.LOCALIZATION_CONSTANT.createApplicationControlTitle());
      setPrompt(HerokuExtension.LOCALIZATION_CONSTANT.createApplicationControlPrompt());
      setEvent(new CreateApplicationEvent());
      setImages(HerokuClientBundle.INSTANCE.createApplication(),
         HerokuClientBundle.INSTANCE.createApplicationDisabled());
   }

}
