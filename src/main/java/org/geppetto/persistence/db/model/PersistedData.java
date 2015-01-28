package org.geppetto.persistence.db.model;

import java.io.Serializable;

import javax.jdo.FetchGroup;
import javax.jdo.annotations.FetchPlan;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
@FetchPlan(name="mainPlan", maxFetchDepth=5, fetchSize=1000, fetchGroups={FetchGroup.ALL, FetchGroup.DEFAULT})
public class PersistedData implements Serializable {
	private static final long serialVersionUID = 1;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String url;
	
	private PersistedDataType type;

	public PersistedData(String url, PersistedDataType type) {
		super();
		this.url = url;
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public PersistedDataType getType() {
		return type;
	}

	public void setType(PersistedDataType type) {
		this.type = type;
	}

	
}