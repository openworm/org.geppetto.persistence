package org.geppetto.persistence.db.model;

import java.io.Serializable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.IView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@PersistenceCapable(detachable = "true")
public class View implements Serializable, IView {

	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	//@Column(length = 10000)
	private JsonObject viewStates;
	
	public View()
	{
		super();
		this.viewStates = new JsonObject();
	}
	
	public View(String view)
	{
		super();
		JsonParser parser = new JsonParser();
		if(view !=null){
			JsonObject o = parser.parse(view).getAsJsonObject();
			this.viewStates = o;
		}
		this.viewStates = null;
	}
	
	@Override
	public JsonObject getView() {
		return this.viewStates;
	}

	@Override
	public void setView(String view) {
		JsonParser parser = new JsonParser();
		if(view!=null){
			JsonObject o = parser.parse(view).getAsJsonObject();
			this.viewStates = o;
		}else{
			this.viewStates=null;
		}
	}
	
	@Override
	public long getId() {
		return this.id;
	}

}
