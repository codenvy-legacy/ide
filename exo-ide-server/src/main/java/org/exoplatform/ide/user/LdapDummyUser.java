/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.user;

import com.exoplatform.cloudide.userdb.NewUser;
import com.exoplatform.cloudide.userdb.client.UserDBServiceClient;
import org.picocontainer.Startable;

/**
 * Setting initial users when starting tomcat, because dummy userdb is empty at starting. Temporary solution.
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class LdapDummyUser implements Startable
{
   @Override
   public void start()
   {
      /**
       * need to run this in separate thread because while starting tomcat userdb client try to connect to dummy
       * database which is not started in current moment, that's why tomcat starting freeze. with new thread we
       * can start tomcat and when it's done userdb client is ready to add new user without any problem.
       */
      new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               UserDBServiceClient userDBServiceClient = new UserDBServiceClient();
               userDBServiceClient.addUser(new NewUser("exo", "exo"));
               userDBServiceClient.addUser(new NewUser("root", "root"));
            }
            catch (Exception e)
            {
               throw new IllegalStateException("Dummy user initialization failed", e.getCause());
            }
         }
      }).start();
   }

   @Override
   public void stop()
   {
   }
}
