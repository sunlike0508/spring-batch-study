package study.springbatch.batch;


import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import study.springbatch.entity.WinEntity;
import study.springbatch.repository.WinRepository;

@Slf4j
@Configuration
public class SecondBatch {

    private final JobRepository jobRepository; // 스프링이 알아서 작업의 과정을 여기에 기록한다.(tracking)
    private final PlatformTransactionManager platformTransactionManager;
    private final WinRepository winRepository;


    public SecondBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
            WinRepository winRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.winRepository = winRepository;
    }


    @Bean
    public Job secondJob() {
        return new JobBuilder("secondJob", jobRepository).start(secondStep()).build();
    }


    @Bean
    public Step secondStep() {
        return new StepBuilder("secondStep", jobRepository).<WinEntity, WinEntity>chunk(10, platformTransactionManager)
                .reader(winReader()).processor(trueProcessor()).writer(winWriter()).build();
    }


    @Bean
    public RepositoryItemReader<WinEntity> winReader() {
        return new RepositoryItemReaderBuilder<WinEntity>().name("winReader").pageSize(10)
                .methodName("findByWinGreaterThanEqual").arguments(Collections.singletonList(10L))
                .repository(winRepository).sorts(Map.of("id", Sort.Direction.DESC)).build();
    }


    @Bean
    public ItemProcessor<WinEntity, WinEntity> trueProcessor() {

        return item -> {
            item.setReward(true);
            return item;
        };
    }


    @Bean
    public RepositoryItemWriter<WinEntity> winWriter() {
        return new RepositoryItemWriterBuilder<WinEntity>().repository(winRepository).methodName("save").build();
    }
}
