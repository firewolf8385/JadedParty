/*
 * This file is part of JadedParty, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.jadedparty.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.jadedmc.jadedparty.velocity.databases.Redis;
import net.jadedmc.jadedparty.velocity.listeners.DisconnectListener;
import net.jadedmc.jadedparty.velocity.settings.ConfigManager;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.nio.file.Path;

@Plugin(
        id = "jadedparty",
        name = "JadedParty",
        version = "1.0",
        url = "https://www.jadedmc.net"
)
public class JadedPartyVelocity {
    private final ConfigManager configManager;
    private final Logger logger;
    private final ProxyServer proxyServer;
    private final Redis redis;

    @Inject
    public JadedPartyVelocity(final ProxyServer proxyServer, final Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;

        // Load the config file.
        this.configManager = new ConfigManager(this, dataDirectory);

        // Connect to redis.
        this.redis = new Redis(this);
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public ProxyServer getProxyServer() {
        return this.proxyServer;
    }

    public Redis getRedis() {
        return this.redis;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxyServer.getEventManager().register(this, new DisconnectListener(this));
    }
}