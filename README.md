# Snap! Web Framework tutorial template

This template demonstrates the basic use of the Snap! web framework. It is also used as the starting point for the Snap! tutorial.

## Eclipse usage
* Install the Eclipse Gradle plugin
* Create a new workspace or create a new folder in your existing workspace.
* Copy this project to a folder to the new workspace/folder
* Import a new Gradle project using File -> import -> Gradle (STS)
- Open the folder with the project.
- Click 'build model'
- Select the project you want to import 
* Once Eclipse has built the project open a shell and goto the workspace folder run the `gradlew appRun`.

## Requirements
* Java JDK 8

## Running the example
Open a terminal session and go to the root folder of the project (where the `build.gradle` is). From there execute `./gradlew appRun`. This will start the project webserver. Now open your browser and go to [http://localhost:9100](http://localhost:9100)

## Walkthrough
Let's walk through all parts of the template so that you will learn how it works.

### HTML
Go back to Eclipse and open the HTML template `src/main/webapp/index.html` file.

```html
@args String greeting
<html>
    <head>
        <title>My Blog Demo App</title>
    </head>
    <body>
        <p>This is a demo app that outputs a simple greeting</p>
        <p>@greeting</p>
    </body>
</html>
```

The HTML file is a very basic HTML file and there shouldn't be any surprises here except for the tags.

There are few basic things you should know about the tags.
* Tags are prefixed by a @ symbol.
* Variables passed into the html file must be declared with @args. Arguments can be of any Java type.

You can use the variables you declared anywhere in the HTML.

Declare the template argument greeting. You can use this later as @greeting.

    @args String greeting

This is where we use the greeting.
    <p>This is a demo app that outputs a simple greeting</p>
	<p>@greeting</p>
    
Note: Snap! uses [Rythm Engine](http://www.rythmengine.org) as its templating engine.


### Java
Now open the java controller (`src/main/java/blog/HomeController.java`) file.

```java
public class HomeController {
    @RouteOptions(methods = { HttpMethod.GET })
    public RequestResult index(RequestContext context) {
        TemplateView view = new TemplateView("index.html");
        view.addParameter("greeting", "Congratulations. It's working.");
        return view;
    }
}
```

In the controller file you will see the name of the Controller Class. 

```java
public class HomeController {
```

Note that: No subclassing, implementing interfaces or special annotations is necessary to use a class as a controller. Snap! let's you use any POJO object as a controller. However you can use implement the Snap.Controller interface if you wish.
    
Now the method that gets called when the index page is retrieved.

```java
@RouteOptions(methods = { HttpMethod.GET })
public RequestResult index(RequestContext context) {
```

This method is annotated with RouteOptions to indicate that only the HTTP GET method is allowed. If a POST was performed on this URL it would be an error.

The next line loads the template that we will return. This template will get rendered.

```java
TemplateView view = new TemplateView("index.html");
```

Note that the file that is loaded is relative to the `src/main/webapp` folder. This folder is the root folder of your web application when it is running.

In the next line we'll add a parameter to the template so that it can be rendered. Note that the parameter name must match the argument in the HTML file. Finally we return the view.

```java
view.addParameter("greeting", "Congratulations. It's working.");
return view;
```

### routes.conf

The way that Snap! connects a URL to a controller and method is by use of the `routes.conf` file. By default there are two routes.conf files in the template folders. One in `src/main/resources/dev` and one in the `src/main/resources/prod` folder. The dev folder contains the routes for development, the prod folder contains the routes for production.

Open the `routes.conf` file in the `src/main/resources/dev` folder. This file has a concise explanation of how to use it. Of interest to us in this example is this line:

    ACTION    ^/$    home     .HomeController::index

* The `ACTION` indicates a dynamic route that should be connected to a controller method.
* The `^/$` is a regular expression matching "/" (without the quotes). This means that this route only accepts the root of the site. 
* The `home` text is an name or alias by which this route is known and can be referenced.
* The `.HomeController::index` is the controller class `HomeController` and the method `index`


### Available tags in HTML templates

`@link("aliasname")` - Produce a relative link indentified by `aliasname`. The URL has no regex match groups.

`@link("aliasname",Object[] args)` - Produce a relative link indentified by `aliasname` and replace regex match groups in the URL with the values in the `args` array

`@rootlink("aliasname")` - Produce a link with full http and host name followed by the URL.

`@label(form, "fieldname")` - Produce a HTML label. The output is defined by the annotation of the `fieldname` in the `form`.

`@field(form, "fieldname")` - Produce a HTML field. The input type will depend on the annotation in the `form` for the `fieldname`

## Example

We'll start by building a blog. We'll begin by adding posts. To do so we should add a database to our system.

Normally you would go to the `MyBlogApp` class and add your database initialization code there in the `void init(ServletConfig config);` method. Ie. select and load the JDBC driver and, if you want to use one, setup your ORM of choice.
In my examples I'll set up Ebean.

// Explain get list of posts
// pass to template
// render in template

### Add a login to the page

Add the corresponding line into the routes.conf

`ACTION ^/login$  login  .HomeController::login`

Note that a controller method must have a signature of `public RequestResult METHOD_NAME(RequestContext context);`

// setup authentication
To setup authentication two things are required.
1. Annotate our controller or controller methods with the @LoginRequired annotation and,
2. Add a user to user system and let Snap! know about it.

First create your Application User. This is a user that must implement the `Snap.User` interface. When you've authenticated the user you let Snap@ know by setting an *id* for this user.
Whenever Snap! needs to know your you will supply it in the MyBlogApp.

Instead of declaring each method `@LoginRequired` you can also apply @LoginRequired to the class. This will make all controller methods require login.
If you wish to make an exception you can add `@IgnoreLoginRequired` to an individual method.

*** User HTTP Basic auth

Open `MyBlogApp.java` override two methods:

    public boolean authenticateUser(RequestContext context, String username, String password)

    public User getUser(Long userid)

The first method will be called when Snap! tries to authenticate your user. You should access your database, find your user and return `true` when authentication succeeded, `false` otherwise. When you successfully authenticated the user you should store a ID in the context like so:

    context.setAuthenticatedUser( UserID );

The second method is called each time you or Snap! needs the authenticated user. If will pass the userID and you should fetch the user from the database and return it.

please note that in Snap! does not store the User object in the Session. The reason for this is that storing database backed objects in the session creates problems with the ORM or database session cache. You should fetch the record each time.
    
*** Using forms authentication

In order to use forms authentication we need to use forms first.

**** Using forms
Create a new public class named LoginForm. Like so:

```java
public class LoginForm extends Form
{
  @TextField(id = "loginname", label = "Login")
  public String loginName;

  @PasswordField(id = "password", label = "Password")
  public String password;

  @SubmitField(id = "submit", label = "Login")
  public String submit;
  
  public LoginForm(RequestContext context)
  {
    super(context);
  }
}
```

A form must contain a public constructor that accepts a `RequestContext`.
// explain the various annotations

// using validation
For validation you can use any Validation API, but I recommend Hibernate validator.

Add the following two dependencies to the build.gradle file.

```gradle
compile 'javax.validation:validation-api:1.1.0.Final'
compile 'org.hibernate:hibernate-validator:5.1.3.Final'
```

Add the `@NotBlank` to the loginName and the password
Annotate the LoginForm class methods as follows:

```java
public class LoginForm extends Form
{
  @TextField(id = "loginname", label = "Login")
  @NotBlank(message = "Login required")
  public String loginName;

  @PasswordField(id = "password", label = "Password")
  @NotBlank(message = "Password required")
  public String password;
  
  ...
  
}
```

Now add the form to your HTML.

```html
@import snap.forms.Form
@args Form loginform
...
<form action='@link("login")' method='post'>
  <fieldset>
    @label(loginform,"loginName")
    @field(form: loginform, field: "loginName", class: "form-control")
  </fieldset>
  <fieldset>
    @label(loginform,"password")
    @field(form: loginform, field: "password", class: "form-control")
  </fieldset>
  @field(form: loginform, field: "submit", class: "btn btn-primary")
</form>
...
```

In your login controller method add the following code to the login controller method.

```java
public RequestResult login(RequestContext context)
{
    LoginForm form = new LoginForm(context);
    if (context.getMethod() == HttpMethod.POST)
    {
      form.assignFieldValues();
      if (form.isValid())
      {
      }
    }

    TemplateView view = new TemplateView("login.html");
    view.addParameter("loginform",form) // the name form must match the Form argument in the html template
    return view;
}
```
    
# Writing a custom annotation.

In order to use custom annotations you have to do three things.

1. Create an annotation
2. Implement the AnnotationHandler interface
3. Register your annotation with the handler on your web application instance.

an example:

Scenario: To keep your site private you wish to use a secret access token. We will give users the token by email. Users who know the token will see the site users that don't know the token will see an Authentication error.

We will let users enter the secret token once upon first visit. We will then store the token in a cookie on the client browser and validate it each time the user requests a page.

To validate the token upon each request we will write a custom Annotation.

### 1. Create the token annotation

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
public @interface SecretTokenRequired
{
}
```

### 2. Create the Annotation Handler

```java
public class SecretTokenRequiredHandler implements AnnotationHandler
{
  @Override
  public void execute(Object controller, Method method, Annotation annotation, RequestContext context)
  {    
  }
}
```

### 3. Register it with Snap!

In your MyBlogApp.java add the following lines to `public void init(ServletConfig config)` just below: `super.init(config)`

```java
registerAnnotation(SecretTokenRequired.class, new SecretTokenRequiredHandler());
```

Now we'll decorate all controller methods with the new annotation.

```java
...
@RouteOptions(methods = { HttpMethod.GET, HttpMethod.POST })
@SecretTokenRequired
public RequestResult index(RequestContext context) {
...
}
```

Now let's add some useful code to the `SecretTokenRequiredHandler`. In the secret token method we'll check if the token cookie has been set and if not we'll throw a `AuthenticationException`. This exception combined with the setting 
`snap.login.redirect=true` and the `snap.login.redirect.url=/entersecret` will prompt the user to enter the secret.

So we'll add a new controller action for /entersecret

open `src/main/resource/{dev,prod}/routes.conf` and add a line for entersecret route:

    ACTION ^/entersecret$  entersecret        .HomeController::enterSecret

Add a simple HTML page that prompts the user to either their secret. ( See `Forms` for more examples )

Add the corresponding method to the home controller. Note that this method does not require the `@SecretTokenRequired` annotation since the visitor will enter it here. 

```java
...
@RouteOptions(methods = { HttpMethod.GET, HttpMethod.POST })
public RequestResult enterSecret(RequestContext context) {
    EnterSecretForm form = new EnterSecretForm(context);
    if (context.getMethod() == HttpMethod.POST)
    {
      form.assignFieldValues();
    }

    TemplateView view = new TemplateView("entersecret.html");
    view.addParameter("form", form);
    return view;
}
```

Now the view will show. We'll leave this and add the code for the POST section later. First we'll add code to the `SecretTokenRequiredHandler` so that Snap! will redirect users if they don't have the secret.

Before we code we'll add the secret to our settings file. Open `src/main/resource/{dev,prod}/blog.properties` and add a new property: `blog.secrettoken=mysupersecrettoken`.

Change the `SecretTokenRequiredHandler` to:

```java
public class SecretTokenRequiredHandler implements AnnotationHandler
{
  private static final String BLOG_SECRET_TOKEN = "blog_secret_token";

  @Override
  public void execute(Object controller, Method method, Annotation annotation, RequestContext context)
  {
    Cookie cookie = context.getCookie(BLOG_SECRET_TOKEN);
    if (cookie == null) // No cookie has been set
      throw new AuthenticationException("You are not allowed to login");

    if (!MyBlogApp.get("secrettoken").equals(cookie.getValue())) // the secret is incorrect
      throw new AuthenticationException("Invalid secret");

    // Do nothing and let the visitor continue
  }
}
```

We'll also need to change the POST section of the `enterSecret(RequestContext context)` method.
What we'll do is this: Get the token from the settings and compare it to the token from the form. If they match, then send a cookie to the user with the secret. Our `SecretTokenRequiredHandler` will read the cookie value on the next visit. Finally redirect the user to where they came from or to the home page. Otherwise report error to the visitor.


```java
@RouteOptions(methods = { HttpMethod.GET, HttpMethod.POST })
public RequestResult enterSecret(RequestContext context) {
    EnterSecretForm form = new EnterSecretForm(context);
    if (context.getMethod() == HttpMethod.POST)
    {
        form.assignFieldValues();
        String token = MyBlogApp.get("secrettoken");
        if (token.equals(form.secret))
        {
            // the user posted secret is correct. Set the cookie for future use
            Cookie cookie = new Cookie(SecretTokenRequiredHandler.BLOG_SECRET_TOKEN, token);
            cookie.setMaxAge(10 * 365 * 24 * 60 * 60); // expire after 10 years
            cookie.setPath("/");
            context.addCookie(cookie);
            // redirect the user to the home page
            String next = context.getParamPostGet("next");
            if (next == null || "".equals(next))
                next = "/";
            return new HttpRedirect(next);
        }
        else
        {
          form.setFieldError("secret", "Incorrect secret");
          form.addError("The secret you entered is not correct");
        }
    }

    TemplateView view = new TemplateView("entersecret.html");
    view.addParameter("form", form);
    return view;
}
```

Done!