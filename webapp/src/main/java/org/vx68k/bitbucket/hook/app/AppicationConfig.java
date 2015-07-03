/*
 * ApplicationConfig - application configuration
 * Copyright (C) 2015 Kaz Nishimura
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

package org.vx68k.bitbucket.hook.app;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.vx68k.bitbucket.api.client.Credentials;

/**
 * Application configuration.
 *
 * @author Kaz Nishimura
 * @since 1.0
 */
@ApplicationScoped
@Named
public class AppicationConfig {

    @Inject
    private Credentials clientCredentials;

    public Credentials getClientCredentials() {
        return clientCredentials;
    }
}
