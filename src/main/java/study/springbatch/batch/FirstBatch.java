package study.springbatch.batch;


import java.util.Map;
import javax.sql.DataSource;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import study.springbatch.entity.AfterEntity;
import study.springbatch.entity.BeforeEntity;
import study.springbatch.repository.AfterRepository;
import study.springbatch.repository.BeforeRepository;

@Slf4j
@Configuration
public class FirstBatch {

    private final JobRepository jobRepository; // 스프링이 알아서 작업의 과정을 여기에 기록한다.(tracking)
    private final PlatformTransactionManager platformTransactionManager;
    private final BeforeRepository beforeRepository;
    private final AfterRepository afterRepository;
    private final DataSource dataSource;


    public FirstBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
            BeforeRepository beforeRepository, AfterRepository afterRepository,
            @Qualifier("dataDBSource") DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.beforeRepository = beforeRepository;
        this.afterRepository = afterRepository;
        this.dataSource = dataSource;
    }


    @Bean
    public Job firstJob() {
        return new JobBuilder("firstJob", jobRepository).start(firstStep()).build();
    }


    @Bean
    public Step firstStep() {
        log.info("first step");

        return new StepBuilder("firstStep", jobRepository).<BeforeEntity, AfterEntity>chunk(10,
                        platformTransactionManager).reader(beforeReader()).processor(middleProcessor()).writer(afterWriter())
                .build();
    }


    @Bean
    public RepositoryItemReader<BeforeEntity> beforeReader() {
        return new RepositoryItemReaderBuilder<BeforeEntity>().name("beforeReader").pageSize(10).methodName("findAll")
                .repository(beforeRepository).sorts(Map.of("id", Sort.Direction.ASC)).build();
    }


    //    @Bean
    //    public JdbcPagingItemReader<BeforeEntity> beforeReader() {
    //
    //        return new JdbcPagingItemReaderBuilder<BeforeEntity>().name("beforeReader").dataSource(dataSource)
    //                .selectClause("SELECT id, username").fromClause("FROM BeforeEntity")
    //                .sortKeys(Map.of("id", Order.ASCENDING)).rowMapper(new BeanPropertyRowMapper<>(BeforeEntity.class))
    //                .pageSize(10).build();
    //    }


    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> middleProcessor() {
        return new ItemProcessor<BeforeEntity, AfterEntity>() {

            @Override
            public AfterEntity process(BeforeEntity item) throws Exception {
                AfterEntity afterEntity = new AfterEntity();
                afterEntity.setUsername(item.getUsername());
                return afterEntity;
            }
        };
    }


    @Bean
    public RepositoryItemWriter<AfterEntity> afterWriter() {
        return new RepositoryItemWriterBuilder<AfterEntity>().repository(afterRepository).methodName("save").build();
    }
    //the following status: [COMPLETED] in 467ms


    //    @Bean
    //    public JdbcBatchItemWriter<AfterEntity> afterWriter() {
    //
    //        String sql = "INSERT INTO AfterEntity (username) VALUES (:username)";
    //
    //        return new JdbcBatchItemWriterBuilder<AfterEntity>().dataSource(dataSource).sql(sql)
    //                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>()).build();
    //    }
    //the following status: [COMPLETED] in 132ms
}
