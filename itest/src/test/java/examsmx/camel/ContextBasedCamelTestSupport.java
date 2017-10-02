package examsmx.camel;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.NoSuchEndpointException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.spi.Language;
import org.apache.camel.test.junit4.CamelTestSupport;

/**
 * Enables TestSupport for a given CamelContext obtained from a different source
 * make sure to call {@link #tearDown()} after you are done with this, some
 * feature will only work by extending this class some will work by making some
 * protected methods public e.g. the {@link #getMandatoryEndpoint(String)}
 *
 */
public class ContextBasedCamelTestSupport extends CamelTestSupport {

	private CamelContext camelContext;

	public ContextBasedCamelTestSupport(CamelContext camelContext) throws Exception {
		context = (ModelCamelContext) camelContext;
		if (camelContext == null)  {
			throw new IllegalArgumentException("CamelContext can't be null");
		}
		this.camelContext = camelContext;
		setUp();
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
		//do not fetch from statics to prevent intermixing of camel context
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
	public void assertMockEndpointsSatisfied() throws InterruptedException {
		super.assertMockEndpointsSatisfied();
	}

	@Override
	public void tearDown() {
		try {
			super.tearDown();
			template().stop();
		} catch (Exception e) {
			throw new RuntimeException("error in tear down", e);
		}
	}

	public String getTestMethodName() {
		return camelContext.getName();
	}

}
