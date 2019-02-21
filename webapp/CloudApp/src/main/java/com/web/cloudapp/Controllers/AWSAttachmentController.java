package com.web.cloudapp.Controllers;

import com.web.cloudapp.Repository.AttachmentRepository;
import com.web.cloudapp.Repository.NoteRepository;
import com.web.cloudapp.model.Attachment;
import com.web.cloudapp.model.Note;
import com.web.cloudapp.model.User;
import com.web.cloudapp.service.AwsService;
import com.web.cloudapp.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Profile("dev")
@Controller
@RequestMapping
@RestController
public class AWSAttachmentController  {

    @Autowired
    private AwsService awsService;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserService service;

    @Autowired
    private AttachmentRepository attachmentRepository;


    @RequestMapping(value = "/note/{id}/attachments", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createAttachment(@PathVariable(value = "id") String id, @RequestParam("file") MultipartFile file) throws IOException {
        Note note;
        User user = service.getUserName();
        Map<String,String> map = new HashMap<>();
        Attachment a = new Attachment();

        note = noteRepository.getNote(id);
        if(note == null ){
            map.put("status", HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity(map,HttpStatus.NOT_FOUND);
        }

        if(!user.getUserName().equals(note.getUserData().getUserName())){
            map.put("status",HttpStatus.UNAUTHORIZED.toString());
            return new ResponseEntity(map,HttpStatus.UNAUTHORIZED);
        }else{
            File convFile = new File(file.getOriginalFilename());

            String ext = FilenameUtils.getExtension(convFile.getPath());
            if(user.getUserName().equals(note.getUserData().getUserName())){
                List<Attachment> as = note.getAttachments();

                String url = awsService.uploadFile(file,a.getId());
                a.setUrl(url);
                as.add(a);
                System.out.println("COME HERE");
                System.out.println(note.getId());
                //attachmentRepository.save(a);
                note.setAttachments(as);
                noteRepository.save(note);
                return new ResponseEntity(a,HttpStatus.OK);
            }
        }
        return new ResponseEntity(map,HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value ="/note/{id}/attachments", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getAttachments(@PathVariable(value = "id") String id){
        Map<String,String> map = new HashMap<String, String>();
        Note note;
        User user = service.getUserName();
        note = noteRepository.getNote(id);
        if(note == null ){
            map.put("status",HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity(map,HttpStatus.NOT_FOUND);
        }
        else if(user.getUserName().equals(note.getUserData().getUserName())){
            map.put("status",HttpStatus.OK.toString());
            return new ResponseEntity(note.getAttachments(),HttpStatus.OK);
        }else{
            map.put("status",HttpStatus.UNAUTHORIZED.toString());
            return new ResponseEntity(map,HttpStatus.UNAUTHORIZED);
        }
    }


    //Update
    @PutMapping("/note/{id}/attachments/{idAttachment}")
    public @ResponseBody ResponseEntity updateAttachment( @PathVariable(value ="id") String noteId,@PathVariable(value ="idAttachment") String attachId,@RequestParam("file") MultipartFile file) {
        Map<String,String> map = new HashMap<>();
        User user = service.getUserName();
        Note n;
        Attachment a = null;
        String url ="";
        if(noteRepository.findById(noteId).isPresent()) n=noteRepository.findById(noteId).get();
        else n=null;
        if(n==null ){
            map.put("status", HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity(map,HttpStatus.NOT_FOUND);
        }
        else if(n.getUserData()!=user){
            map.put("status", HttpStatus.UNAUTHORIZED.toString());
            return new ResponseEntity(map,HttpStatus.UNAUTHORIZED);
        }
        else if(n.getUserData().getUserName().equals(user.getUserName())){
            List<Attachment> as = n.getAttachments();
            int count = 0 ;
            for(Attachment attach : as){
                if(attach.getId().equals(attachId)){
                    System.out.println(attach.getUrl().split("/")[4]);
                    awsService.deleteFileFromS3Bucket(attach.getUrl().split("/")[4]);
                    url = awsService.uploadFile(file,attach.getId());
                    count +=1;
                    a = attach;
                    attachmentRepository.delete(a);
                    System.out.println(attachmentRepository.getAllAttachments(n));
                    break;
                }
            }

        }

        return new ResponseEntity(map,HttpStatus.NO_CONTENT);
    }


    //Delete
    @DeleteMapping("/note/{id}/attachments/{idAttachment}")
    public @ResponseBody ResponseEntity deleteAttachment( @PathVariable(value ="id") String noteId,@PathVariable(value ="idAttachment") String attachId) {
        Map<String,String> map = new HashMap<>();
        User user = service.getUserName();
        Note n;
        Attachment a = null;
        if(noteRepository.findById(noteId).isPresent()) n=noteRepository.findById(noteId).get();
        else n=null;
        if(n==null ){
            map.put("status", HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity(map,HttpStatus.NOT_FOUND);
        }
        else if(n.getUserData()!=user){
            map.put("status", HttpStatus.UNAUTHORIZED.toString());
            return new ResponseEntity(map,HttpStatus.UNAUTHORIZED);
        }
        else if(n.getUserData().getUserName().equals(user.getUserName())){
            List<Attachment> as = n.getAttachments();
            int count = 0 ;
            for(Attachment attach : as){
                if(attach.getId().equals(attachId)){
                    System.out.println(attach.getUrl().split("/")[4]);
                    awsService.deleteFileFromS3Bucket(attach.getUrl().split("/")[4]);
                    count +=1;
                    a = attach;
                }
            }
            if(a!=null) {
                System.out.println("I came to deletle");
                as.remove(count);
                attachmentRepository.delete(a);
                System.out.println("I came to deletle"+as.size());
                n.setAttachments(as);
                noteRepository.save(n);
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }else{
                map.put("status", HttpStatus.NOT_FOUND.toString());
                return new ResponseEntity(map,HttpStatus.NOT_FOUND);}
        }

        return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
    }

}
