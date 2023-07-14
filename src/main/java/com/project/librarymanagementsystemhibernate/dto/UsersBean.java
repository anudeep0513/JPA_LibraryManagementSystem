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
@Table(name="users")
public class UsersBean {
	@Id
	@Column
	private int uId;
	@Column
	private String firstName;
	@Column
	private String lastName;
	@Column
	private String email;
	@Column
	private String password;
	@Column
	private long mobile;
	@Column
	private String role;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "users")
	private List<BookIssueDetails> issueDetails;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "users")
	private List<RequestDetails> requests;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "users")
	private List<BorrowedBooks> borrowed;

}
