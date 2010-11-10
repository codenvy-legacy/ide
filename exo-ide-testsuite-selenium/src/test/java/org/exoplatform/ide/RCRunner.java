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
package org.exoplatform.ide;

import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class RCRunner extends BlockJUnit4ClassRunner {

   /**
    * @param klass Test class
    * @throws InitializationError
    *             if the test class is malformed.
    */
   public RCRunner(Class<?> klass) throws InitializationError {
       super(klass);
   }

   /*
    * Override withAfters() so we can append to the statement which will invoke the test
    * method. We don't override methodBlock() because we wont be able to reference 
    * the target object. 
    */
   @Override
   protected Statement withAfters(FrameworkMethod method, Object target, 
                                  Statement statement) {
       statement = super.withAfters(method, target, statement);
       return withAfterFailures(method, target, statement);
   }

   protected Statement withAfterFailures(FrameworkMethod method, Object target, 
                                         Statement statement) {
       List<FrameworkMethod> failures =
           getTestClass().getAnnotatedMethods(AfterFailure.class);
       return new RunAfterFailures(statement, failures, target);
   }
}
