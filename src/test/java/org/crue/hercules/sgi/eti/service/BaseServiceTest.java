package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.framework.test.context.support.SgiTestProfileResolver;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * BaseServiceTest
 */
@ActiveProfiles(resolver = SgiTestProfileResolver.class)
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BaseServiceTest {

}
