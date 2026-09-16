package com.sawmik.spring_batch.repository;

import com.sawmik.spring_batch.entity.ServerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerLogRepository extends JpaRepository<ServerLog, Long> {
}
