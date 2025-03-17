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
package net.jadedmc.jadedparty.bukkit.databases;

import net.jadedmc.jadedparty.bukkit.JadedPartyBukkit;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Set;

/**
 * Manages the connection process to Redis.
 */
public class Redis {
    private final JadedPartyBukkit plugin;
    private JedisPool jedisPool;
    private boolean connected = false;

    public Redis(@NotNull final JadedPartyBukkit plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        // Grab Redis connection info.
        final String host = plugin.getConfigManager().getConfig().getString("Cache.Redis.host");
        final int port = plugin.getConfigManager().getConfig().getInt("Cache.Redis.port");
        final String username = plugin.getConfigManager().getConfig().getString("Cache.Redis.username");
        final String password = plugin.getConfigManager().getConfig().getString("Cache.Redis.password");

        // Announce connection attempt in debug mode.
        if(plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("Attempting to connect to Redis at " + host + ":" + port);
        }

        // Connect to Redis and subscribe to the connection thread.
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.MAX_VALUE);
        jedisPool = new JedisPool(jedisPoolConfig, host, port, username, password);
        subscribe();

        // Mark Redis as connected.
        connected = true;

        // Announce successful attempt in debug mode.
        if(plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("Connected to Redis.");
        }
    }

    public JedisPool jedisPool() {
        return jedisPool;
    }

    public void publish(String channel,  String message) {
        try(Jedis publisher = jedisPool.getResource()) {
            publisher.publish(channel, message);
        }
    }

    public void publishAsync(@NotNull final String channel, @NotNull final String message) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            publish(channel, message);
        });
    }

    public void set(String key, String value) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        }
    }

    public void sadd(String key, String value) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(key, value);
        }
    }

    public void del(String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    public Set<String> keys(@NotNull final String pattern) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(pattern);
        }
    }

    public String get(@NotNull final String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public boolean exists(@NotNull final String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
    }

    public void subscribe() {
        new Thread("Redis Subscriber") {
            @Override
            public void run() {

                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String msg) {
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                // TODO: Replace this. plugin.getServer().getPluginManager().callEvent(new RedisMessageEvent(channel, msg));
                                System.out.println("[REDIS SUB] " + channel + " " + msg);
                                plugin.getConfigManager().getCache().getMessageProcessor().process(channel, msg);
                            });

                        }
                    }, "jadedparty", "party");
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }.start();
    }
}