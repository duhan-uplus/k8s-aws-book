/*package k8sbook.sampleapp.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
*/
/**
 * Amazon S3 클래스를 Bean에 등록하기 위한 클래스
 */
/*@Configuration
@Profile("!test")
public class AWSConfiguration {

    @Value("${cloud.aws.credentials.accesskey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretkey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

}
*/
package k8sbook.sampleapp.aws;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * [로컬 수정] Amazon S3 클래스를 Bean에 등록 (하지만 실제 AWS 연결은 안 함)
 */
@Configuration
@Profile("!test")
public class AWSConfiguration {

    // accessKey, secretKey, region 변수 선언과 @Value 주입을 모두 제거했습니다.
    // 어차피 안 쓸 거니까요.

    @Bean
    public AmazonS3 amazonS3() {
        System.out.println(">>> [LOCAL MODE] AWSConfiguration: AmazonS3 Client is mocked (NULL).");
        // 실제 AWS 연결 시도를 원천 차단
        return null;
    }

}

