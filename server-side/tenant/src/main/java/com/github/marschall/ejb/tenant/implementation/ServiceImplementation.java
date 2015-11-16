package com.github.marschall.ejb.tenant.implementation;

import com.github.marschall.ejb.tenant.api.ServiceInterface;

public class ServiceImplementation implements ServiceInterface {

  @Override
  public <T> T identiy(T argument) {
    return argument;
  }
}
