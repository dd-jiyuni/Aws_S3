package spring.aws_s3_test.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//Controller Test
@RestController
@RequiredArgsConstructor
public class S3Controller {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Autowired
  AmazonS3Client amazonS3Client;

  @GetMapping("/upload")
  public ResponseEntity<Object> upload(MultipartFile[] multipartFileList) throws Exception {
    List<String> imagePathList = new ArrayList<>();

    for(MultipartFile multipartFile: multipartFileList) {
      String originalName = multipartFile.getOriginalFilename(); // 파일 이름
      long size = multipartFile.getSize(); // 파일 크기

      ObjectMetadata objectMetaData = new ObjectMetadata();
      objectMetaData.setContentType(multipartFile.getContentType());
      objectMetaData.setContentLength(size);

      // S3에 업로드
      amazonS3Client.putObject(
              new PutObjectRequest(bucket, originalName, multipartFile.getInputStream(), objectMetaData)
                      .withCannedAcl(CannedAccessControlList.PublicRead)
      );

      String imagePath = amazonS3Client.getUrl(bucket, originalName).toString(); // 접근가능한 URL 가져오기
      imagePathList.add(imagePath);
    }

    return new ResponseEntity<Object>(imagePathList, HttpStatus.OK);
  }
}
