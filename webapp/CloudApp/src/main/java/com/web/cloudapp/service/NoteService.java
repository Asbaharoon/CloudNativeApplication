package com.web.cloudapp.service;

import com.web.cloudapp.Exception.BadRequest;
import com.web.cloudapp.Exception.EmptyField;
import com.web.cloudapp.Exception.ResourceNotFound;
import com.web.cloudapp.Exception.Unauthorized;
import com.web.cloudapp.Repository.NoteRepository;
import com.web.cloudapp.model.Attachment;
import com.web.cloudapp.model.Note;
import com.web.cloudapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private LogService logService;


    //Get a Note given note Id
    public Note getNote(String id){
        try {
            Note n = noteRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFound("note", "id", id));
            User user = userService.getUserName();
            if (checkNote(n, user)) return n;
            else return null;
        }catch (Exception ex){
            logService.logger.warning(ex.getMessage());
            throw ex;
        }
    }

    //Update a note
    public boolean updateNote(String id, Note uNote) {
        try {
            Note n = getNote(id);
            if (checkNote(uNote)) {
                n.setTitle(uNote.getTitle());
                n.setContent(uNote.getContent());
                noteRepository.save(n);
                logService.logger.info("Note updated successfully");
                return true;
            }
            return false;
        }catch (Exception ex){
            logService.logger.warning(ex.getMessage());
            throw ex;
        }
    }

    //Create a new Note
    public boolean createNote(Note n){
        try {
            if (checkNote(n)) {
                User u = userService.getUserName();
                n.setUserData(u);
                noteRepository.save(n);
                logService.logger.info("Note created successfully");
                return true;
            }
            return false;
        }catch (Exception ex){
            logService.logger.warning(ex.getMessage());
            throw ex;
        }
    }

    //Delete a Note
    public boolean deleteNote(String id ) throws Exception{
        Note n;
        try {
            try {
                n = getNote(id);
            } catch (Exception ex) {
                throw new BadRequest("Note doesn't exists");
            }
            if (n != null) {

                List<Attachment> attachments = attachmentService.getAllAttachments(n.getId());
                if (attachments != null) {
                    for (Attachment a : attachments) {
                        attachmentService.deleteFile(a);
                    }
                    noteRepository.delete(n);
                    logService.logger.info("Note deleted successfully");
                    return true;
                } else {
                    noteRepository.delete(n);
                    logService.logger.info("Note deleted successfully");
                    return true;
                }
            }
            return false;
        }catch (Exception ex){
            logService.logger.warning(ex.getMessage());
            throw ex;
        }
    }

    //Get all the notes of a User
    public List<Note> getAllNotes(){
        User u = userService.getUserName();
        return noteRepository.getAll(u);
    }

    //User validation for note
    public boolean checkNote(Note n, User user) throws RuntimeException{
    try {
        if (!n.getUserData().equals(user)) throw new Unauthorized("note", "access");
        else return true;
    }catch (Exception ex){
        logService.logger.warning(ex.getMessage());
        throw ex;
    }
    }

    //Fields validation for note
    public boolean checkNote(Note n) throws RuntimeException{
    try {
        if (n.getTitle() == null || n.getTitle().equals("")) throw new EmptyField("title");
        else return true;
    }catch (Exception ex){
        logService.logger.warning(ex.getMessage());
        throw ex;
    }
    }
}
