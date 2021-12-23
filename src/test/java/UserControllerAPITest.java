
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class UserControllerAPITest {
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

    public UserControllerAPITest() throws FileNotFoundException {
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
    public void GetSpecificationsValidations() {
        //validating t
        userResponse=
                given().spec(requestSpecification).
                        when().get(properties.getProperty("userUrl")).
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
    public void UsersandEmailPresentValidation() {
        boolean isIDorEmailNull = true;
        for(int i = 0; i < this.userResponseJSONArray.length(); ++i) {

            JSONObject userJSONObject = this.userResponseJSONArray.getJSONObject(i);
            if(userJSONObject.get("username") == null || userJSONObject.get("email") == null){

                isIDorEmailNull = false;
                break;
            }
        }


        Assert.assertTrue(isIDorEmailNull);

    }

    @Test(
            priority = 2
    )
    public void ValidateUniqueID() {

        HashSet<String>userid = new HashSet<>();
        int Number_of_Entries = this.userResponseJSONArray.length();

        for(int i = 0; i < this.userResponseJSONArray.length(); ++i) {
            JSONObject userJSONObject = this.userResponseJSONArray.getJSONObject(i);
            userid.add(userJSONObject.get("id").toString());
        }


        Assert.assertEquals(userid.size() , Number_of_Entries);
    }

    @Test(
            priority = 3
    )
    public void userPasswordValidation() {
        boolean allPasswordsAreValid = true;

        for(int i = 0; i < this.userResponseJSONArray.length(); ++i) {
            JSONObject userJSONObject = this.userResponseJSONArray.getJSONObject(i);
            Object userPassword = userJSONObject.get("password");
            allPasswordsAreValid = this.validatePassword(String.valueOf(userPassword));
            if (!allPasswordsAreValid) {
                System.out.println("Object that failed.");
                System.out.println(userJSONObject.get("username"));
                System.out.println(userPassword);
                break;
            }
        }

        MatcherAssert.assertThat(allPasswordsAreValid, Matchers.is(Matchers.equalTo(true)));
//        ExtentTest test = extent.createTest("Password Validations", "Checking one letter, one digit and one special character in the password.");
//        test.log(Status.INFO, "Validating Password.");
//        test.info("We validate all the password for the presence of one letter, one digit or one special character.");
    }



    private boolean validatePassword(String password) {
        boolean valid = false;
        int numberCount = 0;
        int specialCharacterCount = 0;
        int characterCount = 0;
        HashSet<Character> specialCharacters = new HashSet();
        specialCharacters.add('@');
        specialCharacters.add('!');
        specialCharacters.add('#');
        specialCharacters.add('$');
        specialCharacters.add('%');
        specialCharacters.add('^');
        specialCharacters.add('&');
        specialCharacters.add('_');
        specialCharacters.add('*');
        char[] var7 = password.toCharArray();
        int var8 = var7.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            char c = var7[var9];
            if (Character.isDigit(c)) {
                ++numberCount;
            } else if (Character.isAlphabetic(c)) {
                ++characterCount;
            } else if (specialCharacters.contains(c)) {
                ++specialCharacterCount;
            }
        }

        if (numberCount >= 1 || characterCount >= 1 || specialCharacterCount >= 1) {
            valid = true;
        }

        return valid;
    }
    @Test(priority = 4)
    public void PostSpecificationValidations() {
        //validating t
        File postJson= new File("src/test/TestResources/CreateUserData.json");
        userResponse=
                given().spec(requestSpecification).
                        body(postJson).
                        when().post(properties.getProperty("userUrl")).
                        then().spec(responseSpecification).log().all().extract().response();
         userResponseJSONObject = new JSONObject(userResponse.asString());

       // System.out.println(userResponseJSON);

//        ExtentTest test = extent.createTest("Specification Validations", "Checking the Status Code and the" +
//                "request and response specifications prior to all others tests.");
//        test.log(Status.INFO, "Validating Specifications.");
//        test.info("The checking of the Request and Response Specification is done before other tests. The validations are complete.");

    }

    @Test(
            priority = 5
    )
    public void ValidateSuccessMessage() {

        String expectedMessage = "Successfully sent verification email :";
String actualMessage = null;
        if (userResponseJSONObject.get("message") != null){
            actualMessage =  userResponseJSONObject.get("message").toString();
        }

        Assert.assertEquals(actualMessage , expectedMessage);

    }
    @Test(
            priority = 6
    )
    public void ValidateTimeStamp() {

        boolean isTimeStampPresent = true;

        if (userResponseJSONObject.get("timestamp") == null){
            isTimeStampPresent = false;
        }

        Assert.assertTrue(isTimeStampPresent);

    }

    @AfterTest
    public void windUp() {
        //extent.flush();
    }
}
