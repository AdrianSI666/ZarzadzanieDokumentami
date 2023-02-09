package com.asledz.kancelaria_prawnicza.repository;

import com.asledz.kancelaria_prawnicza.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileRepository extends JpaRepository<File, Long>, JpaSpecificationExecutor<File> {
}
