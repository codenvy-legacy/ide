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
package org.exoplatform.ide.extension.appfog.server;

import org.exoplatform.ide.extension.appfog.shared.AppfogSystemService;
import org.exoplatform.ide.extension.appfog.shared.Infra;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogSystemServiceImpl implements AppfogSystemService {
    private String vendor;
    private String type;
    private String version;
    private String description;
    private Infra  infra;

    public AppfogSystemServiceImpl() {
    }

    public AppfogSystemServiceImpl(String vendor, String type, String version, String description, Infra infra) {
        this.vendor = vendor;
        this.type = type;
        this.version = version;
        this.description = description;
        this.infra = infra;
    }

    @Override
    public String getVendor() {
        return vendor;
    }

    @Override
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Infra getInfra() {
        return infra;
    }

    @Override
    public void setInfra(Infra infra) {
        this.infra = infra;
    }

    @Override
    public String toString() {
        return "AppfogSystemServiceImpl{" +
               "vendor='" + vendor + '\'' +
               ", type='" + type + '\'' +
               ", version='" + version + '\'' +
               ", description='" + description + '\'' +
               ", infra=" + infra +
               '}';
    }
}
