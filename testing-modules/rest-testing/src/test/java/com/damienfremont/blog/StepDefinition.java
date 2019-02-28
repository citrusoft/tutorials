package com.damienfremont.blog;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.github.tomakehurst.wiremock.WireMockServer;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StepDefinition {

    private static final String CREATE_PATH = "/create";
    private static final String APPLICATION_JSON = "application/json";

    private final InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("cucumber.json");
    private final String jsonString = new Scanner(jsonInputStream, "UTF-8").useDelimiter("\\Z").next();

    private final WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    ///////////////////////
    // POST & GET json //
    //////////////////////
    @When("^users upload data on a project$")
    public void usersUploadDataOnAProject() throws IOException {
        wireMockServer.start();

        configureFor("localhost", wireMockServer.port());
        stubFor(post(urlEqualTo(CREATE_PATH))
                .withHeader("content-type", equalTo(APPLICATION_JSON))
                .withRequestBody(containing("testing-framework"))
                .willReturn(aResponse().withStatus(200)));

        HttpPost request = new HttpPost("http://localhost:" + wireMockServer.port() + "/create");
        StringEntity entity = new StringEntity(jsonString);
        request.addHeader("content-type", APPLICATION_JSON);
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        assertEquals(200, response.getStatusLine().getStatusCode());
        verify(postRequestedFor(urlEqualTo(CREATE_PATH))
                .withHeader("content-type", equalTo(APPLICATION_JSON)));

        wireMockServer.stop();
    }

    @When("^users want to get information on the (.+) project$")
    public void usersGetInformationOnAProject(String projectName) throws IOException {
        wireMockServer.start();

        configureFor("localhost", wireMockServer.port());
        stubFor(get(urlEqualTo("/projects/cucumber")).withHeader("accept", equalTo(APPLICATION_JSON))
                .willReturn(aResponse().withBody(jsonString)));

        HttpGet request = new HttpGet("http://localhost:" + wireMockServer.port() + "/projects/" + projectName.toLowerCase());
        request.addHeader("accept", APPLICATION_JSON);
        HttpResponse httpResponse = httpClient.execute(request);
        String responseString = convertResponseToString(httpResponse);

        assertThat(responseString, containsString("\"testing-framework\": \"cucumber\""));
        assertThat(responseString, containsString("\"website\": \"cucumber.io\""));
        verify(getRequestedFor(urlEqualTo("/projects/cucumber")).withHeader("accept", equalTo(APPLICATION_JSON)));

        wireMockServer.stop();
    }

    @Then("^the server should handle it and return a success status$")
    public void theServerShouldReturnASuccessStatus() {
    }

    @Then("^the requested data is returned$")
    public void theRequestedDataIsReturned() {
    }

    ///////////////////////
    // Bookin a Script //
    //////////////////////
    
    private class BookIn {
		String token;
    	String user;
    	String email;
    	String repo;
    	String project;
    	String commit;
    	String status;

    	public BookIn(String token, String user, String email, String repo, String project, String commit,
				String status) {
			super();
			this.token = token;
			this.user = user;
			this.email = email;
			this.repo = repo;
			this.project = project;
			this.commit = commit;
			this.status = status;
		}
    	
    	public String getToken() {
			return token;
		}
		public String getUser() {
			return user;
		}
		public String getEmail() {
			return email;
		}
		public String getRepo() {
			return repo;
		}
		public String getProject() {
			return project;
		}
		public String getCommit() {
			return commit;
		}
		public String getStatus() {
			return status;
		}
    	
    }

    private List<BookIn> bookIns;
    private String actualStatus = "OK";
    
    @Given("^a list of commits$")
    public void a_list_of_commits(List<BookIn> bookings) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // List<YourType>, List<List<E>>, List<Map<K,V>> or Map<K,V>.
        // E,K,V must be a scalar (String, Integer, Date, enum etc)
        bookIns = bookings;
    }
    
    @When("^developer commits a script$")
    public void developer_commits_a_script() throws Throwable {
    	for (BookIn bookin : bookIns) {
    		usersUploadDataOnAProject();
    	}
    }

    @Then("^the server should accept it and return expected status$")
    public void the_server_should_accept_it_and_return_expected_status() throws Throwable {
       	for (BookIn bookin : bookIns) {
    		assertThat(bookin.status, is(actualStatus));
    	}
    }

    @When("^developer commits script (\\d+)$")
    public void developer_commits_script(int row) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
    }

    @Then("^the server should accept it and return OK$")
    public void the_server_should_accept_it_and_return_OK() throws Throwable {
		assertThat(actualStatus, is("OK"));
    }

    @Then("^the server should accept it and return ERROR$")
    public void the_server_should_accept_it_and_return_ERROR() throws Throwable {
		assertThat(actualStatus, is("ERROR"));
    }
    
    private String convertResponseToString(HttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(responseStream, "UTF-8");
        String responseString = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return responseString;
    }
}