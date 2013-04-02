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
package org.exoplatform.ide.codeassistant.storage.api;

import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface DataWriter {

    /** @param typeInfos */
    void addTypeInfo(List<TypeInfo> typeInfos, String artifact);

    /** @param packages */
    void addPackages(Set<String> packages, String artifact);

    /**
     * @param javaDocs
     * @param artifact
     */
    void addJavaDocs(Map<String, String> javaDocs, String artifact);


    /** @param typeInfos */
    void removeTypeInfo(String artifact);

    /** @param packages */
    void removePackages(String artifact);

    /**
     * @param javaDocs
     * @param artifact
     */
    void removeJavaDocs(String artifact);

}
