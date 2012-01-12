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

import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class RunAfterFailures extends Statement {

   private final Statement fNext;

   private final Object fTarget;

   private final List<FrameworkMethod> fAfterFailures;
   
   public RunAfterFailures(Statement next, List<FrameworkMethod> afterFailures,
                           Object target) {
       fNext= next;
       fAfterFailures= afterFailures;
       fTarget= target;
   }

   @Override
   public void evaluate() throws Throwable {
       List<Throwable> fErrors = new ArrayList<Throwable>();
       fErrors.clear();
       try {
           fNext.evaluate();
       } catch (Throwable e) {
           fErrors.add(e);
           for (FrameworkMethod each : fAfterFailures) {
               try {
                   each.invokeExplosively(fTarget, e);
               } catch (Throwable e2) {
                   fErrors.add(e2);
               }
           }
       }
       if (fErrors.isEmpty()) {
           return;
       }
       if (fErrors.size() == 1) {
           throw fErrors.get(0);
       }
       throw new MultipleFailureException(fErrors);
   }

}
