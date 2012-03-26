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
package org.exoplatform.ide.extension.java.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.java.shared.MavenResponse;
import org.exoplatform.ide.extension.java.shared.ast.AstItem;
import org.exoplatform.ide.extension.java.shared.ast.JavaProject;
import org.exoplatform.ide.extension.java.shared.ast.Package;
import org.exoplatform.ide.extension.java.shared.ast.RootPackage;

/**
 * The interface for the {@link AutoBean} generator.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: JavaAutoBeanFactory.java Mar 26, 2012 12:06:00 AM azatsarynnyy $
 *
 */
public interface JavaAutoBeanFactory extends AutoBeanFactory
{
   /**
    * A factory method for a maven response result bean.
    * 
    * @return an {@link AutoBean} of type {@link MavenResponse}
    */
   AutoBean<MavenResponse> mavenResponse();

   /**
    * A factory method for a root package bean.
    * 
    * @return an {@link AutoBean} of type {@link RootPackage}
    */
   AutoBean<RootPackage> rootPackage();

   /**
    * A factory method for a package bean.
    * 
    * @return an {@link AutoBean} of type {@link Package}
    */
   AutoBean<Package> packageItem();

   /**
    * A factory method for an AST item bean.
    * 
    * @return an {@link AutoBean} of type {@link AstItem}
    */
   AutoBean<AstItem> astItem();

   /**
    * A factory method for a java project bean.
    * 
    * @return an {@link AutoBean} of type {@link JavaProject}
    */
   AutoBean<JavaProject> javaProject();
}
