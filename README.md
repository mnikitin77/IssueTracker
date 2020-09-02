# IssueTracker
This is a study project that I fulfilled as practice during my studies on the Sberbank Java School course. It's a basic issue tracking application (i.e. a simple version of jira). The application does not have a UI and it consists of two main parts and a PostgreSQL database.
## Part 1 - Business Logic
The class structure representing the business objects of the IssueTrakcer is in the com.mvnikitin.itracker.business.* packages. The main class in the application structure is Project. A project contains one backlog and a collection of sprints. Once created an issue belongs to the project's backlog. An issue may be moved within the backlog and the project's sprints. A projects contains a team of users and some other attributes. Issues support parent-child relations. The main business objects originated on runtime are created with help of a factory instance. There are workflows representing states and allowed transitions for each issue type (some workflows are in the setup SQL script). Filters are desinged in order to filter issues in the backlog (only).

Other things:
* Access to the data is done via pure JDBC (it is not thread-safe and there is no connection pool; to make it thread-safe you can make DBConnectionImpl bean with the prototype or session scope)
* It is a spring application and uses just Spring Core (all the main utility objects are singleton beans but the project which are prototypes).
* There are a small number of some GoF pattern implementations:
    * Abstract Factory - IssueTrackingFactoryImpl
    * Chain of Responsibility - Filters and the related classes
* To check the application, please make your own test application or unit tests. Alternatively, you can run TestBacklogFilters test to see the filters functionality or run TestUserRepository
  

## Part 2 - REST

The RESTful API allows working with the resources Project and Issue offering main CRUD operations. It is implemented with help of Spring Boot, Spring Data JPA, Basic Security with the in-memory authentication (system_user:Qwerty123) and HATEOAS for linking resources. REST uses its own JPA entities and repositories to access the data via Hibernate and do not uses the classes of Part 1.  
****

IMPORTANT: Before run, please create 'issue_tracker' DB schema, check the DB connection URL over the code (flyway.conf and application.properties), and then run flyway:migrate or run the script V1_1__issue_tracker_initialize.sql manually.