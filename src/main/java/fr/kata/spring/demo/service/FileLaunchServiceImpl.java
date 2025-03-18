package fr.kata.spring.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileLaunchServiceImpl implements FileLaunchService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLaunchServiceImpl.class);

    
    @Value("${batch.folder.input}")
    private String batchInputFolder;
    
    @Value("${batch.folder.output}")
    private String batchOutputFolder;
    
    @Value("${batch.folder.processed}")
    private String batchprocessedFolder;

    @Value("${batch.folder.error}")
    private String errorFolder;
    


    @Override
    public boolean fileExistInputFolder(String filename) {
        Path path = Paths.get(batchInputFolder).resolve(filename);
        return Files.exists(path);
    }


    @Override
    public boolean fileExistProcessedFolder(String filename) {
        Path path = Paths.get(batchprocessedFolder).resolve(filename);
        return Files.exists(path);
    }


    @Override
    public String resolvedInFilePath(String filename) {
        return Paths.get(batchInputFolder).resolve(filename).toFile().getAbsolutePath();
    }
    
    @Override
    public String resolvedOutputFilePath(String inputFile) {
        return Paths.get(batchOutputFolder).resolve( Paths.get(inputFile).getFileName()).toFile().getAbsolutePath();
    }


    @Override
    public void moveFileToErrorFolder(String filename) {
        try {
        Files.move(Paths.get(filename),  Paths.get(errorFolder).resolve(Paths.get(filename).getFileName()));
        LOGGER.info(" Move file  {} to {} ", filename,  Paths.get(errorFolder).resolve(Paths.get(filename).getFileName()), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            LOGGER.error(" Error: move file  {} to {} ", filename,  Paths.get(errorFolder).resolve(Paths.get(filename).getFileName()), e);

        }
    }


    @Override
    public void moveFileToProcessedFolder(String filename) {
        try {
            Files.move(Paths.get(filename), Paths.get(batchprocessedFolder).resolve(Paths.get(filename).getFileName()));
            LOGGER.info(" Move file  {} to {} ", filename,  Paths.get(batchprocessedFolder).resolve(Paths.get(filename).getFileName()), StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            LOGGER.error(" Error: move file  {} to {} ", filename,  Paths.get(batchprocessedFolder).resolve(Paths.get(filename).getFileName()), e);

        }
        
    }

}
