
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
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;



public class VideoControllerAPITest {
    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;
    RequestSpecBuilder requestSpecBuilder;
    ResponseSpecBuilder responseSpecBuilder;
    Properties properties = new Properties();
    FileInputStream fileinputstream = new FileInputStream("src/test/TestResources/Data.Properties");
    JSONArray userResponseJSONArray;
    JSONObject userResponseJSONObject;
    Response userResponse;
    //ExtentReports
    static ExtentHtmlReporter htmlReporter;
    static ExtentReports extent;

    public VideoControllerAPITest() throws FileNotFoundException {
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
                        when().get(properties.getProperty("videoUrlbyID")).
                        then().spec(responseSpecification).log().all().extract().response();

         userResponseJSONObject = new JSONObject(userResponse.asString());


        //System.out.println(userResponseJSONObject);

//        ExtentTest test = extent.createTest("Specification Validations", "Checking the Status Code and the" +
//                "request and response specifications prior to all others tests.");
//        test.log(Status.INFO, "Validating Specifications.");
//        test.info("The checking of the Request and Response Specification is done before other tests. The validations are complete.");

    }

    @Test(
            priority = 1
    )
    public void ValidateID() {
        boolean isIDPresent = true;

        if (userResponseJSONObject.get("id") == null){
            isIDPresent = false;
        }

        Assert.assertTrue(isIDPresent);

    }
    @Test(
            priority = 2
    )
    public void ValidateURL() {
        boolean isURLvalid = true;

        if (userResponseJSONObject.get("url") == null){
            isURLvalid = false;
        }

        Assert.assertTrue(isURLvalid);

    }
    @Test(
            priority = 3
    )
    public void ValidateCourseTitle() {
        boolean isTitlevalid = true;

        if (userResponseJSONObject.get("title") == null){
            isTitlevalid = false;
        }

        Assert.assertTrue(isTitlevalid);
    }
    @Test(
            priority = 4
    )
    public void ValidateVideoType() {
        //video Type contains youtube or not
        boolean isTypeYoutube = false;
        String videotype = userResponseJSONObject.get("videoType").toString().toLowerCase();

            if(isASubstring(videotype)){
                isTypeYoutube = true;
            }


        Assert.assertTrue(isTypeYoutube);

    }

    private static boolean isASubstring(String videotype){
        int counter = 0;
        int i = 0;
        for(; i< "youtube".length(); i++){
            if(counter==videotype.length())
                break;
            if(videotype.charAt(counter)== "youtube".charAt(i)){
                counter++;
            }else{

                if(counter>0){
                    i -= counter;
                }
                counter = 0;
            }
        }
        return counter < videotype.length();
    }

    @AfterTest
    public void windUp() {
        //extent.flush();
    }

}
