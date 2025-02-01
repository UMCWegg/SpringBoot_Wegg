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
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    private final UuidRepository uuidRepository;

    public String upLoadFile(String KeyName, MultipartFile file) throws IOException {
        System.out.println(KeyName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), KeyName, file.getInputStream(), metadata));

        return amazonS3.getUrl(amazonConfig.getBucket(), KeyName).toString();
    }

    public String generateProfileKeyName(Uuid uuid) {
        return amazonConfig.getProfilePath() + '/' + uuid.getUuid();
    }

}
