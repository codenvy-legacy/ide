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
package org.exoplatform.ide.editor.javascript.client.contentassist;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
final class JsProposal extends JavaScriptObject
{

   /**
    * 
    */
   protected JsProposal()
   {
   }

   public native String getProposal()/*-{
		return this.proposal;
   }-*/;

   public native String getDescription()/*-{
		return this.description;
   }-*/;

   public native int getEscapePosition()/*-{
		return this.escapePosition ? this.escapePosition : -1;
   }-*/;

   public native Position[] getPositions()/*-{
		return this.positions ? this.positions : [];
   }-*/;

}
