package org.geppetto.persistence.db.model;

import java.io.Serializable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.IView;

@PersistenceCapable(detachable = "true")
public class View implements Serializable, IView {

	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Column(length = 21000, jdbcType="CLOB")
	private String viewStates;
	
	public View()
	{
		super();
		this.viewStates = "";
	}
	
	public View(String view)
	{
		super();
		this.viewStates = view;
	}
	
	@Override
	public String getView() {
		return this.viewStates;
	}

	@Override
	public void setView(String view) {
		this.viewStates = view;
	}
	
	@Override
	public long getId() {
		return this.id;
	}

}
