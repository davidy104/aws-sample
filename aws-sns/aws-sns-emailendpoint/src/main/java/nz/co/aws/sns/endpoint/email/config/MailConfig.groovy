package nz.co.aws.sns.endpoint.email.config;

import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
class MailConfig {
	String smtpHost
	String imapHost
	String username
	String password
	boolean debugModel = false;
	boolean smtpAuth = false;
	Long consumerDelay = 30000L
	Long connectionTimeout = 10000L
	Integer smtpPort = 25
	Integer smtpsPort = 465
	Long emailRedeliveryDelay = 3000L
	Integer emailMaximumRedeliveries = 2
	Integer maximumAttached = 3
	String defaultTemplate = "defaultEmailTemplate.vm"
	// megabytes
	Integer attachmentFileSize = 2
	
}
