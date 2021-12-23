

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;



public class QuestionControllerAPITest {
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    RequestSpecBuilder requestSpecBuilder;
    ResponseSpecBuilder responseSpecBuilder;
    Properties properties = new Properties();
    FileInputStream fileinputstream = new FileInputStream("src/test/TestResources/Data.Properties");
    JSONArray userResponseJSONArray;
    Response userResponse;
    JSONObject userResponseJSONObject;
    //ExtentReports
    static ExtentHtmlReporter htmlReporter;
    static ExtentReports extent;

    public QuestionControllerAPITest() throws FileNotFoundException {
    }


    @BeforeTest
    public void setupRequestResponseSpecifications() throws IOException {

        properties.load(fileinputstream);
        requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri(properties.getProperty("baseUri")).
                addHeader("Content-Type", "application/json").log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();
        responseSpecBuilder = new ResponseSpecBuilder().
                expectContentType(ContentType.JSON).
                expectStatusCode(200).
                expectHeader("Content-Type", "application/json");
        responseSpecification = responseSpecBuilder.build();

//        htmlReporter = new ExtentHtmlReporter("UserAPITestExtentReport.html");
//        extent = new ExtentReports();
//        extent.attachReporter(htmlReporter);

    }

    @Test(priority = 0)
    public void specificationsValidations() {
        //validating t
        String token = properties.getProperty("token");
        Map<String , String> headers = new HashMap<String, String>() {
            {
                put("Accept", "application/json");
                put("Authorization" , "Bearer " + token);

            }

        };
        userResponse=
                given().spec(requestSpecification).
                        headers(headers).
                        when().get(properties.getProperty("questionUrlbyID")).
                        then().spec(responseSpecification).log().all().extract().response();


        userResponseJSONObject  = new JSONObject(userResponse.asString());



//        ExtentTest test = extent.createTest("Specification Validations", "Checking the Status Code and the" +
//                "request and response specifications prior to all others tests.");
//        test.log(Status.INFO, "Validating Specifications.");
//        test.info("The checking of the Request and Response Specification is done before other tests. The validations are complete.");

    }

    @Test(
            priority = 1
    )
    public void ValidateID() {

      int targetID = 1;
        int UserID = Integer.parseInt(userResponseJSONObject.get("id").toString());

      Assert.assertEquals(UserID , targetID);
    }

    @Test(
            priority = 2
    )
    public void ValidateContent() {

        boolean isContentValid = true;
        if(userResponseJSONObject.get("content").toString() == null){
           isContentValid = false;
        }

        Assert.assertTrue(isContentValid);
    }

    @Test(
            priority = 3
    )
    public void ValidateAnswer() {

        boolean isAnswerYes = false;
        if(userResponseJSONObject.get("answer").toString().equals("yes")){
            isAnswerYes = true;
        }

        Assert.assertTrue(isAnswerYes);
    }

    @Test(
            priority = 4
    )
    public void ValidateMarks() {

        int targetMarks = 10;
        int UserMarks = Integer.parseInt(userResponseJSONObject.get("marks").toString());

        Assert.assertEquals(UserMarks , targetMarks);
    }

    @Test(
            priority = 5
    )
    public void ValidateUniqueOptionNumber() {

        JSONArray options = userResponseJSONObject.getJSONArray("options");
        HashSet<String>NumberofOptions = new HashSet<>();
        int Number_of_Entries = options.length();

        for(int i = 0; i < options.length(); ++i) {
            JSONObject userJSONObject = options.getJSONObject(i);
            NumberofOptions.add(userJSONObject.get("id").toString());
        }


        Assert.assertEquals(NumberofOptions.size() , Number_of_Entries);
    }

    @Test(
            priority = 5
    )
    public void ValidateUniqueOptions() {

        JSONArray options = userResponseJSONObject.getJSONArray("options");
        HashSet<String>UniqueOptions = new HashSet<>();
        int Number_of_Entries = options.length();

        for(int i = 0; i < options.length(); ++i) {
            JSONObject userJSONObject = options.getJSONObject(i);
            UniqueOptions.add(userJSONObject.get("content").toString());
        }


        Assert.assertEquals(UniqueOptions.size() , Number_of_Entries);
    }
    @AfterTest
    public void windUp() {
        //extent.flush();
    }

}
