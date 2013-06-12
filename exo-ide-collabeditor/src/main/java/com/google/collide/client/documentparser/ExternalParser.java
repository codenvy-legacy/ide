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
package com.google.collide.client.documentparser;

import com.codenvy.ide.json.shared.JsonArray;
import org.exoplatform.ide.editor.api.codeassitant.Token;

/**
 * External parser for CollabEditor. It may be used instead of or
 * in addition with internal CodeMirror parser.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExternalParser.java May 29, 2013 11:50:25 AM azatsarynnyy $
 *
 */
public interface ExternalParser {

    /**
     * Returns tokens array for the given <code>content</code>.
     * 
     * @param content text content to extract tokens
     * @return tokens array
     */
    JsonArray<? extends Token> getTokenList(String content);
}
