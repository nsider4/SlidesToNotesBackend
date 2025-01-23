# 🗒️ SlidesToNotes Backend  

This is the backend for the **SlidesToNotes** application, a tool that allows users to upload `.pptx` files, convert slides to notes, download the notes, copy the notes, and download associated metadata. The backend is built using **Spring Boot** and provides several security protections to ensure the application's reliability and safety.

### 📝 Core Features

- Convert `.pptx` slides to notes seamlessly.
- Download generated notes in a user-friendly format.
- Download metadata, including notes and images, in a ZIP file.
- Built-in security measures:
  - **Denial of Service (DoS) / Distributed Denial of Service (DDoS)**
  - **Brute Force Attacks**
  - **Resource Exhaustion**
  - **Cross-Site Scripting (XSS)**
  - **MIME Sniffing Attacks**
  - **Man-in-the-Middle (MitM) Attacks**


## 🔧 Tech Stack  

- **Backend Framework**: Spring Boot  
- **Language**: Java  
- **Database**: MongoDB  
- **Build Tool**: Maven  
- **Hosting**: Render  

---

## Getting Started

### Prerequisites

Ensure you have the following installed on your system:
- **Java Development Kit (JDK)** (Version 17 or later)
- **Maven** (for dependency management)
- **MongoDB** (as the database)
 
### Running the Application

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/SlidesToNotes-Backend.git
   cd SlidesToNotes-Backend

2. **Update the Configuration**:
   Edit the `src/main/resources/application.properties` file to replace placeholders with your MongoDB credentials and any other required configurations:
   ```properties
   # MongoDB configuration
   spring.data.mongodb.uri={MONGO_URI}
   spring.data.mongodb.database={DB_NAME}

   # CORS Configuration
   cors.allowed-origin=https://slides-to-notes.vercel.app

   # Rate Limiting Configuration
   rate.limit.requests=5
   rate.limit.duration.minutes=1

   # Thread Pool Configuration
   fileService.threadPoolSize=4

   spring.servlet.multipart.max-file-size=100MB
   spring.servlet.multipart.max-request-size=100MB
  
3. **Build the Project**:
   Use Maven to build the project and resolve dependencies:
   ```bash
   mvn clean package
   
4. **Run the Application**:
   Start the Spring Boot application:
   ```bash
   mvn spring-boot:run

5. **Access the Application**
   Once the application is available, it will be accessible at:
   ```arduino
   http://localhost:8080
 
## API Endpoints  
Here are the main API endpoints provided by the backend:

- **File Upload and Note Extraction**:  
  - **Endpoint**: `/api/upload`  
  - **Method**: POST  
  - **Description**: Accepts a PPTX file, processes it, and extracts notes.  
  - **Parameters**:  
    - `file` (multipart/form-data): The PPTX file to upload.  
    - `removeImages` (boolean, optional): Indicates whether to exclude images. Default is `true`.

- **Get Note Count**:  
  - **Endpoint**: `/get-note-count`  
  - **Method**: GET  
  - **Description**: Returns the total number of notes processed.  
  - **Headers**:  
    - `Origin`: Should match the allowed origin configured in `application.properties`.

- **Download Metadata as ZIP**:  
  - **Endpoint**: `/api/metadata/download`  
  - **Method**: POST  
  - **Description**: Generates and downloads a ZIP file containing metadata such as notes and images.  
  - **Body**:  
    - A JSON object with the notes and image URLs.

- **Generate DOCX**:  
  - **Endpoint**: `/api/docx/generate`  
  - **Method**: POST  
  - **Description**: Generates a DOCX file based on the provided content.  
  - **Body**:  
    - A JSON object defining headers, paragraphs, images, and tables.

---

## Notes on Security  
The backend is equipped with several security measures:

1. **Rate Limiting**:  
   - Prevents abuse by limiting the number of requests a single client can make within a specified timeframe.

2. **CORS Configuration**:  
   - Restricts access to the backend to requests originating from the specified frontend domain.

3. **HTTPS Enforcement**:  
   - Protects against man-in-the-middle (MitM) attacks by encrypting all communications.

4. **Content Validation**:  
   - Prevents malicious content execution (e.g., XSS attacks) by validating and sanitizing input.

5. **Request Origin Verification**:  
   - Ensures that sensitive endpoints are accessed only by the trusted frontend.

---

## Troubleshooting  

- **Issue**: Application fails to connect to MongoDB.  
  - **Solution**: Verify the `spring.data.mongodb.uri` and `spring.data.mongodb.database` configurations in `application.properties`. Ensure that your MongoDB instance is running and accessible.

- **Issue**: CORS errors when accessing the API from the frontend.  
  - **Solution**: Check the `cors.allowed-origin` configuration. Ensure it matches your frontend URL.

- **Issue**: Out-of-memory errors during large file uploads.  
  - **Solution**: Increase the maximum file and request size in `application.properties`:  
    ```properties
    spring.servlet.multipart.max-file-size=200MB
    spring.servlet.multipart.max-request-size=200MB
    ```

---

## Contributing  
Contributions are welcome! Please open an issue or submit a pull request with your suggestions or bug fixes.
