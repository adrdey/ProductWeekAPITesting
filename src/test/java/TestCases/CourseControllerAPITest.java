package TestCases;

import Resources.Base;
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
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class CourseControllerAPITest extends Base {
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    RequestSpecBuilder requestSpecBuilder;
    ResponseSpecBuilder responseSpecBuilder;
    Properties properties = new Properties();
    FileInputStream fileinputstream = new FileInputStream("src/test/TestResources/Data.Properties");
    JSONArray userResponseJSONArray;
    Response userResponse;
    //ExtentReports
    static ExtentHtmlReporter htmlReporter;
    static ExtentReports extent;

    public CourseControllerAPITest() throws FileNotFoundException {
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
String token = properties.getProperty("GlobalToken");
Map<String , String>headers = new HashMap<String, String>() {
    {
        put("Accept", "application/json");
        put("Authorization" , "Bearer " + token);

    }

};
        userResponse=
                given().spec(requestSpecification).
                        headers(headers).
                        when().get(properties.getProperty("courseUrl")).
                        then().spec(responseSpecification).log().all().extract().response();
        userResponseJSONArray = new JSONArray(userResponse.asString());

        //System.out.println(this.userResponseJSONArray);

//        ExtentTest test = extent.createTest("Specification Validations", "Checking the Status Code and the" +
//                "request and response specifications prior to all others tests.");
//        test.log(Status.INFO, "Validating Specifications.");
//        test.info("The checking of the Request and Response Specification is done before other tests. The validations are complete.");

    }


    @Test(
            priority = 1
    )
    public void ValidateUniqueIDofCourses() {

        HashSet<String>courseid = new HashSet<>();
        int Number_of_Entries = this.userResponseJSONArray.length();

        for(int i = 0; i < this.userResponseJSONArray.length(); ++i) {
            JSONObject userJSONObject = this.userResponseJSONArray.getJSONObject(i);
            courseid.add(userJSONObject.get("id").toString());
        }


        Assert.assertEquals(courseid.size() , Number_of_Entries);
    }
    @Test(
            priority = 2
    )
    public void ValidateUniqueTitleofCourses() {

        HashSet<String>coursestitle = new HashSet<>();
        int Number_of_Entries = this.userResponseJSONArray.length();

        for(int i = 0; i < this.userResponseJSONArray.length(); ++i) {
            JSONObject userJSONObject = this.userResponseJSONArray.getJSONObject(i);
            coursestitle.add(userJSONObject.get("title").toString());
        }


        Assert.assertEquals(coursestitle.size() , Number_of_Entries);
    }

    @Test(
            priority = 2
    )
    public void ValidateURLofCourses() {
            boolean isCourseURLvalid = true;


        for(int i = 0; i < this.userResponseJSONArray.length(); ++i) {
            JSONObject userJSONObject = this.userResponseJSONArray.getJSONObject(i);
            if(userJSONObject.get("url").toString() == null){
                isCourseURLvalid = false;

            }
        }


        Assert.assertTrue(isCourseURLvalid);
    }

    @Test(
            priority = 2
    )
    public void ValidateUniqueDescriptionofCourses() {

        HashSet<String>descriptionURL = new HashSet<>();
        int Number_of_Entries = this.userResponseJSONArray.length();

        for(int i = 0; i < this.userResponseJSONArray.length(); ++i) {
            JSONObject userJSONObject = this.userResponseJSONArray.getJSONObject(i);
            descriptionURL.add(userJSONObject.get("description").toString());
        }


        Assert.assertEquals(descriptionURL.size() , Number_of_Entries);
    }

    @AfterTest
    public void windUp() {
        //extent.flush();
    }

}
