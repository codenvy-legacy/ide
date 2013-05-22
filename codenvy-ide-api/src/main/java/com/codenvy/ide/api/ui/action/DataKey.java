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
package com.codenvy.ide.api.ui.action;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DataKey<T> {
    private static final Map<String, DataKey> ourDataKeyIndex = new HashMap<String, DataKey>();

    private final String myName;

    private DataKey(@NotNull String name) {
        myName = name;
    }

    public static <T> DataKey<T> create(@NotNull String name) {
        //noinspection unchecked
        DataKey<T> key = ourDataKeyIndex.get(name);
        if (key != null) {
            return key;
        }
        key = new DataKey<T>(name);
        ourDataKeyIndex.put(name, key);
        return key;
    }

    @NotNull
    public String getName() {
        return myName;
    }

    /**
     * For short, use MY_KEY.is(dataId) instead of MY_KEY.getName().equals(dataId)
     *
     * @param dataId
     *         key name
     * @return {@code true} if name of DataKey equals to {@code dataId},
     *         {@code false} otherwise
     */
    public final boolean is(String dataId) {
        return myName.equals(dataId);
    }

    @Nullable
    public T getData(@NotNull DataContext dataContext) {
        //noinspection unchecked
        return (T)dataContext.getData(myName);
    }

//    @Nullable
//    public T getData(@NotNull DataProvider dataProvider) {
//        //noinspection unchecked
//        return (T)dataProvider.getData(myName);
//    }
}
