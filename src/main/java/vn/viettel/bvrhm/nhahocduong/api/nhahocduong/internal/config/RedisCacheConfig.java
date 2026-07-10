package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisCacheConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configure Jackson to serialize with type info (needed for Page<T> deserialization)
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jsonSerializer =
            new GenericJackson2JsonRedisSerializer(mapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))           // 5 phút default
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(jsonSerializer))
            .disableCachingNullValues();

        // Per-cache TTL config
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("patients",       defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigs.put("reExams",        defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigs.put("dashboardStats", defaultConfig.entryTtl(Duration.ofMinutes(2)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigs)
            .build();
    }

    // When Redis is down, fall back gracefully to DB instead of throwing exception
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(@NonNull RuntimeException ex, @NonNull Cache cache, @NonNull Object key) {
                log.warn("Redis GET failed [{}] key='{}': {}. Falling back to DB.", cache.getName(), key, ex.getMessage());
            }

            @Override
            public void handleCachePutError(@NonNull RuntimeException ex, @NonNull Cache cache, @NonNull Object key, @Nullable Object value) {
                log.warn("Redis PUT failed [{}] key='{}': {}.", cache.getName(), key, ex.getMessage());
            }

            @Override
            public void handleCacheEvictError(@NonNull RuntimeException ex, @NonNull Cache cache, @NonNull Object key) {
                log.warn("Redis EVICT failed [{}] key='{}': {}.", cache.getName(), key, ex.getMessage());
            }

            @Override
            public void handleCacheClearError(@NonNull RuntimeException ex, @NonNull Cache cache) {
                log.warn("Redis CLEAR failed [{}]: {}.", cache.getName(), ex.getMessage());
            }
        };
    }
}