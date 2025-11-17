package org.acme;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Named("test")
public class TestLambda implements RequestHandler<InputObject, OutputObject> {

  private static final Logger LOG = Logger.getLogger(TestLambda.class);

  @Inject
  @RestClient
  AppConfigClient appConfigClient;

  @Inject
  ProcessingService service;

  @Override
  public OutputObject handleRequest(InputObject input, Context context) {
    try {
      appConfigClient.getConfig();
    } catch (Exception exception) {
      LOG.info("Exception caught in TestLambda::handleRequest", exception);
    }
    return service.process(input).setRequestId(context.getAwsRequestId());
  }
}
