

package org.geppetto.persistence.db.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.IGeppettoProject;
import org.geppetto.core.data.model.IView;

@PersistenceCapable(detachable = "true")
public class GeppettoProject implements Serializable, IGeppettoProject
{
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	private String name;

	@Join
	@Persistent(dependentElement = "true", defaultFetchGroup = "true")
	private List<Experiment> experiments;

	private long activeExperimentId;

	@Column(name = "persisteddata_id")
	@Persistent(dependent = "true", defaultFetchGroup = "true")
	private PersistedData geppettoModel;
	private boolean isPublic = false;
	private boolean isReadOnly = false;
	
	private transient boolean volatileProject;
	
	@Join
	@Persistent(dependentElement = "true", defaultFetchGroup = "true")
	private View view;

	private transient String baseURL;

	public GeppettoProject(String name, PersistedData geppettoModel)
	{
		super();
		this.activeExperimentId = -1;
		this.name = name;
		this.geppettoModel = geppettoModel;
		this.view = new View(null);
	}

	public GeppettoProject()
	{
		super();
		this.activeExperimentId = -1;
	}

	public long getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<Experiment> getExperiments()
	{
		return experiments;
	}

	public void setExperiments(List<Experiment> experiments)
	{
		this.experiments = experiments;
	}

	public PersistedData getGeppettoModel()
	{
		return geppettoModel;
	}

	public void setGeppettoModel(PersistedData geppettoModel)
	{
		this.geppettoModel = geppettoModel;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	@Override
	public boolean isVolatile()
	{
		return this.volatileProject;
	}

	@Override
	public void setVolatile(boolean volatileProject)
	{
		this.volatileProject = volatileProject;
	}

	@Override
	public long getActiveExperimentId()
	{
		return this.activeExperimentId;
	}

	@Override
	public void setActiveExperimentId(long experimentId)
	{
		this.activeExperimentId = experimentId;
	}

	@Override
	public boolean isPublic() {
		return this.isPublic;
	}
	
	public void setPublic(boolean mode){
		this.isPublic = mode;
	}
	
	@Override
	public void setView(IView view) {
		this.view = (View) view;
	}

	@Override
	public IView getView() {
		return this.view;
	}

	@Override
	public String getBaseURL()
	{
		return this.baseURL;
	}

	@Override
	public void setBaseURL(String baseURL)
	{
		this.baseURL=baseURL;
		
	}
}