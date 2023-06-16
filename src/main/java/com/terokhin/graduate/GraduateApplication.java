package com.terokhin.graduate;

import com.terokhin.graduate.config.MainConfiguration;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@ConfigurationPropertiesScan(basePackageClasses = {
        MainConfiguration.class
})
@SpringBootApplication
public class GraduateApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduateApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.getConfiguration().setCollectionsMergeEnabled(false);
        return modelMapper;
    }
}
