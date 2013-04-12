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
 * Interface for storing and managing text.
 * <p>
 * Provides access to the stored text and allows to manipulate it.
 * </p>
 * <p>
 * Clients may implement this interface or use {@link org.eclipse.jface.text.GapTextStore} or
 * {@link org.eclipse.jface.text.CopyOnWriteTextStore}.
 * </p>
 */
public interface ITextStore {

    /**
     * Returns the character at the specified offset.
     *
     * @param offset
     *         the offset in this text store
     * @return the character at this offset
     */
    char get(int offset);

    /**
     * Returns the text of the specified character range.
     *
     * @param offset
     *         the offset of the range
     * @param length
     *         the length of the range
     * @return the text of the range
     */
    String get(int offset, int length);

    /**
     * Returns number of characters stored in this text store.
     *
     * @return the number of characters stored in this text store
     */
    int getLength();

    /**
     * Replaces the specified character range with the given text. <code>replace(getLength(), 0, "some text")</code> is a valid
     * call and appends text to the end of the text store.
     *
     * @param offset
     *         the offset of the range to be replaced
     * @param length
     *         the number of characters to be replaced
     * @param text
     *         the substitution text
     */
    void replace(int offset, int length, String text);

    /**
     * Replace the content of the text store with the given text. Convenience method for <code>replace(0, getLength(), text</code>.
     *
     * @param text
     *         the new content of the text store
     */
    void set(String text);
}
