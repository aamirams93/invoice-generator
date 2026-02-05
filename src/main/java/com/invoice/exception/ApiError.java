package com.invoice.exception;

import java.time.Instant;

public record ApiError(String errorCode, String message, String path, Instant timestamp, String traceId)
{
}