package org.geppetto.persistence.db.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Simulation implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String name;

	private Date launchedTimestamp;

	private String url;

	private String status;

	public Simulation(String name, Date timestamp, String url, String status) {
		super();
		this.name = name;
		launchedTimestamp = timestamp;
		this.url = url;
		this.status = status;
	}

	public long getId() {
		return id;
	}

//	public void setId(long id) {
//		this.id = id;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLaunchedTimestamp() {
		return launchedTimestamp;
	}

	public void setLaunchedTimestamp(Date timestamp) {
		this.launchedTimestamp = timestamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}