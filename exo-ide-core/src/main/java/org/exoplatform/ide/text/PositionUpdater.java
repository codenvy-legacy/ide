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
package org.exoplatform.ide.text;

/**
 * A position updater is responsible for adapting document positions. When installed on a document, the position updater updates
 * the document's positions to changes applied to this document. Document updaters can be selective, i.e. they might only update
 * positions of a certain category.
 * <p>
 * Position updaters are of primary importance for the definition of the semantics of positions.
 * <p>
 * Clients may implement this interface or use the standard implementation {@link org.eclipse.jface.text.DefaultPositionUpdater}.
 * </p>
 * 
 */
public interface PositionUpdater
{

   /**
    * Adapts positions to the change specified by the document event. It is ensured that the document's partitioning has been
    * adapted to this document change and that all the position updaters which have a smaller index in the document's position
    * updater list have been called.
    * 
    * @param event the document event describing the document change
    */
   void update(DocumentEvent event);
}
