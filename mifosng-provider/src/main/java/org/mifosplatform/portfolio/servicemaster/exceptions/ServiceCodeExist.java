package org.mifosplatform.portfolio.servicemaster.exceptions;
import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

@SuppressWarnings("serial")
public class ServiceCodeExist extends AbstractPlatformDomainRuleException {

		public ServiceCodeExist(final String serviceCode) {
			super("service.is.already.exists.with.service.code", "service is already existed with service code:"+serviceCode, serviceCode);
		}

	}
