package com.web.cloudapp.Controllers;

import com.web.cloudapp.model.Attachment;
import com.web.cloudapp.service.AttachmentService;
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

    ResponseEntity rs = new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);

    //Creating new Attachment for the note
    @PostMapping("/note/vinyas/{id}/attachments")
    public @ResponseBody
    ResponseEntity createAttachment(@PathVariable(value = "id") String id, @RequestParam("file") MultipartFile file){
        Attachment a =attachmentService.addAttachment(id,file);
        return new ResponseEntity(a,HttpStatus.CREATED);
    }

    //Get all the attachments of the note
    @GetMapping("/note/vinyas/{id}/attachments")
    public @ResponseBody ResponseEntity getAllAttachments(@PathVariable(value = "id") String id){
        List<Attachment> aL = attachmentService.getAllAttachments(id);
        return new ResponseEntity(aL,HttpStatus.OK);
    }

    //Update the attachment using attachment id
    @PutMapping("/note/vinyas/{id}/attachments/{idAttachment}")
    public @ResponseBody ResponseEntity updateAttachment( @PathVariable(value ="id") String noteId,@PathVariable(value ="idAttachment") String attachId,@RequestParam("file") MultipartFile file){
        boolean success = attachmentService.updateAttachment(noteId,attachId,file);
        if(success)rs= new ResponseEntity(HttpStatus.NO_CONTENT);
        return rs;
    }

    //Delete an attachment
    @DeleteMapping("/note/vinyas/{id}/attachments/{idAttachment}")
    public @ResponseBody ResponseEntity deleteAttachment( @PathVariable(value ="id") String noteId,@PathVariable(value ="idAttachment") String attachId){
        boolean success = attachmentService.deleteAttachment(noteId,attachId);
        if(success) rs= new ResponseEntity(HttpStatus.NO_CONTENT);
        return rs;
    }
}
//Test comment fr commit