package com.web.cloudapp.Repository;

import com.web.cloudapp.model.Note;
import com.web.cloudapp.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class NoteRepositoryImpl implements NoteRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Note> getAllNotes(User user) {

        Query query = entityManager.createNativeQuery("SELECT * FROM modelinfo.notetable as n WHERE n.user_name LIKE ? ", Note.class);
        query.setParameter(1, user);
        return query.getResultList();

    }

    @Override
    public Note getNote(User user, String id){
        Query query = entityManager.createNativeQuery("SELECT * FROM modelinfo.notetable as n WHERE n.id = ? ", Note.class);
        query.setParameter(1,id);
        if(query.getResultList().isEmpty()){
            return null;
        }
        Note n = (Note) query.getSingleResult();
        System.out.println(n.getTitle());
        return n;
    }

    @Override
    public String deleteNote(User user, String id) {

        Query query = entityManager.createNativeQuery("DELETE FROM modelinfo.notetable WHERE id = ? ", Note.class);
        query.setParameter(1, id);
        int a = query.executeUpdate();
        if (a == 0) {
            return null;
        }
        System.out.println("Deleted Successfully");
        return "deleted";
    }
}