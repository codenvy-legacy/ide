/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.groovy.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.groovy.shared.Attribute;
import org.exoplatform.ide.extension.groovy.shared.Jar;

/**
 * The interface for the AutoBean generator.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GroovyAutoBeanFactory.java Mar 16, 2012 12:27:47 PM azatsarynnyy $
 *
 */
public interface GroovyAutoBeanFactory extends AutoBeanFactory
{

   /**
    * A factory method for a jar bean.
    * 
    * @return an {@link AutoBean} of type {@link Jar}
    */
   AutoBean<Jar> jar();

   /**
    * A factory method for a jar attribute bean.
    * 
    * @return an {@link AutoBean} of type {@link Attribute}
    */
   AutoBean<Attribute> attribute();
}
