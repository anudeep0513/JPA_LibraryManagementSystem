package com.capgemini.librarymanagementsystemhibernate.dto;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="bookbean")
public class BookBean {
	@Id
	@Column
	private int bId;
	@Column
	private String bookName;
	@Column
	private String author;
	@Column
	private String category;
	@Column
	private String publisher;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "books")
	private List<BookIssueDetails> issueDetails;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "books")
	private List<RequestDetails> requests;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "books")
	private List<BorrowedBooks> borrowed;
}
