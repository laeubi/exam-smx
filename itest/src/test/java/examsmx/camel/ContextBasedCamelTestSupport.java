package examsmx.camel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.NoSuchEndpointException;
import org.apache.camel.Service;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.spi.Language;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Enables TestSupport for a given CamelContext obtained from a different source
 * make sure to call {@link #tearDown()} after you are done with this, some
 * feature will only work by extending this class some will work by making some
 * protected methods public e.g. the {@link #getMandatoryEndpoint(String)}
 *
 */
public class ContextBasedCamelTestSupport extends CamelTestSupport implements MethodRule {

	private CamelContext camelContext;

	public ContextBasedCamelTestSupport(CamelContext camelContext) throws Exception {
		context = (ModelCamelContext) camelContext;
		if (camelContext == null) {
			throw new IllegalArgumentException("CamelContext can't be null");
		}
		this.camelContext = camelContext;
		//we don't want to start/stop the camel context so prevent this by setting up a service for this
		setCamelContextService(new Service() {

			@Override
			public void stop() throws Exception {
			}

			@Override
			public void start() throws Exception {
			}
		});
	}

	public CamelContext getCamelContext() {
		return camelContext;
	}

	@Override
	public Endpoint resolveMandatoryEndpoint(String uri) {
		return super.resolveMandatoryEndpoint(uri);
	}

	@Override
	public <T extends Endpoint> T resolveMandatoryEndpoint(String uri, Class<T> endpointType) {
		return super.resolveMandatoryEndpoint(uri, endpointType);
	}

	@Override
	public MockEndpoint getMockEndpoint(String uri) {
		return super.getMockEndpoint(uri);
	}

	@Override
	public MockEndpoint getMockEndpoint(String uri, boolean create) throws NoSuchEndpointException {
		return super.getMockEndpoint(uri, create);
	}

	@Override
	public void sendBody(String endpointUri, Object body) {
		super.sendBody(endpointUri, body);
	}

	@Override
	public void sendBody(String endpointUri, Object body, Map<String, Object> headers) {
		super.sendBody(endpointUri, body, headers);
	}

	@Override
	public void sendBodies(String endpointUri, Object... bodies) {
		super.sendBodies(endpointUri, bodies);
	}

	@Override
	public Exchange createExchangeWithBody(Object body) {
		return super.createExchangeWithBody(body);
	}

	@Override
	public void assertExpression(Exchange exchange, String languageName, String expressionText, Object expectedValue) {
		super.assertExpression(exchange, languageName, expressionText, expectedValue);
	}

	@Override
	public void assertPredicate(String languageName, String expressionText, Exchange exchange, boolean expected) {
		super.assertPredicate(languageName, expressionText, exchange, expected);
	}

	@Override
	public Language assertResolveLanguage(String languageName) {
		return super.assertResolveLanguage(languageName);
	}

	@Override
	public void assertMockEndpointsSatisfied(long timeout, TimeUnit unit) throws InterruptedException {
		super.assertMockEndpointsSatisfied(timeout, unit);
	}

	@Override
	public void resetMocks() {
		MockEndpoint.resetMocks(camelContext);
	}

	@Override
	public <T extends Endpoint> T getMandatoryEndpoint(String uri, Class<T> type) {
		return super.getMandatoryEndpoint(uri, type);
	}

	@Override
	public void disableJMX() {
		super.disableJMX();
	}

	@Override
	public void enableJMX() {
		super.enableJMX();
	}

	@Override
	public Exchange createExchangeWithBody(CamelContext camelContext, Object body) {
		return super.createExchangeWithBody(camelContext, body);
	}

	@Override
	public final boolean isCreateCamelContextPerClass() {
		return false;
	}

	@Override
	public final void startCamelContext() throws Exception {
		// not needed
	}

	@Override
	public final void stopCamelContext() throws Exception {
		// not needed
	}

	@Override
	public final void postProcessTest() throws Exception {
		// do not fetch from statics to prevent intermixing of camel context
		applyCamelPostProcessor();
	}

	@Override
	protected final CamelContext createCamelContext() throws Exception {
		return camelContext;
	}

	@Override
	public Endpoint getMandatoryEndpoint(String uri) {
		return super.getMandatoryEndpoint(uri);
	}

	@Override
	public String getTestMethodName() {
		return camelContext.getName() + " / " + super.getTestMethodName();// +
																			// testMethodName;
	}

	@Override
	public void assertMockEndpointsSatisfied() throws InterruptedException {
		super.assertMockEndpointsSatisfied();
	}

	@Override
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		Statement statement = new Statement() {

			@Override
			public void evaluate() throws Throwable {
				setUp();
				try {
					base.evaluate();
				} finally {
					tearDown();
				}
			}
		};
		// call camel rule methods that are normally called by JUnit
		Method[] methods = CamelTestSupport.class.getMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(Rule.class)) {
				System.out.println("Method with @Rule: " + m);
				try {
					Object subRule = m.invoke(this);
					if (subRule instanceof MethodRule) {
						MethodRule methodRule = (MethodRule) subRule;
						statement = methodRule.apply(statement, method, target);
					} else if (subRule instanceof TestRule) {
						TestRule testRule = (TestRule) subRule;
						statement = testRule.apply(statement,
								Description.createTestDescription(method.getDeclaringClass(), method.getName()));
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return statement;
	}

}
