package com.sds.parkinglotsystem.config;

import com.sds.parkinglotsystem.strategy.pricing.PricingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(PricingProperties.class)
public class ApplicationConfig {

    /**
     * A single injectable {@link Clock} keeps time deterministic and testable —
     * services read "now" through this bean rather than calling
     * {@code Instant.now()} directly.
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
