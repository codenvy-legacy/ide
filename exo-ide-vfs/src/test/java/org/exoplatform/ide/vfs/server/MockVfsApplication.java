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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.server.exceptions.ConstraintExceptionMapper;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentExceptionMapper;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundExceptionMapper;
import org.exoplatform.ide.vfs.server.exceptions.LockExceptionMapper;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedExceptionMapper;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedExceptionMapper;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class MockVfsApplication extends Application
{
   
   private final Set<Object> singletons = new HashSet<Object>();
   
   public MockVfsApplication()
   {
      singletons.add(new ConstraintExceptionMapper());
      singletons.add(new InvalidArgumentExceptionMapper());
      singletons.add(new LockExceptionMapper());
      singletons.add(new ItemNotFoundExceptionMapper());
      singletons.add(new NotSupportedExceptionMapper());
      singletons.add(new PermissionDeniedExceptionMapper());
   }

      @Override
      public Set<Class<?>> getClasses()
      {
         Set<Class<?>> cls = new HashSet<Class<?>>(1);
         cls.add(MockVFS.class);
         return cls;
      }

      @Override
      public Set<Object> getSingletons()
      {
         return singletons;
      }

}
