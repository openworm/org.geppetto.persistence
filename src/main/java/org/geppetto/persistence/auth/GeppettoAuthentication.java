

package org.geppetto.persistence.auth;

import org.geppetto.core.auth.IAuthService;
import org.geppetto.core.data.model.IUser;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class GeppettoAuthentication implements IAuthService
{
        @Autowired
        private String authFailureRedirectURL;

	@Override
	public String authFailureRedirect()
	{
            return authFailureRedirectURL;
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}

	@Override
	public Boolean isAuthenticated(String sessionValue)
	{
		// Authentication is checked in the frontend with shiro
		return false;
	}

	@Override
	public String getSessionId()
	{
		return null;
	}

	@Override
	public void setUser(IUser user)
	{
		// TODO User is stored at the moment in the GeppettoManager, it would make more sense to probably have it here
	}

	@Override
	public IUser getUser()
	{
		// TODO User is stored at the moment in the GeppettoManager, it would make more sense to probably have it here
		return null;
	}

}
