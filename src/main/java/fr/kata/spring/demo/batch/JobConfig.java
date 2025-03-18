package fr.kata.spring.demo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.SpringValidator;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import fr.kata.spring.demo.dto.NumberDto;
import fr.kata.spring.demo.service.FileLaunchService;
import fr.kata.spring.demo.service.RuleService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JobConfig {
    
    @Autowired
    private RuleService ruleService;
    
    @Autowired
    private  FileLaunchService fileLaunchService;

    @Bean
    public org.springframework.validation.Validator validator() {
        // see https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#validation-beanvalidation-spring
        return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
    }

    @Bean
    public Validator<NumberDto> itemValidator() {
        SpringValidator<NumberDto> springValidator = new SpringValidator<>();
        springValidator.setValidator(validator());
        return springValidator;
    }
  
    @Bean
    @JobScope
    public JobCompletionNotificationListener jobCompletionNotificationListener(@Value("#{jobParameters['filePath']}") String path) {
        return new JobCompletionNotificationListener(path, fileLaunchService );
    }

    @Bean
    public NumberProcessor numberProcessor(RuleService ruleService) {
        return new NumberProcessor(ruleService, itemValidator());
    }

    
    @Bean
    @StepScope
    public FlatFileItemReader<NumberDto> reader(@Value("#{jobParameters['filePath']}") String path) {
        return new FlatFileItemReaderBuilder<NumberDto>()
                .name("numberReader")
                .resource(new FileSystemResource(path))
                .delimited().delimiter("")
                .names("input")
                .fieldSetMapper(fieldSet -> {
                    NumberDto dto = new NumberDto();
                    dto.setInput(fieldSet.readInt("input"));
                    return dto;
                })
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<NumberDto> writer(@Value("#{jobParameters['filePath']}") String input){
        FlatFileItemWriter<NumberDto> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setResource(new FileSystemResource(fileLaunchService.resolvedOutputFilePath(input)));

        DelimitedLineAggregator<NumberDto> ticketDelimitedLineAggregator = new DelimitedLineAggregator<>();
        ticketDelimitedLineAggregator.setDelimiter(DelimitedLineTokenizer.DELIMITER_TAB);

        BeanWrapperFieldExtractor<NumberDto> wrapperFieldExtractor = new BeanWrapperFieldExtractor<>();
        wrapperFieldExtractor.setNames(new String[] {"input", "result"});
        ticketDelimitedLineAggregator.setFieldExtractor(wrapperFieldExtractor);

        itemWriter.setLineAggregator(ticketDelimitedLineAggregator);
        return itemWriter;
    }
    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<NumberDto, NumberDto>chunk(100, transactionManager)
                .reader(reader(null))  // null path just for type resolution
                .writer(writer(null))
                .processor(numberProcessor(ruleService))
                .faultTolerant()
                .skip(Exception.class)
                .noSkip(ValidationException.class)
                .listener(new NumberProcessListener())
                .build();
    }

    @Bean
    public Job importCustomerJob(JobRepository jobRepository, Step step1)  {
        return new JobBuilder("numberJob", jobRepository)
                .start(step1)
                .listener(jobCompletionNotificationListener(null))
                .build();
    }
}
