package com.web.cloudapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "attachmenttable",schema="modelinfo")
public class Attachment {

    @Id
    @Column(name = "attachment_id")
    private String aid;

    @Column(name = "url")
    private String url;

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

}