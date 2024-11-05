// Copyright (c) Alibaba, Inc. and its affiliates.

package com.alibaba.dashscope.protocol;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import okhttp3.Authenticator;

@Data
@SuperBuilder
public final class ConnectionConfigurations {
  /** Configuration the default connections. */
  private static final String PROXY_HOST_ENV = "DASHSCOPE_PROXY_HOST";

  private static final String PROXY_PORT_ENV = "DASHSCOPE_PROXY_PORT";
  private static final Integer defaultProxyPort = 443;
  // config the proxy
  private String proxyHost;
  private Integer proxyPort;
  private Authenticator proxyAuthenticator;

  /**
   * The connection pool maximum idle connections. Ref:
   * https://square.github.io/okhttp/3.x/okhttp/okhttp3/ConnectionPool.html
   */
  private static final String CONNECTION_POOL_SIZE_ENV = "DASHSCOPE_CONNECTION_POOL_SIZE";

  private static final String CONNECTION_IDLE_TIMEOUT_ENV = "DASHSCOPE_CONNECTION_IDLE_TIME";

  /**
   * Config number of async request(streamCall, duplexCall, streamIn audio services.) concurrently.
   * Ref: https://square.github.io/okhttp/3.x/okhttp/okhttp3/Dispatcher.html
   */
  private static final String MAXIMUM_ASYNC_REQUESTS_ENV = "DASHSCOPE_MAXIMUM_ASYNC_REQUESTS";

  private static final String MAXIMUM_ASYNC_REQUESTS_PER_HOST_ENV =
      "DASHSCOPE_MAXIMUM_ASYNC_REQUESTS_PER_HOST";

  private static final String WRITE_TIMEOUT_ENV = "DASHSCOPE_WRITE_TIMEOUT";
  private static final String READ_TIMEOUT_ENV = "DASHSCOPE_READ_TIMEOUT";
  private static final String CONNECTION_TIMEOUT_ENV = "DASHSCOPE_CONNECTION_TIMEOUT";

  private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(120);
  private static final Duration DEFAULT_WRITE_TIMEOUT = Duration.ofSeconds(60);
  private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(300);
  private static final Duration DEFAULT_CONNECTION_IDLE_TIMEOUT = Duration.ofSeconds(300);
  private static final Integer DEFAULT_CONNECTION_POOL_SIZE = 32;
  private static final Integer DEFAULT_MAXIMUM_ASYNC_REQUESTS = 32;
  private static final Integer DEFAULT_MAXIMUM_ASYNC_REQUESTS_PER_HOST = 32;

  private Duration connectTimeout;
  private Duration writeTimeout;
  private Duration responseTimeout;
  private Duration readTimeout;
  private Duration connectionIdleTimeout;
  private Integer connectionPoolSize;
  private Integer maximumAsyncRequests;
  private Integer maximumAsyncRequestsPerHost;

  public Duration getConnectTimeout() {
    return getDuration(connectTimeout, DEFAULT_CONNECT_TIMEOUT, CONNECTION_TIMEOUT_ENV);
  }

  public Duration getWriteTimeout() {
    return getDuration(writeTimeout, DEFAULT_WRITE_TIMEOUT, WRITE_TIMEOUT_ENV);
  }

  public Duration getReadTimeout() {
    return getDuration(readTimeout, DEFAULT_READ_TIMEOUT, READ_TIMEOUT_ENV);
  }

  public Duration getConnectionIdleTimeout() {
    return getDuration(
        connectionIdleTimeout, DEFAULT_CONNECTION_IDLE_TIMEOUT, CONNECTION_IDLE_TIMEOUT_ENV);
  }

  public Integer getConnectionPoolSize() {
    try {
      if (connectionPoolSize != null) {
        return connectionPoolSize;
      }
      Integer n = Integer.parseInt(System.getenv(CONNECTION_POOL_SIZE_ENV));
      return n;
    } catch (NumberFormatException e) {
      return DEFAULT_CONNECTION_POOL_SIZE;
    }
  }

  public Integer getMaximumAsyncRequests() {
    try {
      if (maximumAsyncRequests != null) {
        return maximumAsyncRequests;
      }
      Integer n = Integer.parseInt(System.getenv(MAXIMUM_ASYNC_REQUESTS_ENV));
      return n;
    } catch (NumberFormatException e) {
      return DEFAULT_MAXIMUM_ASYNC_REQUESTS;
    }
  }

  public Integer getMaximumAsyncRequestsPerHost() {
    try {
      if (maximumAsyncRequestsPerHost != null) {
        return maximumAsyncRequestsPerHost;
      }
      Integer n = Integer.parseInt(System.getenv(MAXIMUM_ASYNC_REQUESTS_PER_HOST_ENV));
      return n;
    } catch (NumberFormatException e) {
      return DEFAULT_MAXIMUM_ASYNC_REQUESTS_PER_HOST;
    }
  }

  private Duration getDuration(Duration target, Duration defaultValue, String env) {
    if (target == null) {
      try {
        long dur = Integer.parseInt(System.getenv(env));
        return Duration.ofSeconds(dur);
      } catch (NumberFormatException e) {
        return defaultValue;
      }
    } else {
      return target;
    }
  }

  public String getProxyHost() {
    if (proxyHost != null) {
      return proxyHost;
    }
    String envHost = System.getenv(PROXY_HOST_ENV);
    return envHost;
  }

  public Integer getProxyPort() {
    if (proxyPort != null) {
      return proxyPort;
    }
    String envPort = System.getenv(PROXY_PORT_ENV);
    if (envPort == null) {
      return defaultProxyPort;
    } else {
      return Integer.parseInt(envPort);
    }
  }

  public Proxy getProxy() {
    String host = getProxyHost();
    if (host != null) {
      return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, getProxyPort()));
    }
    return null;
  }
}
