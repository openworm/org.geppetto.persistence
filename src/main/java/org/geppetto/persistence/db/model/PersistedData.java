

package org.geppetto.persistence.db.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.IPersistedData;
import org.geppetto.core.data.model.PersistedDataType;

@PersistenceCapable(detachable = "true")
public class PersistedData implements Serializable, IPersistedData
{
	private static final long serialVersionUID = 1;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String url;

	private PersistedDataType type;

	public PersistedData(String url, PersistedDataType type)
	{
		super();
		this.url = url;
		this.type = type;
	}

	public long getId()
	{
		return id;
	}

	public String getUrl()
	{
		return url;
	}

	public PersistedDataType getType()
	{
		return type;
	}

	public void setType(PersistedDataType type)
	{
		this.type = type;
	}

	@Override
	public void setId(long id)
	{
		this.id = id;
	}

	@Override
	public void setURL(String url)
	{
		this.url = url;
	}

}