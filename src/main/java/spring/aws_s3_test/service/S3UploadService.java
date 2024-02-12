package spring.aws_s3_test.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3UploadService {
  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public String saveFile(MultipartFile multipartFile) throws IOException {
    String originalFilename = multipartFile.getOriginalFilename();

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(multipartFile.getSize());
    metadata.setContentType(multipartFile.getContentType());

    // S3에 파일 업로드
    amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
    // 업로드한 파일 URL 반환
    return amazonS3.getUrl(bucket, originalFilename).toString();
  }

  // S3에 저장된 파일 삭제
  public void deleteImage(String originalFilename)  {
    amazonS3.deleteObject(bucket, originalFilename);
  }
}
