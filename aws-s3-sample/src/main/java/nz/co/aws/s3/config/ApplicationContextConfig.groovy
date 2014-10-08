package nz.co.aws.s3.config;

import nz.co.aws.config.AwsConfiguration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(basePackages = "nz.co.aws.s3")
@Import(value = [ AwsConfiguration.class ])
class ApplicationContextConfig {
}
