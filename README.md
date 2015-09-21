# A Simple Todo List Application built w/ Java + Azure DocumentDB

The sample code in this Github repository demonstrates how to create a simple application using Java and Azure DocumentDB.

For a complete end-to-end walkthrough of creating the application, please visit the following [Azure documentation page](https://azure.microsoft.com/documentation/articles/documentdb-java-application/).

![My ToDo List Java application](./media/documentdb-java-application/image1.png)


##<a id="Requirements"></a>Requirements
Before you begin this application development tutorial, you must have the following:

- An active Azure DocumentDB account. 
  - If you don't have an account, you can find instructions on how to create one on our [Azure documentation page](https://azure.microsoft.com/documentation/articles/documentdb-create-account/).
- [Java Development Kit (JDK) 7+](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
- [Eclipse IDE for Java EE Developers.](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/lunasr1)

If you're installing these tools for the first time, coreservlets.com provides a walk-through of the installation process in the Quick Start section of their [Tutorial: Installing TomCat7 and Using it with Eclipse](http://www.coreservlets.com/Apache-Tomcat-Tutorial/tomcat-7-with-eclipse.html) article. 

##<a id="Running"></a>Running the Code Sample

All the samples in this tutorial are included in the documentdb-java-todoapp project on [GitHub](https://github.com/Azure-Samples/documentdb-java-todoapp). To import the todo project into Eclipse, ensure you have the software and resources listed in the [Requirements](#Requirements) section, then do the following:

1. Install [Project Lombok](http://projectlombok.org/). Lombok is used to generate constructors, getters, setters in the project. Once you have downloaded the lombok.jar file, double-click it to install it or install it from the command line. 
2. If Eclipse is open, close it and restart it to load Lombok.
3. In Eclipse, on the **File** menu, click **Import**.
4. In the **Import** window, click **Git**, click **Projects from Git**, and then click **Next**. 
5. On the **Select Repository Source** screen, click **Clone URI**.
6. On the **Source Git Repository** screen, in the **URI** box, enter `https://github.com/Azure-Samples/documentdb-java-todoapp.git`, and then click **Next**.
7. On the **Branch Selection** screen, ensure that **master** is selected, and then click **Next**.
8. On the **Local Destination** screen, click **Browse** to select a folder where the repository can be copied, and then click **Next**.
9. On the **Select a wizard to use for importing projects** screen, ensure that **Import existing projects** is selected, and then click **Next**.
10. On the **Import Projects** screen, unselect the **DocumentDB** project, and then click **Finish**. The DocumentDB project contains the DocumentDB Java SDK, which we will add as a dependency instead.
11. In **Project Explorer**, navigate to `\src\com.microsoft.azure.documentdb.sample.dao\DocumentClientFactory.java` and replace the `HOST` and `MASTER_KEY` values with the `URI` and `PRIMARY KEY` for your DocumentDB account, and then save the file. For more information, see [Step 1. Create a DocumentDB database account](https://azure.microsoft.com/documentation/articles/documentdb-java-application#CreateDB).
12. In **Project Explorer**, right click the **azure-documentdb-java-sample**, click **Build Path**, and then click **Configure Build Path**.
13. On the **Java Build Path** screen, in the right pane, select the **Libraries** tab, and then click **Add External JARs**. Navigate to the location of the lombok.jar file, and click **Open**, and then click **OK**.
14. Use step 12 to open the **Properties** window again, and then in the left pane click **Targeted Runtimes**.
15. On the **Targeted Runtimes** screen, click **New**, select **Apache Tomcat v7.0**, and then click **OK**.
16. Use step 12 to open the **Properties** window again, and then in the left pane click **Project Facets**.
17. On the **Project Facets** screen, select **Dynamic Web Module** and **Java**, and then click **OK**.
18. On the **Servers** tab at the bottom of the screen, right-click **Tomcat v7.0 Server at localhost** and then click **Add and Remove**.
19. On the **Add and Remove** window, move **azure-documentdb-java-sample** to the **Configured** box, and then click **Finish**. 
20. In the **Server** tab, right-click **Tomcat v7.0 Server at localhost**, and then click **Restart**.
21. In a browser, navigate to `http://localhost:8080/azure-documentdb-java-sample/` and start adding to your task list. Note that if you changed your default port values, change `8080` to the value you selected.
