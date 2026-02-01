package com.invoice.security.rate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.github.bucket4j.Bucket;



@Component
public class RateLimitService
{
	
	private final Map<String, Map<String, Bucket>> ipBuckets =
            new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip, String api) {

        return ipBuckets
                .computeIfAbsent(ip, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(api, this::createBucketForApi);
    }

    private Bucket createBucketForApi(String api) {
        return switch (api) {
            case "/api/v1/auth/motp" -> RateLimitConfig.otpBucket();
            case "/api/v1/auth/login" -> RateLimitConfig.loginBucket();
            case "/api/v1/auth/add" -> RateLimitConfig.registerBucket();
            default -> RateLimitConfig.defaultBucket();
        };
    }
}
