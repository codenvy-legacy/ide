/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.server.internal.core.search;

import com.codenvy.ide.ext.java.server.core.search.SearchParticipant;
import com.codenvy.ide.ext.java.server.core.search.SearchPattern;

import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;

/**
 * TODO add spec
 */
public abstract class IndexQueryRequestor {

	// answer false if requesting cancel
	public abstract boolean acceptIndexMatch(String documentPath, SearchPattern indexRecord, SearchParticipant participant, AccessRuleSet access);

}
