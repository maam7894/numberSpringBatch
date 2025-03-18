package fr.kata.spring.demo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

import fr.kata.spring.demo.dto.NumberDto;

public class NumberProcessListener implements ItemProcessListener<NumberDto, NumberDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberProcessListener.class);

    @Override
    public void beforeProcess(NumberDto input) {
        LOGGER.info("Number record has been read: {} ",  input);
    }

    @Override
    public void afterProcess(NumberDto input, NumberDto result) {
        LOGGER.info("Number record has been processed to : {} " , result);
    }

    @Override
    public void onProcessError(NumberDto input, Exception e) {
        LOGGER.error("Error in reading the number record : {} " , input);
        LOGGER.error("Error in reading the number record : { }" , e);
    }
}
