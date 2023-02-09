package com.asledz.kancelaria_prawnicza.repository;

import com.asledz.kancelaria_prawnicza.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TypeRepository extends JpaRepository<Type, Long>, JpaSpecificationExecutor<Type> {
}
