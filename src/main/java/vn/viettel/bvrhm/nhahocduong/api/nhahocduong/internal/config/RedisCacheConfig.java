package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.lang.NonNull;

import org.springframework.lang.Nullable;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisCacheConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    // When redis is down, handle gracefully by logging the error and falling back to DB.
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key) {
                log.error("Redis GET failed for key '{}' in cache '{}'. Falling back to DB. Error: {}", 
                        key, cache.getName(), exception.getMessage());
            }

            @Override
            public void handleCachePutError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key, @Nullable Object value) {
                log.error("Redis PUT failed for key '{}' in cache '{}'. Falling back to DB. Error: {}", 
                        key, cache.getName(), exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key) {
                log.error("Redis EVICT failed for key '{}' in cache '{}'. Falling back to DB. Error: {}", 
                        key, cache.getName(), exception.getMessage());
            }

            @Override
            public void handleCacheClearError(@NonNull RuntimeException exception, @NonNull Cache cache) {
                log.error("Redis CLEAR failed for cache '{}'. Falling back to DB. Error: {}", cache.getName(), exception.getMessage());
            }
        };
    }
}