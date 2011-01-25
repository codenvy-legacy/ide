/*
 * Copyright (C) 2010 eXo Platform SAS.
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
      
      public static String ADD = IMAGE_URL + "module/groovy/buttons/add.png";

      public static String REMOVE = IMAGE_URL + "module/groovy/buttons/remove.png";

      public static String YES = IMAGE_URL + "module/groovy/buttons/yes.png";

      public static String URL = IMAGE_URL + "module/groovy/buttons/url.png";

   }

   public interface Controls
   {

      static final String DEPLOY = IMAGE_URL + "module/groovy/bundled/deploy.png";
      
      static final String DEPLOY_SANDBOX = IMAGE_URL + "module/groovy/bundled/deploy-sandbox.png";

      static final String OUTPUT = IMAGE_URL + "module/groovy/bundled/output.png";

      static final String SET_AUTOLOAD = IMAGE_URL + "module/groovy/bundled/set_autoload.png";

      static final String UNSET_AUTOLOAD = IMAGE_URL + "module/groovy/bundled/unset_autoload.png";

      static final String UNDEPLOY = IMAGE_URL + "module/groovy/bundled/undeploy.png";
      
      static final String UNDEPLOY_SANDBOX = IMAGE_URL + "module/groovy/bundled/undeploy-sandbox.png";

      static final String VALIDATE = IMAGE_URL + "module/groovy/bundled/validate.png";
      
      static final String RUN_GROOVY_SERVICE = IMAGE_URL + "module/groovy/bundled/run_groovy_service.png"; 
      
      static final String CONFIGURE_BUILD_PATH = IMAGE_URL + "module/groovy/bundled/configure-build-path.png";   

   }

   public interface FileType
   {

      static final String GROOVY = IMAGE_URL + "module/groovy/filetype/groovy.png";
      
      static final String REST_SERVICE = IMAGE_URL + "module/groovy/filetype/rest.png";
      
      static final String GROOVY_TEMPLATE = IMAGE_URL + "module/groovy/filetype/gtmpl.png";

   }

   public interface ClassPath
   {
      static final String SOURCE_FILE = IMAGE_URL + "module/groovy/classpath/source-file.png";
      
      static final String SOURCE_FOLDER = IMAGE_URL + "module/groovy/classpath/source-folder.png";
      
      static final String WORKSPACE = IMAGE_URL + "module/groovy/classpath/workspace.png";
   }
}
