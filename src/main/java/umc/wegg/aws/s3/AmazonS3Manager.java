package umc.wegg.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import umc.wegg.config.AmazonConfig;
import umc.wegg.domain.Uuid;
import umc.wegg.repository.UuidRepository;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;
    private final AmazonConfig amazonConfig;
    private final UuidRepository uuidRepository;

    public String upLoadFile(String keyName, MultipartFile file) throws IOException {
        log.info("Uploading file: {}", keyName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());  // ✅ Content-Type 설정 추가

        amazonS3.putObject(new PutObjectRequest(
                amazonConfig.getBucket(),
                keyName,
                file.getInputStream(),
                metadata
        ));

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String generateProfileKeyName(Uuid uuid) {
        String profilePath = amazonConfig.getProfilePath();
        if (!profilePath.endsWith("/")) {
            profilePath += "/";
        }
        return profilePath + uuid.getUuid();
    }

    public String generatePostKeyName(Uuid uuid) {
        String postPath = amazonConfig.getPostPath();
        if (!postPath.endsWith("/")) {
            postPath += "/";
        }
        return postPath + uuid.getUuid();
    }
}
