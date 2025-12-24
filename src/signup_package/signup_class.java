package signup_package;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class signup_class {

    // Helper to handle Stale Elements 
    public static void safeClick(WebDriver driver, WebDriverWait wait, By locator) {
        for (int i = 0; i < 3; i++) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
                return;
            } catch (StaleElementReferenceException e) {
                System.out.println("Element went stale, retrying... " + (i + 1));
            }
        }
    }

    // Helper to extract text from email
    private static String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result.append(bodyPart.getContent());
                }
            }
            return result.toString();
        }
        return "";
    }

    // Fetches the latest 6-digit OTP from Gmail
    public static String getLatestOTP(String email, String appPassword) throws Exception {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.ssl.enable", "true");

        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", email, appPassword);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        if (messages.length == 0) {
            inbox.close(false);
            store.close();
            return "";
        }

        Message message = messages[messages.length - 1];
        String content = getTextFromMessage(message);

        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(content);
        
        String otp = "";
        if (m.find()) {
            otp = m.group(0);
        }

        inbox.close(false);
        store.close();
        return otp;
    }

    public static void main(String[] args) {
        String email = "mirageshrestha83@gmail.com"; 
        String appPassword = "ihdn htym ryrc ouna"; 

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            driver.manage().window().maximize();
            driver.get("https://authorized-partner.vercel.app/");

            // 1. Navigation
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Login"))).click();
            Thread.sleep(2000);
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sign Up"))).click();

            // 2. Consent Screen (Handled with safeClick to avoid StaleElementReferenceException)
            System.out.println("Handling consent screen...");
            safeClick(driver, wait, By.id("remember"));
            driver.findElement(By.xpath("//button[text()='Continue']")).click();

            // 3. Form Submission
            System.out.println("Filling signup form...");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName"))).sendKeys("Mirage");
            driver.findElement(By.name("lastName")).sendKeys("Shrestha");
            driver.findElement(By.name("email")).sendKeys(email);
            driver.findElement(By.name("phoneNumber")).sendKeys("9834989277");
            driver.findElement(By.name("password")).sendKeys("Mirage123#");
            driver.findElement(By.name("confirmPassword")).sendKeys("Mirage123#");
            Thread.sleep(2000);
            driver.findElement(By.xpath("//button[text()='Next']")).click();

            // 4. Fetch OTP
            System.out.println("Waiting for OTP email...");
            String otp = "";
            for (int i = 0; i < 6; i++) {
                Thread.sleep(7000); 
                otp = getLatestOTP(email, appPassword);
                if (!otp.isEmpty()) break;
                System.out.println("Polling for OTP... attempt " + (i+1));
            }

            if (otp.isEmpty()) throw new Exception("OTP not found.");
            System.out.println("OTP Received: " + otp);

            // 5. Enter OTP into the data-input-otp field
            WebElement otpInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[data-input-otp='true']")
            ));
            otpInput.click(); // Focus the field
            otpInput.sendKeys(otp);
            Thread.sleep(3000);

            // 6. Verify
            driver.findElement(By.xpath("//button[text()='Verify Code']")).click();
            System.out.println("Signup Process Completed!");
            
            //  7. Agency Details
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("agency_name"))).sendKeys("Miracle Agency");
            driver.findElement(By.name("role_in_agency")).sendKeys("Manager");
            driver.findElement(By.name("agency_email")).sendKeys("miracleagency@gmail.com");
            driver.findElement(By.name("agency_website")).sendKeys("www.miracleagency.com");
            driver.findElement(By.name("agency_address")).sendKeys("Koteshwor, Kathmandu");
            driver.findElement(By.cssSelector("button[role='combobox']")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Australia')]"))).click();
            Thread.sleep(2000);
            driver.findElement(By.xpath("//button[@type='submit' and text()='Next']")).click();
            
            // 8. Professional Experience
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@role='combobox' and .//span[contains(text(), 'Select Your Experience Level')]]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option' or @role='menuitem']//span[text()='7 years']"))).click();
            driver.findElement(By.name("number_of_students_recruited_annually")).sendKeys("500");
            driver.findElement(By.name("focus_area")).sendKeys("Undergraduate admissions to Australia");
            driver.findElement(By.name("success_metrics")).sendKeys("95");
            driver.findElement(By.xpath("//label[contains(.,'Visa Processing')]/preceding-sibling::button")).click();
            driver.findElement(By.xpath("//label[contains(.,'Test Prepration')]/preceding-sibling::button")).click();
            Thread.sleep(2000);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Next']"))).click();
            
            // 9. Verification and Preferences
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("business_registration_number"))).sendKeys("REG12345678");
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@role='combobox' and .//span[contains(text(), 'Preferred Countries')]]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Australia']"))).click();
            
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[contains(.,'Universities')]/preceding-sibling::button"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[contains(.,'Colleges')]/preceding-sibling::button"))).click();
            driver.findElement(By.name("certification_details")).sendKeys("ICEF Certified Education Agent");
            
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//input[@type='file'])[1]"))).sendKeys("C:\\Users\\Rabindra Basnet\\Downloads\\Company Registration.pdf");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//input[@type='file'])[2]"))).sendKeys("C:\\Users\\Rabindra Basnet\\Downloads\\High-School-Achievement-Certificate-Template.pdf");
            Thread.sleep(2000);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit' and text()='Submit']"))).click();
            
            Thread.sleep(9000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            driver.quit(); 
        }
    }
}