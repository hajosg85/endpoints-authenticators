/*-
 * #%L
 * Extended authenticators for Cloud Endpoints v2
 * ---
 * Copyright (C) 2018 AODocs (Altirnao Inc)
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.aodocs.endpoints.auth.authenticator.dsl;

import com.aodocs.endpoints.auth.ExtendedUser;
import com.aodocs.endpoints.auth.authenticator.ExtendedAuthenticator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.api.server.spi.config.model.ApiMethodConfig;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import lombok.experimental.Delegate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * This authenticator builds a complex authenticator using a DSL.
 *
 * TODO: describe the DSL
 **
 */
public class DslAuthenticator extends ExtendedAuthenticator {

    public enum Format {
        YAML(new YAMLMapper()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)),
//        XML(new XmlMapper()), //TODO try make it work?
        JSON(new ObjectMapper());

        @Delegate
        private final ObjectMapper objectMapper;

        Format(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper
                    .setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
                    .registerModules(MODULES);
        }
    }

    private static final List<Module> MODULES = ImmutableList.of(
            DslAuthenticatorConfig.MODULE,
            new GuavaModule(),
            new ParameterNamesModule(JsonCreator.Mode.PROPERTIES)
    );

    private final ExtendedAuthenticator delegate;

    public DslAuthenticator(String dslConfig, Format format) throws IOException {
        this(format.reader().forType(ExtendedAuthenticator.class).readValue(dslConfig));
    }

    @VisibleForTesting
    DslAuthenticator(ExtendedAuthenticator delegate) {
        this.delegate = delegate instanceof DslAuthenticator ? ((DslAuthenticator) delegate).delegate : delegate;
    }

    public String toString(Format format) throws JsonProcessingException {
        return format.writeValueAsString(delegate);
    }

    @Override
    public AuthorizationResult isAuthorized(ExtendedUser extendedUser, ApiMethodConfig apiMethodConfig, HttpServletRequest request) {
        return delegate.isAuthorized(extendedUser, apiMethodConfig, request);
    }

}