package com.topcoder.common.dao;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "request_event")
@Data
public class RequestEventDAO {
  /**
   * the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;


  /**
   * the tactic event id
   */
  @Column(name = "tactic_event_id")
  private int tacticEventId;

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
