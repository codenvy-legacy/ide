/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdi.shared;

import com.codenvy.dto.shared.DTO;

/**
 * Description of debug breakpoint.
 *
 * @author andrew00x
 */
@DTO
public interface BreakPoint {
    Location getLocation();

    void setLocation(Location location);

    BreakPoint withLocation(Location location);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    BreakPoint withEnabled(boolean enabled);

    String getCondition();

    void setCondition(String condition);

    BreakPoint withCondition(String condition);
}