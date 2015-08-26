/*
 * WebAppUser
 * Copyright (C) 2015 Nishimura Software Studio
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.vx68k.bitbucket.webhook.example;

import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationRequestUrl;
import org.vx68k.bitbucket.api.client.Client;
import org.vx68k.bitbucket.api.client.Service;
import org.vx68k.bitbucket.api.client.oauth.OAuthRedirection;

/**
 * User of this web application.
 *
 * @author Kaz Nishimura
 * @since 1.0
 */
@SessionScoped
@Named("user")
public class WebAppUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private WebAppConfig applicationConfig;

    private transient Service bitbucketService;

    public WebAppUser() {
    }

    public WebAppUser(WebAppConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public WebAppConfig getApplicationConfig() {
        return applicationConfig;
    }

    public boolean isAuthenticated() {
        if (bitbucketService == null) {
            return false;
        }
        return bitbucketService.isAuthenticated();
    }

    @Inject
    public void setApplicationConfig(WebAppConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public String login() throws IOException {
        Client bitbucketClient = applicationConfig.getBitbucketClient();
        AuthorizationCodeFlow flow
                = bitbucketClient.getAuthorizationCodeFlow(false);
        if (flow == null) {
            throw new IllegalStateException("No client credentials");
        }

        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpSession session = (HttpSession) externalContext.getSession(true);

        AuthorizationRequestUrl requestUrl = flow.newAuthorizationUrl();
        requestUrl.setState(session.getId());
        externalContext.redirect(requestUrl.build());

        return null;
    }

    public void requestToken(@Observes OAuthRedirection redirection)
            throws IOException {
        HttpServletRequest request = redirection.getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            String state = request.getParameter("state");
            if (state != null && state.equals(session.getId())) {
                // The redirection is for this session.

                String code = request.getParameter("code");
                if (code != null) {
                    // The resource access was authorized.
                    Client bitbucketClient
                            = applicationConfig.getBitbucketClient();
                    bitbucketService = bitbucketClient.getService(code);

                    HttpServletResponse response = redirection.getResponse();
                    response.sendRedirect(request.getContextPath() + "/");
                }
            }
        }
    }
}
