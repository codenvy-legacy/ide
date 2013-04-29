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
package com.codenvy.ide.api.ui.workspace;

/** Defines Part's position on the Screen */
public enum PartStackType {
    /**
     * Contains navigation parts. Designed to navigate
     * by project, types, classes and any other entities.
     * Usually placed on the LEFT side of the IDE.
     */
    NAVIGATION,
    /**
     * Contains informative parts. Designed to display
     * the state of the application, project or processes.
     * Usually placed on the BOTTOM side of the IDE.
     */
    INFORMATION,
    /**
     * Contains editing parts. Designed to provide an
     * ability to edit any resources or settings.
     * Usually placed in the CENTRAL part of the IDE.
     */
    EDITING,
    /**
     * Contains tooling parts. Designed to provide handy
     * features and utilities, access to other services
     * or any other features that are out of other PartType
     * scopes.
     * Usually placed on the RIGHT side of the IDE.
     */
    TOOLING
}
