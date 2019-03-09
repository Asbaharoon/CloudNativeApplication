package com.web.cloudapp.service;

import com.web.cloudapp.Exception.BadRequest;
import com.web.cloudapp.Exception.EmptyField;
import com.web.cloudapp.Exception.ResourceNotFound;
import com.web.cloudapp.Exception.Unauthorized;
import com.web.cloudapp.Repository.NoteRepository;
import com.web.cloudapp.model.Note;
import com.web.cloudapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserService userService;

    //Get a Note given note Id
    public Note getNote(String id){
        Note n = noteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("note","id",id));
        User user =userService.getUserName();
        if(checkNote(n,user)) return n;
        else return null;
    }

    //Update a note
    public boolean updateNote(String id, Note uNote){
        Note n = getNote(id);
        if(checkNote(uNote)){
            n.setTitle(uNote.getTitle());
            n.setContent(uNote.getContent());
            noteRepository.save(n);
            return true;
        }
    return false;}

    //Create a new Note
    public boolean createNote(Note n){
        if(checkNote(n)) {
            User u = userService.getUserName();
            n.setUserData(u);
            noteRepository.save(n);
            return true;
        }
        return false;
    }

    //Delete a Note
    public boolean deleteNote(String id ){
        Note n;
        try {
             n = getNote(id);
        }catch(Exception ex){
            throw new BadRequest("Note doesn't exists");
        }
        if(n!=null){
            noteRepository.delete(n);
            return true;
        }
        return false;
    }

    //Get all the notes of a User
    public List<Note> getAllNotes(){
        User u = userService.getUserName();
        return noteRepository.getAll(u);
    }

    //User validation for note
    public boolean checkNote(Note n, User user) throws RuntimeException{

        if(!n.getUserData().equals(user)) throw new Unauthorized("note","access");
        else return true;
    }

    //Fields validation for note
    public boolean checkNote(Note n) throws RuntimeException{
        if (n.getTitle() == null || n.getTitle().equals("")) throw new EmptyField("title");
        else return true;
    }
}
