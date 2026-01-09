package k8sbook.sampleapp.aws;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * [로컬 수정] S3 대신 로컬 파일 시스템을 사용하도록 변경된 핸들러
 */
@Component
public class S3FileHandler {

    // 로컬에서는 AmazonS3 객체를 안 쓰므로 필드 제거해도 되지만, 생성자 호환성을 위해 둠
    private final ResourcePatternResolver resolver;

    public S3FileHandler(AmazonS3 amazonS3, ApplicationContext context) {
        // [중요] S3 Resolver 대신 "로컬 파일 리졸버"로 교체!
        this.resolver = new PathMatchingResourcePatternResolver(context);
    }

    // 1. 파일 목록 조회 (S3 URL 대신 로컬 파일 경로 패턴 검색)
    public Resource[] listFilesInFolder(String bucketName, String folderPath, String filePattern) {
        // bucketName 무시. folderPath를 로컬 경로로 해석
        // 예: "file:/app/data/*.csv"
        var searchPath = "file:" + folderPath + "/" + filePattern;
        System.out.println(">>> [LOCAL] Searching files: " + searchPath);
        
        try {
            return resolver.getResources(searchPath);
        } catch (IOException e) {
            // 파일이 없으면 빈 배열 리턴 (에러 대신)
            System.out.println(">>> [LOCAL] No files found or error: " + e.getMessage());
            return new Resource[0];
        }
    }

    // 2. 파일 삭제 (흉내만 냄)
    public void deleteFile(String bucketName, String filePath) {
        System.out.println(">>> [LOCAL] Mock DELETE file: " + filePath);
        // 실제 삭제하고 싶다면: new File(filePath).delete();
    }

    // 3. 파일 복사 (흉내만 냄)
    public void copyFile(String fromBucketName, String fromFilePath, String toBucketName, String toFilePath) {
        System.out.println(">>> [LOCAL] Mock COPY file: " + fromFilePath + " -> " + toFilePath);
    }

    // 4. 작업 폴더로 이동 (복사 후 원본 삭제 흉내 + 리소스 리턴)
    public Resource moveFileToWorkFolder(String bucketName, String filePath, String folderName, String workFolderSuffix) {
        var newFilePath = filePath.replaceFirst(folderName, folderName + workFolderSuffix);
        
        System.out.println(">>> [LOCAL] Mock MOVE to work folder: " + newFilePath);
        
        // 이동했다 치고, 해당 경로의 파일 리소스를 리턴 (실제 파일은 원래 위치에 있어야 읽힘)
        // 로컬 테스트 편의상 "원래 파일 경로"를 리턴하거나, 
        // 입력 파일이 딱 하나라면 그 파일을 가리키게 해야 함.
        
        // 일단은 원본 경로 그대로 리턴해서 읽기라도 되게 처리 (가장 안전)
        return new FileSystemResource(filePath);
    }

    // 5. 폴더 내 파일 모두 삭제
    public void deleteAllFilesInFolder(String bucketName, String folderPath) {
        System.out.println(">>> [LOCAL] Mock DELETE ALL in folder: " + folderPath);
    }

    // 6. 예외 파일 제외하고 삭제
    public void deleteAllFilesInFolderExcept(String bucketName, String folderPath, List<String> excludeList) {
        System.out.println(">>> [LOCAL] Mock DELETE ALL EXCEPT " + excludeList + " in: " + folderPath);
    }
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
