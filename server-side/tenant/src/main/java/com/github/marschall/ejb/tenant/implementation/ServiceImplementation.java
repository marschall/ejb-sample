package com.github.marschall.ejb.tenant.implementation;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import com.github.marschall.ejb.tenant.api.ServiceInterface;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Remote(ServiceInterface.class)
public class ServiceImplementation implements ServiceInterface {

  @Override
  public <T> T identiy(T argument) {
    return argument;
  }
}
