package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped
@RegisterRestClient(configKey = "appconfig")
@Path("/applications")
public interface AppConfigClient {

  @GET
  @Path("test/environments/default/configurations/test")
  String getConfig();

}