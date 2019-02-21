package com.web.cloudapp.Repository;

import com.web.cloudapp.model.Attachment;
import com.web.cloudapp.model.Note;

import java.util.List;

public interface AttachmentCustom{

    List<Attachment> getAllAttachments(Note note);

    Attachment getAttachment(String id);

}
