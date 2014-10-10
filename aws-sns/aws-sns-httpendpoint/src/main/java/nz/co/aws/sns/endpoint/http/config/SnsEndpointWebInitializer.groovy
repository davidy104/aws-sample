package nz.co.aws.sns.endpoint.http.config
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.ServletRegistration

import org.springframework.web.WebApplicationInitializer
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet


class SnsEndpointWebInitializer implements WebApplicationInitializer {
	static final String DISPATCHER_SERVLET_NAME = "dispatcher"
	static final String DISPATCHER_SERVLET_MAPPING = "/"

	@Override
	public void onStartup(ServletContext servletContext)
	throws ServletException {
		AnnotationConfigWebApplicationContext rootContext = createContext(
				ApplicationContextConfiguration.class)

		ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
				DISPATCHER_SERVLET_NAME, new DispatcherServlet(rootContext))
		dispatcher.setLoadOnStartup(1)
		dispatcher.addMapping(DISPATCHER_SERVLET_MAPPING)
		servletContext.addListener(new ContextLoaderListener(rootContext))

		ServletRegistration.Dynamic jerseyServletDispatcher = servletContext
				.addServlet("JerseySpringServlet", JerseySpringServlet.class)
		jerseyServletDispatcher.setLoadOnStartup(1)
		jerseyServletDispatcher.addMapping("/rest/*")
	}

	private AnnotationConfigWebApplicationContext createContext(
			final Class<?>... annotatedClasses) {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext()
		context.register(annotatedClasses)
		return context
	}
}
