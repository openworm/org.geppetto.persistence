

package org.geppetto.persistence.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.IUser;
import org.geppetto.core.data.model.IUserGroup;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PersistenceCapable(detachable = "true")
public class User implements Serializable, IUser
{

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String login;

	@JsonIgnore
	private String password;

	private String name;

	@Persistent(defaultFetchGroup = "true")
	private UserGroup userGroup;
	
	@Join
	@JsonIgnore
	@Persistent(defaultFetchGroup = "true")
	private List<GeppettoProject> geppettoProjects;

	@JsonIgnore
	private String dropboxToken;

	@Join
	@Persistent(defaultFetchGroup = "true")
	private List<Date> loginTimeStamps = new ArrayList<Date>();

	public User(String login, String password, String name, List<GeppettoProject> geppettoProjects, IUserGroup group)
	{
		super();
		this.login = login;
		this.password = password;
		this.name = name;
		this.geppettoProjects = geppettoProjects;
		this.userGroup = (UserGroup)group;
	}

	public long getId()
	{
		return id;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<GeppettoProject> getGeppettoProjects()
	{
		return geppettoProjects;
	}

	public void setGeppettoProjects(List<GeppettoProject> geppettoProjects)
	{
		this.geppettoProjects = geppettoProjects;
	}

	@Override
	public String getDropboxToken()
	{
		return this.dropboxToken;
	}

	@Override
	public void setDropboxToken(String token)
	{
		this.dropboxToken = token;
	}

	@Override
	public IUserGroup getUserGroup() 
	{
		return this.userGroup;
	}
	
	public void setUserGroup(UserGroup group)
	{
		this.userGroup = group;
	}


	@Override
	public List<Date> getLoginTimeStamps() {
		return this.loginTimeStamps;
	}

	@Override
	public void addLoginTimeStamp(Date date) {
		this.loginTimeStamps.add(date);		
	}
}