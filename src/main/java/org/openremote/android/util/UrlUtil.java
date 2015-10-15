package org.openremote.android.util;

import gumi.builders.UrlBuilder;

public class UrlUtil {

    public static UrlBuilder url(String baseUrl, String contextPath, String... pathSegments) {
        return UrlBuilder.fromString(baseUrl)
            .withPath(getPath(contextPath, pathSegments));
    }

    public static UrlBuilder url(String scheme, String host, String port, String contextPath, String... pathSegments) {
        return UrlBuilder.empty()
            .withScheme(scheme)
            .withHost(host)
            .withPort(port != null ? Integer.valueOf(port) : null)
            .withPath(getPath(contextPath, pathSegments));
    }

    protected static String getPath(String contextPath, String... pathSegments) {
        StringBuilder path = new StringBuilder();
        if (contextPath != null)
            path.append(contextPath);
        if (pathSegments != null) {
            for (String pathSegment : pathSegments) {
                path
                    .append(pathSegment.startsWith("/") ? "" : "/")
                    .append(pathSegment);
            }
        }
        return path.toString();
    }
}