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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Profile("!dev")
@RestController
@ComponentScan(basePackages = {"com.web.cloudapp"})
public class LcoalAttachmentController {


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

               // String url = awsService.uploadFile(file,a.getId());
                String pathDir = System.getProperty("user.home")+"/uploads/";
        File pathNew = new File(pathDir);
        if(!pathNew.exists()){
            pathNew.mkdir();
        }
        File path = new File(pathDir+"_"+file.getOriginalFilename());
        String attatchmentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path.getAbsolutePath()).toUriString();

        a.setUrl(attatchmentUrl);

        file.transferTo(path);
            //    a.setUrl(url);
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
                    try{
                        System.out.println("print her");
                        System.out.println(attach.getUrl());
                URL url = new URL(URLDecoder.decode( attach.getUrl(), "UTF-8" ));
                String path = url.getPath();
                        System.out.println(path);
                File file = new File(path);
                file.delete();
                a =attach;
                noteRepository.getNote(noteId).getAttachments().remove(a);
                attachmentRepository.delete(a);
                        System.out.println(attachmentRepository.getAllAttachments(n));
                break;
            }catch(Exception e){

            }
               }
                count +=1;
            }
        }

        return new ResponseEntity(map,HttpStatus.NO_CONTENT);
    }




    @PutMapping("/note/{id}/attachments/{idAttachment}")
    public @ResponseBody ResponseEntity updateAttachment( @PathVariable(value ="id") String noteId,@PathVariable(value ="idAttachment") String attachId,@RequestParam("file") MultipartFile file) {
        Map<String,String> map = new HashMap<>();
        User user = service.getUserName();
        Note n;
        Attachment a = null;
        URL url = null ;
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
                    try {
             url = new URL(URLDecoder.decode( attach.getUrl(), "UTF-8" ));
            String path1 = url.getPath();
            File file1 = new File(path1);
            file1.delete();

            System.out.println(attach.getUrl());

            attachmentRepository.delete(attach);
            String pathDir = System.getProperty("user.home")+"/uploads/";
            File pathNew = new File(pathDir);
            if(!pathNew.exists()){
                pathNew.mkdir();
            }
            File path = new File(pathDir+"_"+file.getOriginalFilename());
            String attatchmentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(path.getAbsolutePath()).toUriString();
            a = attach;
            a.setUrl(attatchmentUrl);
            file.transferTo(path);
        }        catch(Exception e){

 }
                    count +=1;
                    a = attach;
                }
            }
            if(a!=null) {

                as.remove(count);
                as.add(a);
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
