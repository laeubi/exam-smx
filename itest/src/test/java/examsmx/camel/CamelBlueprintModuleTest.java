package examsmx.camel;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.After;
import org.junit.Before;
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
public class CamelBlueprintModuleTest implements ConfigBase {

	private static final String EP_TARGET = "mock:log:test";
	private static final String EP_FROM = "direct:test";
	@Inject
	@Filter(timeout = 30000, value = "(camel.context.name=camel-blueprint-ctx)")
	private CamelContext camelContext1;

	@Inject
	@Filter(timeout = 30000, value = "(camel.context.name=camel-blueprint-2-ctx)")
	private CamelContext camelContext2;

	private ContextBasedCamelTestSupport testSupport1;
	private ContextBasedCamelTestSupport testSupport2;

	@Override
	public Option[] createOptions() {
		return new Option[] { CoreOptions.mavenBundle("exam-smx", "camel-blueprint").versionAsInProject(),
				CoreOptions.mavenBundle("exam-smx", "camel-blueprint-2").versionAsInProject(),
				CoreOptions.mavenBundle("org.apache.camel", "camel-test").versionAsInProject(), KarafDistributionOption
						.editConfigurationFilePut("etc/camel.blueprint.cfg", "message", "Hello JUnitTest") };
	}

	@Before
	public void setupTestSupport() throws Exception {
		testSupport1 = new ContextBasedCamelTestSupport(camelContext1) {
			@Override
			public String isMockEndpoints() {
				return "log:test";
			}

			@Override
			protected void doPreSetup() throws Exception {
				replaceRouteFromWith("timerRoute", EP_FROM);
			}

		};
		testSupport2 = new ContextBasedCamelTestSupport(camelContext2) {

		};
	}

	@After
	public void shutdownTestSupport() {
		testSupport1.tearDown();
		testSupport2.tearDown();
	}

	@Test
	public void routeTest() throws InterruptedException {
		testSupport1.template().sendBody(EP_FROM, "-");
		MockEndpoint ep = testSupport1.getMockEndpoint(EP_TARGET);
		ep.expectedMessageCount(1);
		ep.message(0).header("myHeader").endsWith("JUnitTest");
		testSupport1.assertMockEndpointsSatisfied();
	}

}
