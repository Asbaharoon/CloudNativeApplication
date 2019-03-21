package com.web.cloudapp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;

@Service
public class AwsService {

    @Autowired
    private AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    @Autowired
    private LogService logService;


    @Value("${aws.s3.bucketname}")
    private String nameCardBucket;

    @Value("${endpointUrl}")
    private String endpointUrl;

    /*
     * upload file to folder and set it to public
     */
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        return convFile;
    }
    private String generateFileName(MultipartFile multiPart){
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    public String uploadFile(MultipartFile file1, String filename)  throws Exception{
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(file1);
            String fileName = generateFileName(file1);
            endpointUrl="https://s3.amazonaws.com";
            fileUrl = endpointUrl + "/" + nameCardBucket + "/" + fileName;
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(FilenameUtils.getExtension(file.getPath()));
            s3.putObject(new PutObjectRequest(nameCardBucket,
                    fileName, file1.getInputStream(),metadata).withCannedAcl(CannedAccessControlList.PublicRead));
            logService.logger.info("File uploaded successfully");
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }
        return fileUrl;
    }

    //Deleting the file from S3 bucket
    public boolean deleteFileFromS3Bucket(String fileNameToDelete) {
        try {
            String fileName = fileNameToDelete.substring(fileNameToDelete.lastIndexOf("/") + 1);
            s3.deleteObject(nameCardBucket, fileName);
            logService.logger.info("File deleted successfully");
            return true;
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }
    }


    public ResponseEntity<byte[]> downloadFile(String fileName){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            S3Object s3object = s3.getObject(new GetObjectRequest(nameCardBucket, fileName));
            InputStream inputStream =s3object.getObjectContent();

            byte[] picturebuffer = new byte[512];
            int l = inputStream.read(picturebuffer);
            while (l >= 0) {
                outputStream.write(picturebuffer, 0, l);
                l = inputStream.read(picturebuffer);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "image/jpg");
        return new ResponseEntity<byte[]>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }
}
