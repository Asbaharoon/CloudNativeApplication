package com.web.cloudapp.Repository;

import com.web.cloudapp.model.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends CrudRepository<Note,String> , NoteRepositoryCustom {

}
