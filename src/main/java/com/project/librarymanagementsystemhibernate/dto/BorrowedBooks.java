package com.capgemini.librarymanagementsystemhibernate.dto;

import java.io.Serializable;

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
@Table(name="borrowed_books")
public class BorrowedBooks implements Serializable{
	@EmbeddedId
	private CompositePK compositePK;
	@Column
	private String email;
	
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
