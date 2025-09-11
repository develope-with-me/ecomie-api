package org.csbf.security.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface FileUploadService {

    void init();

    String save(MultipartFile file);

    Resource load(String fileName);

    void deleteAll();

    boolean resourceExist(String fileName);

    void deleteFile(String fileName);

    Stream<Path> loadAll();
}
