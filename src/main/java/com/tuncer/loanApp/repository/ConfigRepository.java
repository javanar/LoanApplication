package com.tuncer.loanApp.repository;

import com.tuncer.loanApp.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Integer> {
    List<Config> findByConfigurationKey(String configurationKey);
}
