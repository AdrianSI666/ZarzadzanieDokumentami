package gatlingPerformance;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_NUMBER;
import static com.asledz.kancelaria_prawnicza.enums.PageProperties.PAGE_SIZE;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class DbSimulation extends Simulation {

    // Http Configuration
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8091/")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .maxConnectionsPerHost(6);

    private static final int USER_COUNT = 1;
    private static final int RAMP_DURATION = 1;

    // FEEDER FOR TEST DATA
    private static final FeederBuilder.FileBased<Object> pageParams = jsonFile("gatlingParams/page.json").random();
    private static final FeederBuilder.FileBased<Object> loginParams = jsonFile("gatlingParams/login.json").random();
    private static final FeederBuilder.FileBased<Object> sortParams = jsonFile("gatlingParams/sort.json").random();
    private static final FeederBuilder.FileBased<Object> filterParams = jsonFile("gatlingParams/filter.json").random();

    // HTTP CALLS
    private static final ChainBuilder login =
            feed(loginParams)
                    .exec(http("Log in")
                            .post("login")
                            .body(StringBody("{\"email\":\"#{email}\",\"password\":\"#{password}\"}"))
                            .check(jmesPath("access_token").saveAs("jwtToken")));
    private static final ChainBuilder getPageOfDocuments =
            feed(pageParams)
                    .exec(http("page: #{page} size: #{pageSize}")
                            .get("/documents")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .queryParam(PAGE_NUMBER.name, "#{page}")
                            .queryParam(PAGE_SIZE.name, "#{pageSize}"));
    private static final ChainBuilder getPageOfDocumentsSorted =
            feed(pageParams)
                    .feed(sortParams)
                    .exec(http("page: #{page} size: #{pageSize} sort:#{sort}")
                            .get("/documents")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .queryParam(PAGE_NUMBER.name, "#{page}")
                            .queryParam(PAGE_SIZE.name, "#{pageSize}")
                            .queryParam("#{sort}", "#{bool}"));
    private static final ChainBuilder getPageOfDocumentsSorted2Params =
            feed(pageParams)
                    .feed(sortParams)
                    .exec(http("page: #{page} size: #{pageSize} sort: #{sort} and: #{sort2}")
                            .get("/documents")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .queryParam(PAGE_NUMBER.name, "#{page}")
                            .queryParam(PAGE_SIZE.name, "#{pageSize}")
                            .queryParam("#{sort}", "#{bool}")
                            .queryParam("#{sort2}", "#{bool2}"));

    private static final ChainBuilder getPageOfDocumentsByFilter =
            feed(pageParams)
                    .feed(filterParams)
                    .exec(http("page: #{page} size: #{pageSize} filter1: #{value1}")
                            .get("/documents/by")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .queryParam(PAGE_NUMBER.name, "#{page}")
                            .queryParam(PAGE_SIZE.name, "#{pageSize}")
                            .queryParam("#{filter1}", "#{value1}"));
    private static final ChainBuilder getPageOfDocumentsByFilterAndSort =
            feed(pageParams)
                    .feed(filterParams)
                    .exec(http("page: #{page} size: #{pageSize} filter1: #{value1} and sort1: #{sort1}")
                            .get("/documents/by")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .queryParam(PAGE_NUMBER.name, "#{page}")
                            .queryParam(PAGE_SIZE.name, "#{pageSize}")
                            .queryParam("#{filter1}", "#{value1}")
                            .queryParam("#{sort1}", "#{value3}"));
    private static final ChainBuilder getPageOfDocumentsByFilter2 =
            feed(pageParams)
                    .feed(filterParams)
                    .exec(http("page: #{page} size: #{pageSize} filter1: #{value1}, filter2: #{value2}")
                            .get("/documents/by")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .queryParam(PAGE_NUMBER.name, "#{page}")
                            .queryParam(PAGE_SIZE.name, "#{pageSize}")
                            .queryParam("#{filter1}", "#{value1}")
                            .queryParam("#{filter2}", "#{value2}"));
    private static final ChainBuilder getPageOfDocumentsByFilter2AndSort =
            feed(pageParams)
                    .feed(filterParams)
                    .exec(http("page: #{page} size: #{pageSize} filter1: #{value1}, filter2: #{value2} and sort1: #{sort1}, sort2:#{sort2}")
                            .get("/documents/by")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .queryParam(PAGE_NUMBER.name, "#{page}")
                            .queryParam(PAGE_SIZE.name, "#{pageSize}")
                            .queryParam("#{filter1}", "#{value1}")
                            .queryParam("#{filter2}", "#{value2}")
                            .queryParam("#{sort1}", "#{value3}")
                            .queryParam("#{sort2}", "#{value4}"));

    private final ScenarioBuilder scn = scenario("Kancelaria Prawnicza Performance Test")
            .exec(login)
            .repeat(1000)
            .on(getPageOfDocuments)

            .repeat(1000)
            .on(getPageOfDocumentsSorted)

            .repeat(1000)
            .on(getPageOfDocumentsSorted2Params)
            .repeat(1000)
            .on(getPageOfDocumentsByFilter)

            .repeat(1000)
            .on(getPageOfDocumentsByFilterAndSort)

            .repeat(1000)
            .on(getPageOfDocumentsByFilter2)

            .repeat(1000)
            .on(getPageOfDocumentsByFilter2AndSort);

    {
        setUp(
                scn.injectOpen(
                        nothingFor(5),
                        rampUsers(USER_COUNT).during(RAMP_DURATION)
                )).protocols(httpProtocol);
    }
}
