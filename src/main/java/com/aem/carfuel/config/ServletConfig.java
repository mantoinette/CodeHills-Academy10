package com.aem.carfuel.config;

import com.aem.carfuel.service.CarService;
import com.aem.carfuel.servlet.FuelStatsServlet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for registering manual servlets.
 * 
 * This demonstrates how to register traditional servlets in a Spring Boot application
 * and inject Spring-managed beans into them.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ServletConfig {
    
    private final CarService carService;
    
    /**
     * Register the FuelStatsServlet and map it to /servlet/fuel-stats.
     * 
     * This demonstrates manual servlet registration in Spring Boot,
     * which is different from the @WebServlet annotation approach.
     * 
     * @return ServletRegistrationBean for FuelStatsServlet
     */
    @Bean
    public ServletRegistrationBean<FuelStatsServlet> fuelStatsServlet() {
        log.info("Registering FuelStatsServlet at /servlet/fuel-stats");
        
        // Create servlet instance
        FuelStatsServlet servlet = new FuelStatsServlet();
        
        // Manually inject the CarService (Spring-managed bean)
        servlet.setCarService(carService);
        
        // Register the servlet with URL mapping
        ServletRegistrationBean<FuelStatsServlet> registrationBean = 
            new ServletRegistrationBean<>(servlet, "/servlet/fuel-stats");
        
        // Optional: Set load-on-startup order
        registrationBean.setLoadOnStartup(1);
        
        log.info("FuelStatsServlet registered successfully");
        
        return registrationBean;
    }
}
