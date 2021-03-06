package com.upcmvc.qilu2016.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upcmvc.qilu2016.config.Config;
import com.upcmvc.qilu2016.dao.UserDao;
import com.upcmvc.qilu2016.dto.QQClientInfo;
import com.upcmvc.qilu2016.model.User;
import com.upcmvc.qilu2016.service.LoginService;
import com.upcmvc.qilu2016.service.QQOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Created by Jaxlying on 2016/7/14.
 */
@RestController
public class QQRegController {

    @Autowired
    private Config config;

    @Autowired
    private QQOauthService qqOauthService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginService loginService;

    @Autowired
    private HttpSession httpSession;


    @RequestMapping(value = "/",method = RequestMethod.GET,params = {"code","state=qq"})
    @JsonIgnore
    public Object dealOauth(String code) throws IOException {
        String token = qqOauthService.getToken(qqOauthService.getTokenAndRefresh(code));
        String idstr = qqOauthService.getOpenId(token);
        QQClientInfo qqClientInfo = qqOauthService.getQQclientinfo(idstr);
        if(loginService.isOurUser(qqClientInfo.openid) == true){
            User user = userDao.findByQqopenid(qqClientInfo.openid);
            httpSession.setAttribute("user",user);
            return user;
        }
        else
            return qqOauthService.getQQInfor(token,qqClientInfo.openid);
    }

    @RequestMapping(value = "/regist",params = "state=qq")
    @JsonIgnore
    public Object regist(String nickname,String figureurl_qq_1,String openid,String phone){
        User user = new User(openid,nickname,phone,figureurl_qq_1);
        userDao.save(user);
        User newuser = userDao.findTopByOrderByCreattimeDesc();
        httpSession.setAttribute("user",newuser);
        return newuser;
    }

}

