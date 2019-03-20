package com.web.cloudapp.Controllers;

import com.timgroup.statsd.StatsDClient;
import com.web.cloudapp.model.Attachment;
import com.web.cloudapp.service.AttachmentService;
import com.web.cloudapp.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
     private StatsDClient statsDClient;
    @Autowired
    private LogService logService;


    ResponseEntity rs = new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);

    //Creating new Attachment for the note
    @PostMapping("/note/{id}/attachments")
    public @ResponseBody
    ResponseEntity createAttachment(@PathVariable(value = "id") String id, @RequestParam("file") MultipartFile file) throws Exception{
        try {
            Attachment a = attachmentService.addAttachment(id, file);
            statsDClient.incrementCounter("attachment.post");
            logService.logger.info("Request completed successfully with status : "+ HttpStatus.CREATED.toString());
            return new ResponseEntity(a, HttpStatus.CREATED);
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }
    }

    //Get all the attachments of the note
    @GetMapping("/note/{id}/attachments")
    public @ResponseBody ResponseEntity getAllAttachments(@PathVariable(value = "id") String id){
        List<Attachment> aL = attachmentService.getAllAttachments(id);
        statsDClient.incrementCounter("attachment.get");
        logService.logger.info("Request completed successfully with status : "+HttpStatus.OK.toString());
        return new ResponseEntity(aL,HttpStatus.OK);
    }

    //Update the attachment using attachment id
    @PutMapping("/note/{id}/attachments/{idAttachment}")
    public @ResponseBody ResponseEntity updateAttachment( @PathVariable(value ="id") String noteId,@PathVariable(value ="idAttachment") String attachId,@RequestParam("file") MultipartFile file) throws Exception{
        try {
            boolean success = attachmentService.updateAttachment(noteId, attachId, file);
            if (success) rs = new ResponseEntity(HttpStatus.NO_CONTENT);
            statsDClient.incrementCounter("attachment.put");
            logService.logger.info("Request completed successfully : "+ HttpStatus.NO_CONTENT.toString());
            return rs;
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }
    }

    //Delete an attachment
    @DeleteMapping("/note/{id}/attachments/{idAttachment}")
    public @ResponseBody ResponseEntity deleteAttachment( @PathVariable(value ="id") String noteId,@PathVariable(value ="idAttachment") String attachId) throws Exception{
        try {
            boolean success = attachmentService.deleteAttachment(noteId, attachId);
            if (success) rs = new ResponseEntity(HttpStatus.NO_CONTENT);
            statsDClient.incrementCounter("attachment.delete");
            logService.logger.info("Request completed successfully with status : "+HttpStatus.NO_CONTENT.toString());
            return rs;
        }catch (Exception ex){
            logService.logger.severe(ex.getMessage());
            throw ex;
        }
    }
}
