package com.web.cloudapp.Repository;

import com.web.cloudapp.model.Attachment;
import com.web.cloudapp.model.Note;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class AttachmentRepositoryImpl implements AttachmentCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Attachment> getAllAttachments(Note note) {

        Query query = entityManager.createNativeQuery("SELECT * FROM modelinfo.attachmenttable as a WHERE a.id LIKE ? ", Attachment.class);
        query.setParameter(1, note);
        return query.getResultList();
    }


    public Attachment getAttachment(String id){
        Query query = entityManager.createNativeQuery("SELECT * FROM modelinfo.attachmenttable as a WHERE a.attachment_id = ? ", Attachment.class);
        query.setParameter(1,id);
        if(query.getResultList().isEmpty()){
            return null;
        }
        Attachment a = (Attachment) query.getSingleResult();
        return a;
    }
    public Attachment deleteAttachment(String id){
        Query query = entityManager.createNativeQuery("DELETE FROM modelinfo.attachmenttable WHERE attachment_id = ? ", Attachment.class);
        query.setParameter(1,id);
        query.executeUpdate();
        if(query.getResultList().isEmpty()){
            return null;
        }
        Attachment a = (Attachment) query.getSingleResult();
        return a;
    }

}
