package org.egov.tlcalculator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;


@Configuration
@Data
public class CTLBillingSlabConfigs {

	@Value("${kafka.topics.save.ctl.service}")
	public String persisterCTLSaveTopic;
	
	@Value("${kafka.topics.update.ctl.service}")
	public String persisterCTLUpdateTopic;
	
}
