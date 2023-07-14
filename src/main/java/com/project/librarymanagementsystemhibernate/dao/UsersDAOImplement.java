package com.capgemini.librarymanagementsystemhibernate.dao;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.capgemini.librarymanagementsystemhibernate.dto.BookBean;
import com.capgemini.librarymanagementsystemhibernate.dto.BookIssueDetails;
import com.capgemini.librarymanagementsystemhibernate.dto.BorrowedBooks;
import com.capgemini.librarymanagementsystemhibernate.dto.RequestDetails;
import com.capgemini.librarymanagementsystemhibernate.dto.UsersBean;
import com.capgemini.librarymanagementsystemhibernate.exception.LMSException;

public class UsersDAOImplement implements UsersDAO{
	EntityManagerFactory factory = null;
	EntityManager manager = null;
	EntityTransaction transaction = null;
	int noOfBooks;

	@Override
	public boolean register(UsersBean user) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			transaction = manager.getTransaction();
			transaction.begin();
			/*
			String jpql = "insert into UsersBean (uId,firstName,lastName,email,password,mobile,role) values (:uId,:firstName,:lastName,:email,:password,:mobile,:role)";
			Query query = manager.createQuery(jpql);
			query.setParameter("uId",user.getUId());
			query.setParameter("firstName",user.getFirstName());
			query.setParameter("lastName",user.getLastName());
			query.setParameter("email", user.getEmail());
			query.setParameter("password", user.getPassword());
			query.setParameter("mobile", user.getMobile());
			query.setParameter("role", user.getRole());
			int count = query.executeUpdate();
			*/
			manager.persist(user);
			transaction.commit();
			/*
			if(user.getEmail().isEmpty() && count==0) {
				return false;
			} else {
				return true;
			}
			*/
			return true;
		}catch (Exception e) {
			System.err.println(e.getMessage());
			transaction.rollback();
			return false;
		}finally {
			manager.close();
			factory.close();
		}
	}

	@Override
	public UsersBean login(String email, String password) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			String jpql="select u from UsersBean u where u.email=:email and u.password=:password";
			TypedQuery<UsersBean> query = manager.createQuery(jpql,UsersBean.class);
			query.setParameter("email", email);
			query.setParameter("password", password);
			UsersBean bean = query.getSingleResult();
			return bean;
		}catch(Exception e){
			System.err.println(e.getMessage());
			return null;
		}finally {
			manager.close();
			factory.close();
		}
	}

	@Override
	public boolean addBook(BookBean book) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			transaction = manager.getTransaction();
			transaction.begin();
			String jpql = "insert into BookBean (bId,bookName,author,category,publisher) values (:bId,:bookName,:author,:category,:publisher)";
			Query query = manager.createNativeQuery(jpql);
			query.setParameter("bId",book.getBId());
			query.setParameter("bookName",book.getBookName());
			query.setParameter("author",book.getAuthor());
			query.setParameter("category", book.getCategory());
			query.setParameter("publisher", book.getPublisher());
			int count = query.executeUpdate();
			transaction.commit();
			if(count!=0) {
				return true;
			} else {
				return false;
			}
		}catch (Exception e) {
			System.err.println(e.getMessage());
			transaction.rollback();
			return false;
		}finally {
			manager.close();
			factory.close();
		}
	}

	@Override
	public boolean removeBook(int bId) {
		try(FileInputStream info = new FileInputStream("db.properties");) {
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			transaction = manager.getTransaction();
			transaction.begin();
			UsersBean record = manager.find(UsersBean.class,bId);
			manager.remove(record);
			transaction.commit();
			return true;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			transaction.rollback();
			return false;
		} finally {
			manager.close();
			factory.close();
		}
	}

	@Override
	public boolean updateBook(BookBean book) {
		try(FileInputStream info = new FileInputStream("db.properties");) {
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			transaction = manager.getTransaction();
			transaction.begin();
			BookBean record = manager.find(BookBean.class, book.getBId());
			record.setBookName(book.getBookName());
			transaction.commit();
			return true;
		}catch (Exception e) {
			System.err.println(e.getMessage());
			transaction.rollback();
			return false;
		}finally {
			manager.close();
			factory.close();
		}
	}

	@Override 
	public boolean issueBook(int bId, int uId) {
		try(FileInputStream info = new FileInputStream("db.properties");) {
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			transaction = manager.getTransaction();
			String jpql = "select b from BookBean b where b.bId=:bId";
			TypedQuery<BookBean> query = manager.createQuery(jpql,BookBean.class);
			query.setParameter("bId", bId);
			BookBean rs = query.getSingleResult();
			if(rs != null) {
				String jpql1 = "select r from RequestDetails r join r.users u on r.uId=u.uId join r.books b on r.bId=b.bId where u.uId=:uId and b.bId=:bId";
				TypedQuery<RequestDetails> query1 = manager.createQuery(jpql1,RequestDetails.class);
				query1.setParameter("uId", uId);
				query1.setParameter("bId", bId);
				query1.setParameter("userId", uId);
				RequestDetails rs1 = query1.getSingleResult();
				if(rs1 != null) {
					transaction.begin();
					String jpql2 = "insert into BookIssueDetails(bId,uId,issueDate,returnDate) values(:bId,:uId,:issueDate,:returnDate)";
					Query query2 = manager.createNativeQuery(jpql2);
					query2.setParameter("bId", bId);
					query2.setParameter("uId", uId);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
					Calendar cal = Calendar.getInstance();
					String issueDate = sdf.format(cal.getTime());
					query2.setParameter("issueDate", java.sql.Date.valueOf(issueDate));
					cal.add(Calendar.DAY_OF_MONTH, 7);
					String returnDate = sdf.format(cal.getTime());
					query2.setParameter("returnDate", java.sql.Date.valueOf(returnDate));
					int count = query2.executeUpdate();
					transaction.commit();
					if(count != 0) {
						transaction.begin();
						String jpql3 = "Insert into BorrowedBooks(bId,uId,email) values(:bId,:uId,(select u.email from UsersBean u where u.uId=:userId))";
						Query query3 = manager.createNativeQuery(jpql3);
						query3.setParameter("bId", bId);
						query3.setParameter("uId", uId);
						query3.setParameter("userId", uId);
						int count1 = query3.executeUpdate();
						transaction.commit();
						if(count1 != 0) {
							return true;
						} else {
							return false;
						}
					}else {
						throw new LMSException("Book Not issued");
					}
				}else {
					throw new LMSException("The respective user have not placed any request");
				}
			}else {
				throw new LMSException("There is no book exist with bookId"+bId);
			}
		}catch (Exception e) {
			System.err.println(e.getMessage());
			transaction.rollback();
			return false;
		}finally {
			manager.close();
			factory.close();
		}
	}

	@Override
	public boolean request(int uId, int bId) {
		int count=0;
		try(FileInputStream info = new FileInputStream("db.properties");) {
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			transaction = manager.getTransaction();
			String jpql = "select b from BookBean b where b.bId=:bId";
			TypedQuery<BookBean> query = manager.createQuery(jpql,BookBean.class);
			query.setParameter("bId", bId);
			BookBean rs = query.getSingleResult();
			if(rs != null) {
				String jpql1 = "select b from BorrowedBooks b join b.users u on u.uId=b.uId join b.books bk on bk.bId=b.bId ";
				TypedQuery<BorrowedBooks> query1 = manager.createQuery(jpql1,BorrowedBooks.class);
				query1.setParameter("uId", uId);
				query1.setParameter("bId", bId);
				query1.setParameter("userId", uId);
				BorrowedBooks rs1 = query1.getSingleResult();
				if(rs1 == null) {
					String jpql2 = "select b from BookIssueDetails b join b.users u on u.uId=b.uId where u.uId=:uId";
					TypedQuery<BookIssueDetails> query2 = manager.createQuery(jpql2,BookIssueDetails.class);
					query2.setParameter("uId", uId);
					List<BookIssueDetails> rs2 = query2.getResultList();
					for(BookIssueDetails p : rs2) {
						noOfBooks = count++;
					}
					if(noOfBooks<3) {
						transaction.begin();
						String jpql3 = "insert into RequestDetails(uId,fullName,bId,bookName,email) values(:uId,(select u.firstName from UsersBean u where u.uId=:userId),:bId,"
								+ "(select b.bookName from BookBean b where b.bId=:bookId),(select u.email from UsersBean where u.uId=:user_Id))" ;
						Query query3 = manager.createNativeQuery(jpql3);
						query3.setParameter("uId", uId);
						query3.setParameter("userId", uId);
						query3.setParameter("bId", bId);
						query3.setParameter("bookId", bId);
						query3.setParameter("user_Id", uId);
						int count1 = query3.executeUpdate();
						transaction.commit();
						if(count1 != 0) {
							return true;
						}else {
							return false;
						}

					}else {
						throw new LMSException("You have crossed the book limit");
					}
				}else {
					throw new LMSException("You have already borrowed the requested book");
				}
			}else {
				throw new LMSException("The book with requested id is not present");
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
			transaction.rollback();
			return false;
		} finally {
			manager.close();
			factory.close();
		}
	}


	@Override
	public List<BorrowedBooks> borrowedBook(int uId) {
		try(FileInputStream info = new FileInputStream("db.properties");) {
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			String jpql = "select b from BorrowedBooks b where b.uId=:uId";
			TypedQuery<BorrowedBooks> query = manager.createQuery(jpql,BorrowedBooks.class);
			query.setParameter("uId", uId);
			List<BorrowedBooks> recordList = query.getResultList();
			return recordList; 
		}catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}finally {
			manager.close();
			factory.close();
		}
	}

	@Override
	public List<BookBean> searchBookById(int bId) {
		try(FileInputStream info = new FileInputStream("db.properties");) {
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			String jpql = "select b from BookBean b where b.bId=:bId";
			TypedQuery<BookBean> query = manager.createQuery(jpql,BookBean.class);
			query.setParameter("bId", bId);
			List<BookBean> recordList = query.getResultList();
			return recordList; 
		}catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}finally {
			manager.close();
			factory.close();
		}
	}

	@Override
	public List<BookBean> searchBookByTitle(String bookName) {
		try(FileInputStream info = new FileInputStream("db.properties");) {
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			String jpql = "select b from BookBean b where b.bookName=:bookName";
			TypedQuery<BookBean> query = manager.createQuery(jpql,BookBean.class);
			query.setParameter("bookName", bookName);
			List<BookBean> recordList = query.getResultList();
			return recordList; 
		}catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}finally {
			manager.close();
			factory.close();
		}
	}

	@Override
	public List<BookBean> searchBookByAuthor(String author) {
		try(FileInputStream info = new FileInputStream("db.properties");) {
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			String jpql = "select b from BookBean b where b.author=:author";
			TypedQuery<BookBean> query = manager.createQuery(jpql,BookBean.class);
			query.setParameter("author", author);
			List<BookBean> recordList = query.getResultList();
			return recordList; 
		}catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}finally {
			manager.close();
			factory.close();
		}
	}

	@Override
	public List<BookBean> getBooksInfo() {
		factory = Persistence.createEntityManagerFactory("TestPersistence");
		manager = factory.createEntityManager();
		String jpql = "select b from BookBean b";
		TypedQuery<BookBean> query = manager.createQuery(jpql,BookBean.class);
		List<BookBean> recordList = query.getResultList();
		manager.close();
		factory.close();
		return recordList;
	}

	@Override
	public boolean returnBook(int bId, int uId, String status) {
		try(FileInputStream info = new FileInputStream("db.properties");){
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			transaction = manager.getTransaction();
			String jpql = "select b from BookBean b where b.bId=:bId";
			TypedQuery<BookBean> query = manager.createQuery(jpql,BookBean.class);
			query.setParameter("bId", bId);
			BookBean rs = query.getSingleResult();
			if(rs != null) {
				String jpql1 = "select b from BookIssueDetails b where b.bId=:bId and b.uId=uId ";
				TypedQuery<BookIssueDetails> query1 = manager.createQuery(jpql,BookIssueDetails.class);
				query1.setParameter("bId", bId);
				query1.setParameter("uId", uId);
				BookIssueDetails rs1 = query1.getSingleResult();
				if(rs1 != null) {
					Date issueDate = rs1.getIssueDate();
					Calendar cal = Calendar.getInstance();
					Date returnDate = cal.getTime();
					long difference = issueDate.getTime() - returnDate.getTime();
					float daysBetween = (difference / (1000*60*60*24));
					if(daysBetween>7) {
						float fine = daysBetween*5;
						System.out.println("The user has to pay the fine of the respective book of Rs:"+fine);
						if(status=="yes") {
							transaction.begin();
							String jpql2 = "delete from BookIssueDetails b where b.bId=:bId and u.uId=:uId";
							Query query2 = manager.createNativeQuery(jpql2);
							query2.setParameter("bId", bId);
							query2.setParameter("uId", uId);
							int count1 = query2.executeUpdate();
							transaction.commit();
							if(count1 != 0) {
								transaction.begin();
								String jpql3 = "delete from BorrowedBooks b where b.bId=:bId and b.uId=:uId";
								Query query3 = manager.createNativeQuery(jpql3);
								query3.setParameter("bId", bId);
								query3.setParameter("uId", uId);
								int count2 = query3.executeUpdate();
								transaction.commit();
								if(count2 != 0) {
									transaction.begin();
									String jpql4 = "delete from BorrowedBooks b where b.bId=:bId and b.uId=:uId";
									Query query4 = manager.createNativeQuery(jpql4);
									query4.setParameter("bId", bId);
									query4.setParameter("uId", uId);
									int count3 = query4.executeUpdate();
									transaction.commit();
									if(count3 != 0) {
										return true;
									}else {
										return false;
									}
								}else {
									return false;
								}

							}else {
								return false;
							}

						}else {
							throw new LMSException("The User has to pay fine for delaying book return");
						}
					}else {
						transaction.begin();
						String jpql2 = "delete from BookIssueDetails b where b.bId=:bId and u.uId=:uId";
						Query query2 = manager.createNativeQuery(jpql2);
						query2.setParameter("bId", bId);
						query2.setParameter("uId", uId);
						int count1 = query2.executeUpdate();
						transaction.commit();
						if(count1 != 0) {
							transaction.begin();
							String jpql3 = "delete from BorrowedBooks b where b.bId=:bId and b.uId=:uId";
							Query query3 = manager.createNativeQuery(jpql3);
							query3.setParameter("bId", bId);
							query3.setParameter("uId", uId);
							int count2 = query3.executeUpdate();
							transaction.commit();
							if(count2 != 0) {
								transaction.begin();
								String jpql4 = "delete from BorrowedBooks b where b.bId=:bId and b.uId=:uId";
								Query query4 = manager.createNativeQuery(jpql4);
								query4.setParameter("bId", bId);
								query4.setParameter("uId", uId);
								int count3 = query4.executeUpdate();
								transaction.commit();
								if(count3 != 0) {
									return true;
								}else {
									return false;
								}
							}else {
								return false;
							}

						}else {
							return false;
						}
					}

				}else {
					throw new LMSException("This respective user hasn't borrowed any book");
				}
			}else {
				throw new LMSException("book doesnt exist");
			}

		}catch (Exception e) {
			System.err.println(e.getMessage());
			transaction.rollback();
			return false;
		}finally {
			manager.close();
			factory.close();
		}
	}

	@Override
	public List<Integer> bookHistoryDetails(int uId) {
		int count=0;
		factory = Persistence.createEntityManagerFactory("TestPersistence");
		manager = factory.createEntityManager();
		String jpql = "select b from BookIssueDetails b";
		TypedQuery<BookIssueDetails> query = manager.createQuery(jpql,BookIssueDetails.class);
		List<BookIssueDetails> recordList = query.getResultList();
		for(BookIssueDetails p : recordList) {
			noOfBooks = count++;
		}
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.add(noOfBooks);
		manager.close();
		factory.close();
		return list;
	}

	@Override
	public List<RequestDetails> showRequests() {
		factory = Persistence.createEntityManagerFactory("TestPersistence");
		manager = factory.createEntityManager();
		String jpql = "select r from RequestDetails r";
		TypedQuery<RequestDetails> query = manager.createQuery(jpql,RequestDetails.class);
		List<RequestDetails> recordList = query.getResultList();
		manager.close();
		factory.close();
		return recordList;
	}

	@Override
	public List<BookIssueDetails> showIssuedBooks() {
		factory = Persistence.createEntityManagerFactory("TestPersistence");
		manager = factory.createEntityManager();
		String jpql = "select b from BookIssueDetails b";
		TypedQuery<BookIssueDetails> query = manager.createQuery(jpql,BookIssueDetails.class);
		List<BookIssueDetails> recordList = query.getResultList();
		manager.close();
		factory.close();
		return recordList;
	}

	@Override
	public List<UsersBean> showUsers() {
		factory = Persistence.createEntityManagerFactory("TestPersistence");
		manager = factory.createEntityManager();
		String jpql = "select u from UsersBean u";
		TypedQuery<UsersBean> query = manager.createQuery(jpql,UsersBean.class);
		List<UsersBean> recordList = query.getResultList();
		manager.close();
		factory.close();
		return recordList;
	}

	@Override
	public boolean updatePassword(String email, String password, String newPassword, String role) {
		try(FileInputStream info = new FileInputStream("db.properties");) {
			Properties pro = new Properties();
			pro.load(info);
			factory = Persistence.createEntityManagerFactory("TestPersistence");
			manager = factory.createEntityManager();
			transaction = manager.getTransaction();
			transaction.begin();
			String jpql = "select u from UsersBean where u.email=:email and u.role=:role and u.password=:password";
			TypedQuery<UsersBean> query = manager.createQuery(jpql,UsersBean.class);
			query.setParameter("email", email);
			query.setParameter("role", role);
			query.setParameter("password", password);
			UsersBean rs = query.getSingleResult();
			if(rs != null) {
				UsersBean record = manager.find(UsersBean.class,email);
				record.setPassword(newPassword);
				transaction.commit();
				return true;			
			}else {
				throw new LMSException("User doesnt exist");
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
			transaction.rollback();
			return false;
		} finally {
			manager.close();
			factory.close();
		}
	}

}
