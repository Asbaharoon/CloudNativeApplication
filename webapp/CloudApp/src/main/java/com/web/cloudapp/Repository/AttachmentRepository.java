package com.web.cloudapp.Repository;

import com.web.cloudapp.model.Attachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface AttachmentRepository extends CrudRepository<Attachment,String> {

}
