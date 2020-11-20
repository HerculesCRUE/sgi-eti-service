package org.crue.hercules.sgi.eti.integration;

import org.crue.hercules.sgi.framework.test.context.support.SgiTestProfileResolver;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
@ActiveProfiles(resolver = SgiTestProfileResolver.class)
public class BaseIT {

  @Autowired
  protected TestRestTemplate restTemplate;

  @Autowired
  protected TokenBuilder tokenBuilder;

}