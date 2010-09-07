/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.module.groovy;

import org.exoplatform.gwtframework.ui.client.util.UIHelper;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class Images
{

   public static final String IMAGE_URL = UIHelper.getGadgetImagesURL();

   public interface Buttons
   {

      public static String OK = IMAGE_URL + "module/groovy/buttons/ok.png";

      public static String NO = IMAGE_URL + "module/groovy/buttons/no.png";

      public static String YES = IMAGE_URL + "module/groovy/buttons/yes.png";

      public static String URL = IMAGE_URL + "module/groovy/buttons/url.png";

   }

   public interface Controls
   {

      static final String DEPLOY = IMAGE_URL + "module/groovy/bundled/deploy.png";

      static final String OUTPUT = IMAGE_URL + "module/groovy/bundled/output.png";

      static final String SET_AUTOLOAD = IMAGE_URL + "module/groovy/bundled/set_autoload.png";

      static final String UNSET_AUTOLOAD = IMAGE_URL + "module/groovy/bundled/unset_autoload.png";

      static final String UNDEPLOY = IMAGE_URL + "module/groovy/bundled/undeploy.png";

      static final String VALIDATE = IMAGE_URL + "module/groovy/bundled/validate.png";

   }

   public interface FileType
   {

      static final String GROOVY = IMAGE_URL + "module/groovy/filetype/groovy.png";

   }

}
