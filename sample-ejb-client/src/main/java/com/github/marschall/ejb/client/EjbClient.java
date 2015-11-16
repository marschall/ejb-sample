package com.github.marschall.ejb.client;


import java.lang.annotation.Annotation;
import java.security.Security;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.sasl.JBossSaslProvider;

import com.github.marschall.ejb.tenant.api.AnnotatedClass;
import com.github.marschall.ejb.tenant.api.CustomAnnotation;
import com.github.marschall.ejb.tenant.api.ServiceInterface;

public class EjbClient {
  static {
    Security.addProvider(new JBossSaslProvider());
  }

  public void run() {
    Context context = createInitialContext();

    ServiceInterface service = lookUp(context, "tenant", "ServiceImplementation", ServiceInterface.class);
    Annotation annotation = getAnnotation();
    Annotation returnValue = service.identiy(annotation);
    System.out.println(returnValue);
  }

  Annotation getAnnotation() {
    return AnnotatedClass.class.getAnnotation(CustomAnnotation.class);
  }

  <T> T lookUp(Context context, String moduleName, String beanName, Class<T> interfaceClass) {
    // The app name is the application name of the deployed EJBs. This is typically the ear name
    // without the .ear suffix. However, the application name could be overridden in the application.xml of the
    // EJB deployment on the server.
    // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
    String appName = "sample-application";
    // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
    // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
    // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
    // jboss-as-ejb-remote-app
    // val moduleName = "";
    // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
    // our EJB deployment, so this is an empty string
    String distinctName = "";
    // The EJB name which by default is the simple class name of the bean implementation class
    //    final String beanName = "AS7Bean";
    // the remote view fully qualified class name
    // let's do the lookup
    // val proxy = context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + interfaceClass.getName());
    // val remoteName = "ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + interfaceClass.getName()
    String remoteName = "ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + interfaceClass.getName();
    System.out.println(remoteName);
    Object proxy;
    try {
      proxy = context.lookup(remoteName);
    } catch (NamingException e) {
      throw new RuntimeException("look up of " + remoteName + "failed", e);
    }
    // val proxy = context.lookup("ejb:/" + appName + "/" + moduleName + "/" + beanName + "!" + interfaceClass.getName());
    return interfaceClass.cast(proxy);
  }

  Hashtable<?, ?> createConfigurationHashTableOld() {
    // https://issues.jboss.org/browse/EJBCLIENT-34
    Hashtable<Object, Object> jndiProperties = new Hashtable<>();

    // https://community.jboss.org/thread/196054
    // https://community.jboss.org/thread/199165?tstart=0
    // java.naming.provider.url=remote://localhost:4447
    // java.naming.factory.initial=org.jboss.naming.remote.client.InitialContextFactory
    // java.naming.factory.url.pkgs=org.jboss.ejb.client.naming

    // https://community.jboss.org/thread/196943
    // https://community.jboss.org/wiki/JBossAS7RemoteEJBAuthenticationHowto
    // https://community.jboss.org/message/732309#732309#732309
    // https://community.jboss.org/thread/176963
    jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");

    // needs updated client
    /*
    jndiProperties.put("endpoint.name", "client-endpoint")
    jndiProperties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", false)

    jndiProperties.put("remote.connections", "default")

    jndiProperties.put("remote.connection.default.host", "127.0.0.1")
    jndiProperties.put("remote.connection.default.port", 4447)
    jndiProperties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", false);
     */

    // jndiProperties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");

    return jndiProperties;
  }

  Hashtable<Object, Object> createConfigurationHashTable() {
    // https://issues.jboss.org/browse/EJBCLIENT-34
    Hashtable<Object, Object> jndiProperties = new Hashtable<>();

    jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
    jndiProperties.put(Context.PROVIDER_URL,"remote://localhost:4447");

    return jndiProperties;
  }

  Context createInitialContext()  {
    Hashtable<?, ?> jndiProperties = createConfigurationHashTable();
    try {
      return new InitialContext(jndiProperties);
    } catch (NamingException e) {
      throw new RuntimeException("name lookup failed", e);
    }
  }


  public static void main(String[] args) {
    EjbClient client = new EjbClient();
    client.run();
  }

}
