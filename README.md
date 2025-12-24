# Selenium Automation Signup

DEMO VIDEO LINK:
https://www.loom.com/share/60eaf0b38e834e108fce627d26da97d5

My Output Console:

<img width="236" height="86" alt="image" src="https://github.com/user-attachments/assets/63e2b0b9-d5d3-48b3-959b-b1abdd6cfd59" />


The project is an automation of an all-encompassing registration process to the Authorized Partner platform. It has such advanced features as automated Gmail OTP retrieval and management of dynamic web elements.

## 🚀 Key Features
- **Gmail IMAP Integration: Will automatically connect with gmail to get 6-digit OTP codes.
- **Stale Elements Handling: This takes a special safeClick method to avoid automation failures.
- **Form Automation: Fills out the multi-page forms such as Agency Details and Professional Experience.
- **File Upload: Automates the increment of PDF registration documents.

## 🛠️ Prerequisites
- **Java JDK 11+**
- **httpclient5**
- **httpcore5**
- **Java activation-1.1.1**
- **Selenium WebDriver**
- **JavaMail API (javax.mail)**
- **ChromeDriver** (matching your Chrome version)

## ⚙️ Setup Instructions
1. Gmail Settings: - IMAP settings are enabled in Gmail settings.
   Create an app password using your Google Account Security information.
2. **Update Script: - Enter email and App Password on the code file signup_class.java.
- Simple change the paths of the files uploaded as PDFs in the "Verification" section.

## 🏃 Execution
1. Import the project into Eclipse.
2. Add Selenium, JavaMail, httpclient, httpcore, and activation-1.1.1 JARs to your Build Path.
3. Run `signup_class.java` as a Java Application.
