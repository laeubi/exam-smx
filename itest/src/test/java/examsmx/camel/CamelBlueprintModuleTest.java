package examsmx.camel;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;

import examsmx.base.ConfigBase;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CamelBlueprintModuleTest extends CamelTestSupport implements ConfigBase {

	private static final String EP_TARGET = "mock:log:test";
	private static final String EP_FROM = "direct:test";
	@Inject
	@Filter(timeout = 30000, value = "(camel.context.name=camel-blueprint-ctx)")
	private CamelContext camelContext;

	@Override
	public Option[] createOptions() {
		return new Option[] { CoreOptions.mavenBundle("exam-smx", "camel-blueprint").versionAsInProject(),
				CoreOptions.mavenBundle("org.apache.camel", "camel-test").versionAsInProject(),
				KarafDistributionOption.editConfigurationFilePut("etc/camel.blueprint.cfg", "message", "Hello JUnitTest") };
	}
	@Override
	public String isMockEndpoints() {
		return "log:test";
	}
	
	@Override
	public boolean isCreateCamelContextPerClass() {
		return true;
	}
	
	@Override
	protected void startCamelContext() throws Exception {
		//not needed
	}
	@Override
	protected void stopCamelContext() throws Exception {
		//not needed
	}
	
	@Override
	protected void doPreSetup() throws Exception {
		replaceRouteFromWith("timerRoute", EP_FROM);
	}
	
	
	@Test
	public void routeTest() throws InterruptedException {
		template.sendBody(EP_FROM, "-");
		MockEndpoint ep = getMockEndpoint(EP_TARGET);
		ep.expectedMessageCount(1);
		ep.message(0).header("myHeader").endsWith("JUnitTest");
		assertMockEndpointsSatisfied();
	}

	@Override
	protected CamelContext createCamelContext() throws Exception {
		return camelContext;
	}

}
