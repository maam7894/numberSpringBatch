package fr.kata.spring.demo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.Validator;

import fr.kata.spring.demo.dto.NumberDto;
import fr.kata.spring.demo.service.RuleService;


public class NumberProcessor implements ItemProcessor<NumberDto, NumberDto> {
    
  
    private static final Logger LOGGER = LoggerFactory.getLogger(NumberProcessor.class);

    
    private final RuleService ruleService;
    private final Validator<NumberDto> validator;

    public NumberProcessor(RuleService ruleService, Validator<NumberDto> validator ) {
        this.ruleService = ruleService;
        this.validator = validator;
    }
     
    @Override
    public NumberDto process(NumberDto item) throws Exception {
         validator.validate(item); 
        item.setResult(ruleService.transform(item.getInput()));
        return item;
    }
}
