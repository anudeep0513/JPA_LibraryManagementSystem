package com.capgemini.librarymanagementsystemhibernate.dto;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;

@Data
@Entity
@Table(name="request_details")
public class RequestDetails {
	@EmbeddedId
	private CompositePK compositePK;
	@Column
	private String fullName;
	@Column
	private String bookName;
	
	@Exclude
	@MapsId("bId")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="bId")
	private BookBean books;
	
	@Exclude
	@MapsId("uId")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="uId")
	private UsersBean users;
}
