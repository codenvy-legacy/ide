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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.debug.Breakpoint;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:39:59 AM Mar 28, 2012 evgen $
 */
public class EditorBreakPoint extends Breakpoint {
    private final BreakPoint breakPoint;

    /**
     * Create editor breakpoint.
     *
     * @param breakPoint
     * @param message
     */
    public EditorBreakPoint(BreakPoint breakPoint, String message) {
        super(Type.BREAKPOINT, breakPoint.getLocation().getLineNumber(), message);
        this.breakPoint = breakPoint;

    }

    /** {@inheritDoc} */
    @Override
    public int getLineNumber() {
        return breakPoint.getLocation().getLineNumber();
    }

    /** @return the breakPoint */
    public BreakPoint getBreakPoint() {
        return breakPoint;
    }
}