package com.web.cloudapp.Repository;

import com.web.cloudapp.model.Attachment;
import org.springframework.data.repository.CrudRepository;

public interface AttachmentRepository extends CrudRepository<Attachment,String> ,AttachmentCustom{
}
