package k8sbook.sampleapp.aws;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Component
public class S3FileHandler {

    // 로컬 모드: 생성자 유지 (S3 Mock 안 씀)
    public S3FileHandler(AmazonS3 amazonS3, ApplicationContext context) {
    }

    // [핵심] 파일 읽기 (로컬 파일 리턴)
    public Resource getFileResource(String bucketName, String key) {
        System.out.println(">>> [LOCAL MODE] Reading local file: " + key);
        return new FileSystemResource(key);
    }

    // --- 아래는 컴파일 에러 방지용 껍데기 메서드들 (Dummy Methods) ---

    // 1. deleteAllFilesInFolder
    public void deleteAllFilesInFolder(String bucketName, String folderName) {
        System.out.println(">>> [LOCAL MODE] Skipping deleteAllFilesInFolder: " + folderName);
    }

    // 2. deleteAllFilesInFolderExcept
    public void deleteAllFilesInFolderExcept(String bucketName, String folderName, List<String> excludeFiles) {
        System.out.println(">>> [LOCAL MODE] Skipping deleteAllFilesInFolderExcept: " + folderName);
    }

    // 3. listFilesInFolder (중요: 빈 리스트라도 리턴해야 함)
    public Resource[] listFilesInFolder(String bucketName, String folderName, String pattern) {
        System.out.println(">>> [LOCAL MODE] Mock listFilesInFolder: " + folderName);
        
        // 실제 동작: folderName으로 들어온 경로(예: /data)의 파일 목록을 뒤져서 리턴하거나
        // 일단 빌드 통과를 위해 빈 배열 리턴 (배치 로직에 따라 파일 처리가 스킵될 수 있음)
        // 만약 로컬 파일 목록을 진짜로 처리하게 하려면 File 객체 뒤져서 리턴해야 함.
        
        // 간단한 테스트를 위해: 그냥 null 또는 빈 배열 리턴
        // (단, LocationDataLoader에서 null check 없이 쓰면 NPE 날 수 있으니 빈 배열 추천)
        return new Resource[0];
    }
    
    // 혹시 다른 메서드도 호출된다면 비슷하게 void 또는 null/empty 리턴으로 막아주세요.
}


