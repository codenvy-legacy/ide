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
package org.exoplatform.ide.extension.aws.server.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: AWSApplication.java Aug 23, 2012
 */
public class AWSApplication extends Application
{
   private final Set<Class<?>> classes;

//   private final Set<Object> singletons;

   public AWSApplication()
   {
      classes = new HashSet<Class<?>>(1);
      classes.add(BeanstalkService.class);
      classes.add(S3Service.class);
//      singletons = new HashSet<Object>(1); TODO
//      singletons.add(new AWSExceptionMapper());
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }

//   @Override
//   public Set<Object> getSingletons()
//   {
//      return singletons;
//   }
}
