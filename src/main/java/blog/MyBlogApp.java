package blog;

import java.util.Properties;

import javax.servlet.ServletConfig;

import snap.WebApplication;

public class MyBlogApp extends WebApplication {

  @Override
  public void init(ServletConfig config)
  {
    super.init(config);

    Properties mProperties = readProperties("blog.properties");

  }
}
