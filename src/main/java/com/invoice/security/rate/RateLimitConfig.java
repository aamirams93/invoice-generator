package com.invoice.security.rate;

import java.time.Duration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

public class RateLimitConfig {

    // Login: 5 requests per minute
	public static Bucket loginBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(
                        5,
                        Refill.intervally(5, Duration.ofMinutes(1))
                ))
                .build();
    }

    // Register: 2 requests per minute
    public static Bucket registerBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(
                        2,
                        Refill.intervally(2, Duration.ofMinutes(1))
                ))
                .build();
    }

    // OTP: 3 requests per minute
    public static Bucket otpBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(
                        1,
                        Refill.intervally(1, Duration.ofMinutes(1))
                ))
                .build();
    }
    public static Bucket defaultBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                .build();
    }
}
