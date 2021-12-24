package Resources;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;


import java.io.File;
import java.io.IOException;

public class Base {

    static ExtentReports extent;


    public static ExtentReports ReportObject()
    {
        String path = "/Users/adrdey/IdeaProjects/ProductWeek_API_Assignment/Report/index.html";

        ExtentSparkReporter reporter = new ExtentSparkReporter(path);
        reporter.config().setReportName("Skill Stack API Test  Results");
        reporter.config().setDocumentTitle("Test Results");


        extent=new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Tester", "Akash Kr Singh/ Adrito Dey/ Supriya Lnu");
        return extent;

    }
}
