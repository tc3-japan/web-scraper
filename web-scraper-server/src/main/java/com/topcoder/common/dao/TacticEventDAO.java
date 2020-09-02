package com.topcoder.common.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tactic_event")
@Data
public class TacticEventDAO {
    /**
     * the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * contents
     */
    @JsonIgnore
    @Column(name = "contents", columnDefinition = "MEDIUMTEXT")
    private String contents;


    /**
     * the status
     */
    @Column(name = "status")
    private String status;


    /**
     * update at time
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    private Date createAt;

    /**
     * finish at time
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finish_at")
    private Date finishAt;

}
