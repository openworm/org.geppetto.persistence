package org.geppetto.persistence.db.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String login;
	
	private String name;
	
	@Join
	private List<GeppettoProject> geppettoProjects;
	
	// TODO: do we store an encrypted password here?
	public User(String login, String name, List<GeppettoProject> geppettoProjects) {
		super();
		this.login = login;
		this.name = name;
		this.geppettoProjects = geppettoProjects;
	}

	public long getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<GeppettoProject> getGeppettoProjects() {
		return geppettoProjects;
	}

	public void setGeppettoProjects(List<GeppettoProject> geppettoProjects) {
		this.geppettoProjects = geppettoProjects;
	}


}