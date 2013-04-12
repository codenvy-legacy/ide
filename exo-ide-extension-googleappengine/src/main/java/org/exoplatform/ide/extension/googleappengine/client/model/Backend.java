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
package org.exoplatform.ide.extension.googleappengine.client.model;

import java.util.Set;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 4:45:33 PM anya $
 */
public interface Backend {
    public String getInstanceClass();

    public Integer getInstances();

    public Integer getMaxConcurrentRequests();

    public String getName();

    public Set<Option> getOptions();

    public State getState();

    public Boolean isDynamic();

    public Boolean isFailFast();

    public Boolean isPublic();

    public void setInstanceClass(String instanceClass);

    public void setInstances(Integer number);

    public void setMaxConcurrentRequests(Integer number);

    public void setName(String name);

    public void setOptions(Set<Option> options);

    public void setState(State state);

    public void setDynamic(Boolean isDynamic);

    public void setFailFast(Boolean isFailFast);

    public void setPublic(Boolean isPublic);
}
