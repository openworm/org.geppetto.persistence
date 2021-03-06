

package org.geppetto.persistence.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.geppetto.core.data.model.ExperimentStatus;
import org.geppetto.core.data.model.IExperiment;
import org.geppetto.core.data.model.IGeppettoProject;
import org.geppetto.core.data.model.ISimulationResult;
import org.geppetto.core.data.model.IView;

@PersistenceCapable(detachable = "true")
public class Experiment implements Serializable, IExperiment
{
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	@Join
	@Persistent(dependentElement = "true", defaultFetchGroup = "true")
	private List<AspectConfiguration> aspectConfigurations;

	private String name;
	
	@Column(length = 10000)
	private String details;

	@Column(length = 1000)
	private String description;

	private Date creationDate;

	private Date lastModified;
	
	private Date lastRan;

	private ExperimentStatus status;

	@Join
	@Persistent(dependentElement = "true", defaultFetchGroup = "true")
	private List<SimulationResult> simulationResults;

	private Date startDate;

	private Date endDate;

	private transient IGeppettoProject project;

	private String script;

	private boolean isPublic = false;
	
	@Join
	@Persistent(dependentElement = "true", defaultFetchGroup = "true")
	private View view;
	
	public Experiment(List<AspectConfiguration> aspectConfigurations, String name, String description, Date creationDate, Date lastModified, ExperimentStatus status,
			List<SimulationResult> simulationResults, Date startDate, Date endDate, IGeppettoProject project, View view)
	{
		super();
		this.aspectConfigurations = aspectConfigurations;
		this.name = name;
		this.description = description;
		this.creationDate = creationDate;
		this.lastModified = lastModified;
		this.status = status;
		this.simulationResults = simulationResults;
		this.startDate = startDate;
		this.endDate = endDate;
		this.project = project;
		this.view = view;
	}

	public long getId()
	{
		return id;
	}

	public List<AspectConfiguration> getAspectConfigurations()
	{
		return aspectConfigurations;
	}

	public void setAspectConfigurations(List<AspectConfiguration> aspectConfigurations)
	{
		this.aspectConfigurations = aspectConfigurations;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Date getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}

	public Date getLastModified()
	{
		return lastModified;
	}

	public void setLastModified(Date lastModified)
	{
		this.lastModified = lastModified;
	}

	public void addSimulationResult(ISimulationResult result)
	{
		if(result instanceof SimulationResult)
		{
			simulationResults.add((SimulationResult) result);
		}
	}

	public List<SimulationResult> getSimulationResults()
	{
		return simulationResults;
	}

	public void setSimulationResults(List<SimulationResult> simulationResults)
	{
		this.simulationResults = simulationResults;
	}

	public ExperimentStatus getStatus()
	{
		return status;
	}

	public synchronized void setStatus(ExperimentStatus status)
	{
		this.status = status;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}
	
	public void setDetails(String details){
		this.details = details;
	}
	
	public String getDetails(){
		return this.details;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.data.model.IExperiment#getParentProject()
	 */
	@Override
	public IGeppettoProject getParentProject()
	{
		return project;
	}

	@Override
	public void setParentProject(IGeppettoProject project)
	{
		this.project = project;
	}

	@Override
	public boolean equals(Object o)
	{
		Experiment other = (Experiment) o;
		return id == other.id;
	}

	@Override
	public void setId(long id)
	{
		this.id = id;
	}

	@Override
	public void updateLastModified()
	{
		lastModified = new Date();
	}

	@Override
	public String getScript()
	{
		return script;
	}

	@Override
	public void setScript(String script)
	{
		this.script = script;
	}


	@Override
	public void updateStartDate() {
		this.startDate = new Date();
	}

	@Override
	public void updateEndDate() {
		this.endDate = new Date();
	}

	@Override
	public boolean isPublic() {
		return this.isPublic;
	}
	
	public void setIsPublic(boolean mode){
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
}