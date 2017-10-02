package examsmx.camel;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Rule;
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


	@Test
	public void routeTest() throws Exception {
		getTestsupport1().template().sendBody(EP_FROM, "-");
		MockEndpoint ep = getTestsupport1().getMockEndpoint(EP_TARGET);
		ep.expectedMessageCount(1);
		ep.message(0).header("myHeader").endsWith("JUnitTest");
		getTestsupport1().assertMockEndpointsSatisfied();
	}
	
	@Test
	public void routeTest2() throws Exception {
		MockEndpoint ep = getTestsupport2().getMockEndpoint("mock:file:/tmp/input");
		String body = "REPLACED";
		ep.whenAnyExchangeReceived(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				exchange.getIn().setBody(body);
			}
		});
		Object requestBody = getTestsupport2().template().requestBody("file:/tmp/input", "TEST");
		assertEquals(body, requestBody);
	}

	@Rule
	public ContextBasedCamelTestSupport getTestsupport1() throws Exception {
		if (testSupport1 == null) {
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
		}
		return testSupport1;
	}
	
	@Rule
	public ContextBasedCamelTestSupport getTestsupport2() throws Exception {
		if (testSupport2 == null) {
			testSupport2 = new ContextBasedCamelTestSupport(camelContext2) {
				@Override
				public String isMockEndpointsAndSkip() {
					return "file:*";
				}
			};
		}
		return testSupport2;
	}

}
