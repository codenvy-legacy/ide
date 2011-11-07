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
package org.exoplatform.ide.extension.samples.client.wizard;

import org.exoplatform.ide.extension.samples.client.ProjectProperties;

/**
 * Interface for wizard step.
 * <p/>
 * You can use this interface,
 * if you want to create multi-step wizard.
 * <p/>
 * Used to navigate between wizard steps:
 * what to do on current step.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: WizardContinuable.java Oct 17, 2011 2:59:03 PM vereshchaka $
 */
public interface WizardContinuable
{
   /**
    * Continue wizard on next step.
    * @param projectProperties project properties, which were enter by user.
    */
   void onContinue(ProjectProperties projectProperties);
}
