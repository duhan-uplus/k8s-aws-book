package k8sbook.sampleapp.aws;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class S3FileHandler {

    // 로컬 모드에서는 AmazonS3 객체를 안 씁니다 (null 처리되거나 무시됨)
    public S3FileHandler(AmazonS3 amazonS3, ApplicationContext context) {
        // 생성자는 호환성을 위해 남겨둠
    }

    // [핵심] S3 다운로드 -> 로컬 파일 읽기로 변경
    // bucketName은 무시하고, key(파일경로)를 로컬 경로로 인식
    public Resource getFileResource(String bucketName, String key) {
        System.out.println(">>> [LOCAL MODE] Reading local file: " + key);
        return new FileSystemResource(key);
    }

    // 나머지 메서드(upload, delete, copy 등)는 로컬에서는 안 쓸 확률이 높지만
    // 호출 시 에러가 안 나게 빈 껍데기로 둡니다.
    public void deleteFile(String bucketName, String key) {
        System.out.println(">>> [LOCAL MODE] Mock delete file: " + key);
    }
    
    // 만약 원본 코드에 다른 메서드가 있다면 비슷한 방식으로 "System.out.println"만 하게 바꾸세요.
}

/*
package k8sbook.sampleapp.aws;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.cloud.aws.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class S3FileHandler {

    private final AmazonS3 amazonS3;

    private final ResourcePatternResolver resolver;

    public S3FileHandler(AmazonS3 amazonS3, ApplicationContext context) {
        this.amazonS3 = amazonS3;
        this.resolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, context);
    }

    public Resource[] listFilesInFolder(String bucketName, String folderPath, String filePattern) {
        var s3FolderUrl = "s3://" + bucketName + "/" + folderPath;
        var searchPath = s3FolderUrl + "/" + filePattern;
        try {
            return resolver.getResources(searchPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String bucketName, String filePath) {
        amazonS3.deleteObject(bucketName, filePath);
    }

    public void copyFile(String fromBucketName, String fromFilePath, String toBucketName, String toFilePath) {
        amazonS3.copyObject(fromBucketName, fromFilePath, toBucketName, toFilePath);
    }

    public Resource moveFileToWorkFolder(String bucketName, String filePath, String folderName, String workFolderSuffix) {
        var newFilePath = filePath.replaceFirst(folderName, folderName + workFolderSuffix);
        copyFile(bucketName, filePath, bucketName, newFilePath);
        deleteFile(bucketName, filePath);
        return resolver.getResource("s3://" + bucketName + "/" + newFilePath);
    }

    public void deleteAllFilesInFolder(String bucketName, String folderPath) {
        var searchPath = "s3://" + bucketName + "/" + folderPath + "/*";
        try {
            var files = resolver.getResources(searchPath);
            for (var file : files) {
                deleteFile(bucketName, file.getFilename());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAllFilesInFolderExcept(String bucketName, String folderPath, List<String> excludeList) {
        var searchPath = "s3://" + bucketName + "/" + folderPath + "/*";
        try {
            var files = resolver.getResources(searchPath);
            for (var file : files) {
                var shortFileName = new File(file.getFilename()).getName();
                if (!excludeList.contains(shortFileName)) {
                    deleteFile(bucketName, file.getFilename());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
*/
