
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

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.Properties;

        import static io.restassured.RestAssured.given;



public class AuthControllerAPITest {
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

    public AuthControllerAPITest() throws FileNotFoundException {
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

        File postJson= new File("src/test/TestResources/CreateTokenGenerator.json");
        userResponse=
                given().spec(requestSpecification).
                        body(postJson).

                        when().post(properties.getProperty("authUrl")).
                        then().spec(responseSpecification).log().all().extract().response();
         userResponseJSONObject = new JSONObject(userResponse.asString());


//        ExtentTest test = extent.createTest("Specification Validations", "Checking the Status Code and the" +
//                "request and response specifications prior to all others tests.");
//        test.log(Status.INFO, "Validating Specifications.");
//        test.info("The checking of the Request and Response Specification is done before other tests. The validations are complete.");

    }

@Test(priority =  1)
public void AuthorityValidation(){

boolean isAuthorityNormal = false;
JSONArray RoleJSONArray = userResponseJSONObject.getJSONArray("role");


    for (int i = 0 ; i < RoleJSONArray.length() ; i++){
        JSONObject RoleJSONObject = RoleJSONArray.getJSONObject(i);
        if(RoleJSONObject.get("authority").toString().equals("NORMAL")){

            isAuthorityNormal = true;
            break;
        }

    }

    Assert.assertTrue(isAuthorityNormal);



}


    @Test(priority =  2)
    public void UIDValidation(){

        boolean isUIDPresent = true;

            if(userResponseJSONObject.get("uid") == null) {
                isUIDPresent = false;
            }



        Assert.assertTrue(isUIDPresent);



    }

    @Test(priority =  3)
    public void TokenValidation(){
   
        boolean isTokenPresent = true;

        if(userResponseJSONObject.get("token") == null) {
            isTokenPresent = false;
        }



        Assert.assertTrue(isTokenPresent);



    }

    @AfterTest
    public void windUp() {
        //extent.flush();
    }

}
