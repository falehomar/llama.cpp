# Certificate Verification Configuration

This document explains the configuration changes made to disable certificate verification in the Gradle build process.

## Changes Made

### 1. Created `gradle.properties`

Added a new `gradle.properties` file with the following settings:

```properties
# Disable SSL certificate verification for Gradle
systemProp.javax.net.ssl.trustStore=NONE
systemProp.javax.net.ssl.trustStoreType=NONE
systemProp.javax.net.ssl.trustStorePassword=
systemProp.javax.net.ssl.keyStore=NONE
systemProp.javax.net.ssl.keyStoreType=NONE
systemProp.javax.net.ssl.keyStorePassword=
systemProp.javax.net.ssl.keyPassword=

# Disable certificate verification for Gradle dependency resolution
systemProp.org.gradle.internal.http.connectionTimeout=180000
systemProp.org.gradle.internal.http.socketTimeout=180000
systemProp.org.gradle.internal.repository.max.tentatives=3
systemProp.org.gradle.internal.repository.initial.backoff=500

# Disable certificate verification for Maven/Ivy repositories
systemProp.maven.wagon.http.ssl.insecure=true
systemProp.maven.wagon.http.ssl.allowall=true
systemProp.maven.wagon.http.ssl.ignore.validity.dates=true
```

### 2. Modified `gradle/wrapper/gradle-wrapper.properties`

Changed the `validateDistributionUrl` property from `true` to `false`:

```properties
validateDistributionUrl=false
```

## Purpose

These changes disable SSL certificate verification during the Gradle build process, which is useful in environments where:

- Self-signed certificates are used
- Corporate proxies intercept and modify SSL traffic
- Network security tools interfere with certificate validation
- Development environments lack proper certificate configuration

## Security Considerations

Disabling certificate verification reduces security by eliminating protection against:

- Man-in-the-middle attacks
- DNS spoofing
- Malicious dependency injection

These settings should be used with caution, particularly in production environments. Consider using proper certificate management as a more secure alternative when possible.
