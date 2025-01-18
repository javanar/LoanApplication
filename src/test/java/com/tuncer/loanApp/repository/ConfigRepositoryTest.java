package com.tuncer.loanApp.repository;

import com.tuncer.loanApp.model.Config;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ConfigRepositoryTest {

    @Autowired
    private ConfigRepository configRepository;

    @Test
    void shouldReturnAConfigValueGivenValidKey(){
        //given
        Config config = new Config();
        config.setConfigurationKey("KEY");
        config.setConfigurationValue("value");
        config.setExplanation("test");

        //when
        configRepository.save(config);
        List<Config> configValue = configRepository.findByConfigurationKey("KEY");

        //then
        assertThat(configValue.getFirst().getConfigurationValue()).isEqualTo("value");
    }
}
