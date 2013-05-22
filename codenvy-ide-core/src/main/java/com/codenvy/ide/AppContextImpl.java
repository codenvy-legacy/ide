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
package com.codenvy.ide;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;

/**
 * Default implementation of {@link AppContext}.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AppContextImpl implements AppContext {

    private final JsonStringMap<Object> dataId2Data;

    public AppContextImpl() {
        dataId2Data = JsonCollections.createStringMap();
    }

    /** {@inheritDoc} */
    @Override
    public void setData(@NotNull String dataId, Object data) {
        dataId2Data.put(dataId, data);
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public Object getData(String dataId) {
        return dataId2Data.get(dataId);
    }
}
