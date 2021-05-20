/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.client.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.familysearch.homelands.admin.client.FileService;
import org.familysearch.homelands.admin.client.exception.AdminException;

/**
 * @author wjohnson000
 *
 */
public class LocalFileService implements FileService {

    private String basePath;

    public LocalFileService(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void createFolder(String path, String folderName) throws AdminException {
    }

    @Override
    public List<String> listFolders() {
        return Collections.emptyList();
    }

    @Override
    public List<String> listFolders(String path) {
        return Collections.emptyList();
    }

    @Override
    public List<String> listFiles() {
        return Collections.emptyList();
    }

    @Override
    public List<String> listFiles(String path) {
        return Collections.emptyList();
    }

    @Override
    public void uploadFile(String path, String filename, byte[] contents) throws AdminException {
        try {
            Files.write(Paths.get(basePath, filename), contents, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException ex) {
            System.out.println("Unable to write file: " + filename + " --> " + ex.getMessage());
        }
    }

    @Override
    public Map<String, Boolean> uploadFiles(String path, Map<String, byte[]> files)
                            throws ExecutionException, InterruptedException {
        return Collections.emptyMap();
    }

    @Override
    public void copyFile(String locationPath, String filename, String destinationPath) throws AdminException {
    }

    @Override
    public void copyFile(String locationPath, String filename, String destinationPath, String newFileName)
                            throws AdminException {
    }

    @Override
    public void moveFile(String locationPath, String filename, String destinationPath) throws AdminException {
    }

    @Override
    public void moveFile(String locationPath, String filename, String destinationPath, String newFileName)
                            throws AdminException {
    }

    @Override
    public byte[] getFile(String path, String filename) {
        try {
            return Files.readAllBytes(Paths.get(basePath, filename));
        }
        catch (IOException ex) {
            System.out.println("Unable to get files: " + ex.getMessage());
            return new byte[0];
        }
    }

    @Override
    public Map<String, byte[]> getFiles(String path) throws ExecutionException, InterruptedException {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Boolean> deleteFolder(String path) throws ExecutionException, InterruptedException {
        return Collections.emptyMap();
    }

    @Override
    public void deleteFile(String path, String filename) throws AdminException {
    }

    @Override
    public Map<String, Boolean> deleteFiles(String path, List<String> filename)
                            throws ExecutionException, InterruptedException {
        return Collections.emptyMap();
    }

}
