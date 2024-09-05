package study.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.springbatch.entity.BeforeEntity;

@Repository
public interface BeforeRepository extends JpaRepository<BeforeEntity, Long> {}
