package examsmx.base;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureSecurity;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

public interface ConfigBase {
	@Configuration
	public default Option[] configure() throws MalformedURLException, IOException {
		MavenArtifactUrlReference serviceMixUrl = maven().groupId("org.apache.servicemix").artifactId("apache-servicemix").type("zip").versionAsInProject();
		String localRepo = System.getProperty("maven.repo.local", "");
		// @formatter:off
		return new Option[] {
				when(localRepo.length() > 0)
						.useOptions(systemProperty("org.ops4j.pax.url.mvn.localRepository").value(localRepo)),
				composite(createOptions()),
				karafDistributionConfiguration()
						.frameworkUrl(serviceMixUrl)
						.unpackDirectory(new File("target/exam")).useDeployFolder(false).runEmbedded(false).name("SMX"),
				KarafDistributionOption.keepRuntimeFolder(),
				systemProperty("pax.exam.osgi.unresolved.fail").value("true"),
				  configureSecurity().disableKarafMBeanServerBuilder(),
				              logLevel(LogLevelOption.LogLevel.INFO),
				               editConfigurationFilePut("etc/custom.properties", "karaf.delay.console", "false"),
				               editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.apache.karaf.features", "WARN"),
				               editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.apache.camel.core.osgi", "DEBUG"),
				               editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.apache.camel.blueprint", "DEBUG"),
				               editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.apache.camel.blueprint.BlueprintCamelContext", "TRACE"),
				               editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.apache.aries.spifly", "WARN"),
		};
		// @formatter:on
	}

	public abstract Option[] createOptions();
}
