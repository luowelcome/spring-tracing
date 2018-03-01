package mcp.cloudtrace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
class ClientRestController {

	private final RestTemplate restTemplate;

	public ClientRestController(RestTemplate rt) {
		this.restTemplate = rt;
	}

	@GetMapping("/backend")
	public String deviceNames(HttpServletRequest req) {
		String clientId = req.getHeader("client-id");
		log.info("clientId=" + clientId);
		return "Hello, " + clientId;
	}

	@GetMapping("/frontend")
	public String callBackend() {
		return restTemplate.getForObject("http://localhost:8080/backend", String.class);
	}
}
