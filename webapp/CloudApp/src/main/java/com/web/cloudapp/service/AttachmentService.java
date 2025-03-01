package com.web.cloudapp.service;

import com.web.cloudapp.Exception.BadRequest;
import com.web.cloudapp.Exception.ResourceNotFound;
import com.web.cloudapp.Repository.AttachmentRepository;
import com.web.cloudapp.Repository.NoteRepository;
import com.web.cloudapp.model.Attachment;
import com.web.cloudapp.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

@Service
public class AttachmentService {

    @Autowired
    Environment env;

    @Autowired
    private NoteService noteService;
    @Autowired
    private AttachmentRepository attachmentRepo;
    @Value("${image.filepath}")
    private String filePath;

    @Autowired
    private AwsService awsService;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private LogService logService;

    //Get All Attachments of a Note
    public List<Attachment> getAllAttachments(String noteId){
        try {
            Note n = noteService.getNote(noteId);
            return n.getAttachments();
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }

    }

    //Create a new Attachment
    public Attachment addAttachment(String noteId, MultipartFile attachment)throws Exception {
        try {
            Note n = noteService.getNote(noteId);
            Attachment a = new Attachment();
            List<Attachment> attachments = n.getAttachments();
            a = addFileToPath(a, attachment);
            attachments.add(a);
            n.setAttachments(attachments);
            noteRepository.save(n);
            logService.logger.info("Attachment created succesfully");
            return a;
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }
    }

    //Updating an Attachment
    public boolean updateAttachment(String noteId, String attachmentId, MultipartFile attachment) throws Exception{
        try {
            Attachment a = attachmentRepo.findById(attachmentId).orElseThrow(() -> new ResourceNotFound("attachment", "Id", attachmentId));
            Note n = noteService.getNote(noteId);
            List<Attachment> attachments = n.getAttachments();
            if (deleteFile(a)) {
                attachments.remove(a);
                attachmentRepo.delete(a);
                a = addFileToPath(a, attachment);
                attachments.add(a);
                n.setAttachments(attachments);
                noteRepository.save(n);
                logService.logger.info("Attachment updated successfully");
                return true;
            } else return false;
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }
    }

    //Deleting an Attachment
    public boolean deleteAttachment(String noteId, String attachmentId) throws Exception{
        try {
            Attachment a = attachmentRepo.findById(attachmentId).orElseThrow(() -> new BadRequest("Attachment doesn't exists"));
            Note n = noteService.getNote(noteId);
            List<Attachment> attachments = n.getAttachments();
            if (deleteFile(a)) {
                attachments.remove(a);
                attachmentRepo.delete(a);
                n.setAttachments(attachments);
                noteRepository.save(n);
                logService.logger.info("Attachment deleted successfully");
                return true;
            } else return false;
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }
    }

    //Moving the File to local folder or Amazon aws S3
    public Attachment addFileToPath(Attachment a, MultipartFile file) throws Exception{
        String attatchmentUrl=null;
        String attachType=null;
        try {
            File pathNew = new File(filePath);
            if (!pathNew.exists()) {
                pathNew.mkdir();
            }
            if (Arrays.asList(env.getActiveProfiles()).contains("dev")){
                attatchmentUrl = awsService.uploadFile(file,a.getId());
            }
            else {
                File path = new File(filePath + a.getId() + "_" + file.getOriginalFilename());
                attatchmentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(path.getAbsolutePath()).toUriString();
                file.transferTo(path);
            }
            a.setUrl(attatchmentUrl);
            attachType = attatchmentUrl.substring(attatchmentUrl.indexOf('.') + 1);
            a.setAtt_type(attachType);
            a.setSize(file.getSize());
            logService.logger.info("File added to the path: "+a.getUrl());
            return a;
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }
    }

    //Deleting the file from Local folder or Amazon aws S3
    public boolean deleteFile(Attachment a) throws Exception{
        if (Arrays.asList(env.getActiveProfiles()).contains("dev")){
            return awsService.deleteFileFromS3Bucket(a.getUrl().split("/")[4]);
        }
        else {
            try {
                URL url = new URL(URLDecoder.decode(a.getUrl(), "UTF-8"));
                String path = url.getPath();
                File file = new File(path);
                file.delete();
                logService.logger.info("File deleted from the path");
                return true;
            } catch (Exception ex){
                logService.logger.severe(ex.getMessage());
                throw ex;
            }
        }
    }
}
