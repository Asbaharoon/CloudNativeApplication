package com.web.cloudapp.service;

import java.io.*;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.util.Date;

@Service
public class AwsService {

    @Autowired
    private AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();


    @Value("${aws.s3.bucketname}")
    private String nameCardBucket;

    @Value("${endpointUrl}")
    private String endpointUrl;

    /*
     * upload file to folder and set it to public
     */
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    public String uploadFile(MultipartFile file1, String filename) {
        String fileUrl = "";
        System.out.println(s3.getS3AccountOwner());
        try {
            File file = convertMultiPartToFile(file1);
            String fileName = generateFileName(file1);
            System.out.println(fileName);
            endpointUrl="https://s3.amazonaws.com";
            fileUrl = endpointUrl + "/" + nameCardBucket + "/" + fileName;
            s3.putObject(new PutObjectRequest(nameCardBucket,
                    fileName, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        }catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    public String deleteFileFromS3Bucket(String fileNameToDelete) {
        String fileName = fileNameToDelete.substring(fileNameToDelete.lastIndexOf("/") + 1);
        s3.deleteObject(nameCardBucket,fileName);
        return "Successfully deleted";
    }

    public ResponseEntity<byte[]> downloadFile(String fileName){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            System.out.println("Downloading an object");
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
