/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.dto;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides implementations of all registered DTO interfaces.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
@Singleton
public class DtoFactory {
    private final Map<Class<?>, DtoProvider<?>> dtoInterface2Providers = new HashMap<Class<?>, DtoProvider<?>>();

    /**
     * Creates new instance of class which implements specified DTO interface.
     *
     * @param dtoInterface
     *         DTO interface
     * @return new instance of DTO implementation
     * @throws IllegalArgumentException
     *         if can't provide any implementation for specified interface
     */
    public <T> T createDto(Class<T> dtoInterface) {
        return getDtoProvider(dtoInterface).newInstance();
    }

    /**
     * Creates new instance of class which implements specified DTO interface, parses specified JSON string and uses
     * parsed data for initializing fields of DTO object.
     *
     * @param json
     *         JSON data
     * @param dtoInterface
     *         DTO interface
     * @return new instance of DTO implementation
     * @throws IllegalArgumentException
     *         if can't provide any implementation for specified interface
     */
    public <T> T createDtoFromJson(String json, Class<T> dtoInterface) {
        return getDtoProvider(dtoInterface).fromJson(json);
    }

    /**
     * Parses the JSON data from the specified sting into list of objects of the specified type.
     *
     * @param json
     *         JSON data
     * @param dtoInterface
     *         DTO interface
     * @return list of DTO
     * @throws IllegalArgumentException
     *         if can't provide any implementation for specified interface
     */
    public <T> Array<T> createListDtoFromJson(String json, Class<T> dtoInterface) {
        final DtoProvider<T> dtoProvider = getDtoProvider(dtoInterface);
        final JSONArray jsonArray = JSONParser.parseStrict(json).isArray();
        final Array<T> result = Collections.createArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            String payload = jsonArray.get(i).isObject().toString();
            result.add(dtoProvider.fromJson(payload));
        }

        return result;
    }

    /** Serializes dto to JSON format. */
    public <T> String toJson(T dto) {
        if (dto instanceof JsonSerializable) {
            return ((JsonSerializable)dto).toJson();
        }
        throw new IllegalArgumentException("JsonSerializable instance required. ");
    }

    /**
     * Registers DtoProvider for DTO interface.
     *
     * @param dtoInterface
     *         DTO interface
     * @param provider
     *         provider for DTO interface
     * @see DtoProvider
     */
    public void registerProvider(Class<?> dtoInterface, DtoProvider<?> provider) {
        dtoInterface2Providers.put(dtoInterface, provider);
    }

    private <T> DtoProvider<T> getDtoProvider(Class<T> dtoInterface) {
        DtoProvider<?> dtoProvider = dtoInterface2Providers.get(dtoInterface);
        if (dtoProvider == null) {
            throw new IllegalArgumentException("Unknown DTO type " + dtoInterface);
        }
        return (DtoProvider<T>)dtoProvider;
    }
}
