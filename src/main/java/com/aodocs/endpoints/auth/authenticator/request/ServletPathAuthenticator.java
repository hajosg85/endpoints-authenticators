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
package com.aodocs.endpoints.auth.authenticator.request;

import com.aodocs.endpoints.auth.ExtendedUser;
import com.aodocs.endpoints.auth.authenticator.ExtendedAuthenticator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.server.spi.config.model.ApiMethodConfig;
import com.google.common.base.CharMatcher;
import lombok.NonNull;

import javax.servlet.http.HttpServletRequest;

/**
 * This authenticators allows any method with the provided servlet path.
 */
public class ServletPathAuthenticator extends ExtendedAuthenticator {

    private static final CharMatcher NORMALIZER = CharMatcher.is('/');

    @JsonProperty
    private final String servletPath;

    public ServletPathAuthenticator(@JsonProperty("servletPath") @NonNull String servletPath) {
        this.servletPath = servletPath;
    }

    @Override
    public AuthorizationResult isAuthorized(ExtendedUser extendedUser, ApiMethodConfig methodConfig, HttpServletRequest request) {
        return new AuthorizationResult(normalize(request.getServletPath()).equals(normalize(servletPath)));
    }

    private String normalize(String path) {
        return NORMALIZER.trimAndCollapseFrom(path, '/');
    }

}
