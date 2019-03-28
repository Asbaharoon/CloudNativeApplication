package com.web.cloudapp.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "attachmenttable")
public class Attachment {

    @Id
    @Column(name = "attachment_id")
    private String aid;

    @Column(name = "url")
    private String url;

    @Column(name = "attachment_type")
    private String att_type;

    @Column(name = "attachment_size")
    private Long size;

    public Attachment(){
        aid= UUID.randomUUID().toString();
    }

    public String getId() {
        return aid;
    }

    public void setId(String aid) {
        this.aid = aid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAtt_type() {
        return att_type;
    }

    public void setAtt_type(String att_type) {
        this.att_type = att_type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return url;

        
    }
}