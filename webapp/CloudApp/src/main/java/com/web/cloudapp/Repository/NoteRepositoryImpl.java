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
}