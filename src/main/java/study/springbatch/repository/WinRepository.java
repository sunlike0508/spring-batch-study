package study.springbatch.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.springbatch.entity.WinEntity;


@Repository
public interface WinRepository extends JpaRepository<WinEntity, Long> {

    //findByWinGreaterThanEqual
    Page<WinEntity> findByWinGreaterThanEqual(Long win, Pageable pageable);
}
