package study.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.springbatch.entity.AfterEntity;

@Repository
public interface AfterRepository extends JpaRepository<AfterEntity, Long> {}
