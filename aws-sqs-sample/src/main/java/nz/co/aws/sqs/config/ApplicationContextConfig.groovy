package nz.co.aws.sqs.config;

import nz.co.aws.config.AwsConfiguration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(basePackages = "nz.co.aws.sqs")
@Import(value = [ AwsConfiguration.class ])
class ApplicationContextConfig {
}
