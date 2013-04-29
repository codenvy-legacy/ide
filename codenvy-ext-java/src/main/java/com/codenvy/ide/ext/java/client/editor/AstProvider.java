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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.ext.java.client.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.client.internal.compiler.env.INameEnvironment;

import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.util.ListenerRegistrar.Remover;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface AstProvider {
    public interface AstListener {
        void onCompilationUnitChanged(CompilationUnit cUnit);
    }

    Remover addAstListener(AstListener listener);

    INameEnvironment getNameEnvironment();

    File getFile();
}
