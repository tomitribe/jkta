/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.eclipse.wg.jakartaee;

import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.Optional;

public class Env {

    public static GitHub github() {

        final String user = Optional.ofNullable(System.getenv("GITHUB_USER"))
                .orElse(System.getenv("USER"));

        final String token = Optional.ofNullable(System.getenv("GITHUB_TOKEN"))
                .orElseThrow(() -> new IllegalStateException("GITHUB_TOKEN must be set as an env variable\nFollow instructions here: https://github.com/settings/tokens/new"));

        try {
            return GitHub.connect(user, token);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to log into Github", e);
        }
    }
}
