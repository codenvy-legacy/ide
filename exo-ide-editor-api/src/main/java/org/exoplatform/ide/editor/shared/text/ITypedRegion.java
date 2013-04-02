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
package org.exoplatform.ide.editor.shared.text;

/**
 * Describes a region of an indexed text store such as a document or a string. The region consists of offset, length, and type.
 * The region type is defined as a string.
 * <p>
 * A typed region can, e.g., be used to described document partitions.
 * </p>
 * <p>
 * Clients may implement this interface or use the standard implementation {@link org.eclipse.jface.text.TypedRegion}.
 * </p>
 */
public interface ITypedRegion extends IRegion {

    /**
     * Returns the content type of the region.
     *
     * @return the content type of the region
     */
    String getType();
}
