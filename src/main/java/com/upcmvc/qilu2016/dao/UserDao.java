package com.upcmvc.qilu2016.dao;

import com.upcmvc.qilu2016.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by lenovo on 2016/5/30.
 */
public interface UserDao extends CrudRepository<User,Integer> {

}