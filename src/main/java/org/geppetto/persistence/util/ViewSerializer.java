package org.geppetto.persistence.util;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.data.model.IView;
import org.geppetto.persistence.db.model.GeppettoProject;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Creates serializer and deserializer for View object and GeppettoProject
 * @author jrmartin
 *
 */
public class ViewSerializer implements JsonDeserializer<GeppettoProject>,JsonSerializer<IView>
{
	private static Log logger = LogFactory.getLog(ViewSerializer.class);
	
	@Override
	public GeppettoProject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() { 
			   public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			      return new Date(json.getAsJsonPrimitive().getAsLong()); 
			   } 
			});
		
		if(obj.has("view")){
			JsonObject view =  obj.getAsJsonObject("view");
			if(view.has("viewStates")){
				JsonObject jsonViewStates  = view.getAsJsonObject("viewStates");
				view.addProperty("viewStates",jsonViewStates.toString());
			}
			if(view.has("id")){
				JsonPrimitive id  = view.getAsJsonPrimitive("id");
				view.addProperty("id",id.getAsNumber());
			}
		}
		JsonArray experiments = (JsonArray) obj.get("experiments");
		for(int i =0; i<experiments.size();i++){
			JsonObject experiment = (JsonObject) experiments.get(i);

			if(experiment.has("lastModified")){
				long lastModified =  experiment.getAsJsonPrimitive("lastModified").getAsLong();
				experiment.addProperty("lastModified",lastModified);
			}

			if(experiment.has("creationDate")){
				long lastModified =  experiment.getAsJsonPrimitive("creationDate").getAsLong();
				experiment.addProperty("creationDate",lastModified);
			}
			
			if(experiment.has("startDate")){
				long lastModified =  experiment.getAsJsonPrimitive("startDate").getAsLong();
				experiment.addProperty("startDate",lastModified);
			}
			
			if(experiment.has("endDate")){
				long lastModified =  experiment.getAsJsonPrimitive("endDate").getAsLong();
				experiment.addProperty("endDate",lastModified);
			}

			if(experiment.has("view")){
				JsonObject view =  experiment.getAsJsonObject("view");
				if(view.has("viewStates")){
					JsonObject jsonViewStates  = view.getAsJsonObject("viewStates");
					view.addProperty("viewStates",jsonViewStates.toString());
				}
				if(view.has("id")){
					JsonPrimitive id  = view.getAsJsonPrimitive("id");
					view.addProperty("id",id.getAsNumber());
				}
			}
		}
		GeppettoProject project =  gson.create().fromJson(obj.toString(), GeppettoProject.class);

		return project;
	}

	@Override
	public JsonElement serialize(IView src, Type typeOfSrc, JsonSerializationContext context) {
		JsonParser parser = new JsonParser();
		JsonObject json = new JsonObject();;
		JsonObject viewStates = null;
		if(src.getView() !=null){
			viewStates = parser.parse(src.getView()).getAsJsonObject();
		}

		json.addProperty("id",src.getId());
		json.add("viewStates",viewStates);
		return json;
	}

	public String getDate(long timeStamp){
		String time = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(new java.util.Date (timeStamp*1000));

		return time;
	}
}
