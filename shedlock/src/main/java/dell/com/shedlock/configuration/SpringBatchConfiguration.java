package dell.com.shedlock.configuration;

import dell.com.shedlock.model.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    private final JobBuilderFactory jobBuilderFactory;

    private final DataSource dataSource;

    public SpringBatchConfiguration(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory, DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    FlatFileItemReader<User> itemReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                .resource(new ClassPathResource("data.csv"))
                .delimited()
                .delimiter(",")
                .names("name","surname","address")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<User>(){{
                    setTargetType(User.class);
                }})
                .build();
    }

    @Bean
    JdbcBatchItemWriter<User> itemWriter() {
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO users (name, surname, address) VALUES (:name, :surname, :address)")
                .build();
    }

    @Bean
    Step step() {
        return stepBuilderFactory.get("step")
                .<User, User>chunk(10)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    Job job() {
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }
}
