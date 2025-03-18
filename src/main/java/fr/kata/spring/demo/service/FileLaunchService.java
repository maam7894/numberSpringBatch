package fr.kata.spring.demo.service;

public interface FileLaunchService {
    
    boolean fileExistInputFolder(String filename);
    boolean fileExistProcessedFolder(String filename);
    String resolvedInFilePath(String filename);
    String resolvedOutputFilePath(String inputFile);
    void moveFileToErrorFolder(String filename);
    void moveFileToProcessedFolder(String filename);

    

}
