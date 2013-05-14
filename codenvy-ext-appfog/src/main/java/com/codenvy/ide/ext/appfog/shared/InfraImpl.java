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
package com.codenvy.ide.ext.appfog.shared;

import com.codenvy.ide.ext.appfog.shared.Infra;

/** @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a> */
public class InfraImpl implements Infra {
    private String name;
    private String provider;

    public InfraImpl() {
    }

    public InfraImpl(String name, String provider) {
        this.name = name;
        this.provider = provider;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "InfraImpl{" +
               "name='" + name + '\'' +
               ", provider='" + provider + '\'' +
               '}';
    }
}