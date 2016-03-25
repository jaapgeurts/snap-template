# Snap! Web Framework tutorial template

This template demonstrates the basic use of the Snap! web framework. It is also used as the starting point for the Snap! tutorial.

## Eclipse usage
* Install the Eclipse Gradle plugin
* Create a new workspace or create a new folder in your existing workspace.
* Copy this project to a folder to the new workspace/folder
* Import a new Gradle project using File -> import
* Once Eclipse has built the project open a shell and goto the workspace folder run the debug(unix/mac) or debug.bat(windows) scripts.

## Requirements
* Installed Gradle build system
* Java JRE 8 or JDK 8

## Running the example
Open a terminal session and go to the root folder of the project (where the `build.gradle` is). From there execute `gradle appStartWar`. This will start the project webserver. Now open your browser and go to [http://localhost:9100](http://localhost:9100)

## Walkthrough
We'll walk through all parts of the template so that you will learn how it works.

### HTML
Go back to Eclipse and open the HTML template(`src/main/webapp/index.html`) file.

The HTML file is a very basic HTML file and there shouldn't be any surprised here except for the tags.

There are few basic things you should know about Rythm.
* Rythm uses the @ symbol in front of commands.
* Variables passed into the html file must be declared with @args. Arguments can be of any type

You can use the variables you declared anywhere in the HTML.

Note: Snap! uses [Rythm Engine](http://www.rythmengine.org) as its templating engine.


### Java
Now open the java controller (`src/main/java/blog/HomeController.java`) file.

    public class HomeController {
        @RouteOptions(methods = { HttpMethod.GET })
        public RequestResult index(RequestContext context) {
            TemplateView view = new TemplateView("index.html");
            view.addParameter("greeting", "Congratulations. It's working.");
            return view;
        }
    }

In the controller file you will see the name of the Controller Class. 

    public class HomeController {

Note that: No subclassing, implementing interfaces or special annotations are needed to use a class as a controller. Snap! let's you use any POJO object as a controller.
    
Now the method that gets called when the index page is retrieved.

    @RouteOptions(methods = { HttpMethod.GET })
        public RequestResult index(RequestContext context) {

This method is annotated with RouteOptions to indicate that only the HTTP GET method is allowed. If a POST was performed on this URL it would be an error.

The next line loads the template that we will return. This template will get rendered.

            TemplateView view = new TemplateView("index.html");

Note that the file that is loaded is relative to the `src/main/webapp` folder. This folder is the root folder of your web application when it is running.

In the next line we'll add a parameter to the template so that it can be rendered. Finally we return the view.
            view.addParameter("greeting", "Congratulations. It's working.");
            return view;

### routes.conf

The way that Snap! connects a URL to a controller and method is by use of the `routes.conf` file. By default there are two routes.conf files in the template folders. One in `src/main/resources/dev` and one in the `src/main/resources/prod` folder. The dev folder contains the routes for development, the prod folder contains the routes for production.

Open the `routes.conf` file in the `src/main/resources/dev` folder. This file has a concise explanation of how to use it. Of interest to us in this example is this line:

    ACTION    ^/$    home     .HomeController::index

* The `ACTION` indicates a dynamic route that should be connected to a controller method.
* The `^/$` is a regular expression matching "/" (without the quotes). This means that this route only accepts the root of the site. 
* The `home` text is an name or alias by which this route is known and can be referenced.
* The `.HomeController::index` is the controller class `HomeController` and the method `index`


## Exercise

We'll start building our blog. We'll begin by adding posts. To do so we should add a database to our system.


