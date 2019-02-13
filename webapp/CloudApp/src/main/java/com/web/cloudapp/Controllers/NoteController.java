package com.web.cloudapp.Controllers;


import com.web.cloudapp.Repository.NoteRepository;
import com.web.cloudapp.model.Note;
import com.web.cloudapp.model.User;
import com.web.cloudapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserService service;

    @RequestMapping(value = "/note", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity addNote(@RequestBody Note note) {
        Map<String,String> map = new HashMap<String, String>();
        if(note.getContent()==null && note.getTitle()==null){
            map.put("message", "Note title / note content cannot be blank");
            map.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        Note n = new Note();
        User user = service.getUserName();

        n.setContent(note.getContent());
        n.setTitle(note.getTitle());
        n.setUserData(user);
        noteRepository.save(n);

        map.put("message", "Note created successfully");
        map.put("status", HttpStatus.CREATED.toString());
        return new ResponseEntity(map,HttpStatus.CREATED);
    }

    @RequestMapping(value ="/note")
    public @ResponseBody ResponseEntity getAllNote(){
        List<Note> notes = new ArrayList<>();
        User user =service.getUserName();
        notes =noteRepository.getAllNotes(user);
        return new ResponseEntity(notes,HttpStatus.OK);
    }

    @RequestMapping(value ="/note/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getNote(@PathVariable(value = "id") String id){
        Note note = new Note();
        User user = service.getUserName();
        note = noteRepository.getNote(user,id);
        if(note == null ){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        else if(user.getUserName().equals(note.getUserData().getUserName())){
            return new ResponseEntity(note,HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
    }
}