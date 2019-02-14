package com.web.cloudapp.Controllers;


import com.web.cloudapp.Repository.NoteRepository;
import com.web.cloudapp.model.Note;
import com.web.cloudapp.model.User;
import com.web.cloudapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;

@RestController
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserService service;

    //Post
    @RequestMapping(value = "/note", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity addNote(@RequestBody Note note) {
        Map<String,String> map = new HashMap<String, String>();
        if(note.getContent()==null && note.getTitle()==null){
            map.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        else if(note.getTitle().equals("")){
            map.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        User user = service.getUserName();
        note.setUserData(user);
        noteRepository.save(note);
        return new ResponseEntity(note,HttpStatus.CREATED);
    }

    //GetAll
    @RequestMapping(value ="/note")
    public @ResponseBody ResponseEntity getAllNote(){
        List<Note> notes = new ArrayList<>();
        User user =service.getUserName();
        notes =noteRepository.getAllNotes(user);
        return new ResponseEntity(notes,HttpStatus.OK);
    }

    //GetOne
    @RequestMapping(value ="/note/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getNote(@PathVariable(value = "id") String id){
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
            return new ResponseEntity(note,HttpStatus.OK);
        }else{
            map.put("status",HttpStatus.UNAUTHORIZED.toString());
            return new ResponseEntity(map,HttpStatus.UNAUTHORIZED);
        }
    }

    //Delete
    @Transactional
    @RequestMapping(value="/note/{id}", method = RequestMethod.DELETE)
    public  @ResponseBody ResponseEntity deleteNote(@PathVariable(value = "id") String id){
        Note note;
        User user = service.getUserName();
        Map<String,String> map = new HashMap<String, String>();

        note = noteRepository.getNote(id);
        if(note == null ){
            map.put("status",HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        else if(!user.getUserName().equals(note.getUserData().getUserName())){
            map.put("status",HttpStatus.UNAUTHORIZED.toString());
            return new ResponseEntity(map,HttpStatus.UNAUTHORIZED);
        }
        else {
            noteRepository.delete(note);
            map.put("status",HttpStatus.NO_CONTENT.toString());
            return new ResponseEntity(map,HttpStatus.NO_CONTENT);
        }
    }

    //Update
    @PutMapping("/note/{id}")
    public @ResponseBody ResponseEntity updateNote(@RequestBody Note note, @PathVariable(value ="id") String noteId) {
        Map<String,String> map = new HashMap<>();
        User user = service.getUserName();
        Note n;
        if(noteRepository.findById(noteId).isPresent()) n=noteRepository.findById(noteId).get();
        else n=null;
        if(note.getTitle()==null || note.getContent()==null || n==null){
            map.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        else if(n.getUserData()!=user){
            map.put("status", HttpStatus.UNAUTHORIZED.toString());
            return new ResponseEntity(map,HttpStatus.UNAUTHORIZED);
        }
        if(note.getTitle().equals("")){
            map.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        else {
            n.setTitle(note.getTitle());
            n.setContent(note.getContent());
            noteRepository.save(n);
            map.put("status",HttpStatus.NO_CONTENT.toString());
            return new ResponseEntity(map,HttpStatus.NO_CONTENT);
        }
    }
}