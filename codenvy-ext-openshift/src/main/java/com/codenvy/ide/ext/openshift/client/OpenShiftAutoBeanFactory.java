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
package com.codenvy.ide.ext.openshift.client;

import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ext.openshift.shared.Credentials;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface OpenShiftAutoBeanFactory extends AutoBeanFactory {
    /**
     * A factory method for an application info bean.
     *
     * @return an {@link AutoBean} of type {@link AppInfo}
     */
    AutoBean<AppInfo> appInfo();

    /**
     * A factory method for a RedHat user info bean.
     *
     * @return a {@link com.google.web.bindery.autobean.shared.AutoBean} of type {@link RHUserInfo}
     */
    AutoBean<RHUserInfo> rhUserInfo();

    /**
     * A factory method for a credentials bean.
     *
     * @return an {@link AutoBean} of type {@link Credentials}
     */
    AutoBean<Credentials> credentials();
}
