package rentasad.library.db;

import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class SimpleContainerTest {

	@Container
	private GenericContainer<?> simpleContainer = new GenericContainer<>(DockerImageName.parse("alpine:latest"))
			.withCommand("sh", "-c", "while :; do sleep 1; done")  // Container am Laufen halten
			.withExposedPorts(80);

	@Test
	public void testSimpleContainer() {
		simpleContainer.start();
		System.out.println(simpleContainer.getLogs());  // Container-Logs abrufen
		assertTrue(simpleContainer.isRunning());
	}

	@Test
	public void testDockerEnvironment() {
		System.out.println("Docker host: " + DockerClientFactory.instance().dockerHostIpAddress());
		System.out.println("Docker version: " + DockerClientFactory.instance().client().versionCmd().exec().getVersion());
		System.out.println("Docker info: " + DockerClientFactory.instance().client().infoCmd().exec());

		assertTrue(DockerClientFactory.instance().isDockerAvailable());
	}
}
