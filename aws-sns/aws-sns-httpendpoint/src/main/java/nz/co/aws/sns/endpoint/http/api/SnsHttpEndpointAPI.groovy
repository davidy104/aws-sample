package nz.co.aws.sns.endpoint.http.api

import groovy.util.logging.Slf4j

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.ProxyInputStream
import org.springframework.stereotype.Service

@Service
@Path("/v1")
@Slf4j
class SnsHttpEndpointAPI {

	@POST
	@Path("/{path:.*}")
	@Consumes("application/json")
	@Produces("application/json")
	Response process(@Context final HttpServletRequest request) {
		log.info "process start"
		def messagetype = request.getHeader("x-amz-sns-message-type")
		//If message doesn't have the message type header, don't process it.
		if (!messagetype ){
			return Response.status(Status.BAD_REQUEST).build()
		}
		String requestBodyStr = IOUtils.toString(new ProxyInputStream(request.inputStream) {
					@Override
					void close(){
						super.close()
					}
				}, "UTF-8")

		log.info "request: {} $requestBodyStr"
		return Response.status(Status.OK).build()
	}

















}
