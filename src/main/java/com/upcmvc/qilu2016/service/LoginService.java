package com.upcmvc.qilu2016.service;

import com.google.gson.Gson;
import com.upcmvc.qilu2016.GsonTemplate.VerifyUserInfo;
import com.upcmvc.qilu2016.util.MCrypt;
import com.upcmvc.qilu2016.util.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by 陈子枫 on 2016/7/5.
 */
@Service
public class LoginService {

    @Autowired
    private HttpSession httpSession;
    @Autowired
    private GetRealMessage getRealMessage;
    @Value("${myconfig.client_id}")
    private static String client_id;
    @Value("${myconfig.redirect_uri}")
    private static String redirect_uri;

    public boolean procssAuthReal() throws IOException{
        String access_taoken = (String) httpSession.getAttribute("access_token");
        String message = getRealMessage.getMessage(access_taoken,"verify_me");
        return saveRealSession(message);
    }
    /**
     * 保存学校验证信息的session
     * @param message
     * @return
     */
    private boolean saveRealSession(String message) {
        Gson gson = new Gson();
        try {
            VerifyUserInfo verifyUserInfo = gson.fromJson(message,VerifyUserInfo.class);
             if(verifyUserInfo.status.equals("success")){
                 httpSession.setAttribute("realname",verifyUserInfo.info.yb_realname);
                 httpSession.setAttribute("studentid",verifyUserInfo.info.yb_schoolid);
                 return true;
             }else {
                 return false;
             }
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }


    /**
     * 判断用户是否携带真实信息(学号)
     * @return
     */
    private boolean isAuthReal(){return httpSession.getAttribute("yi_studentid")!=null;}

    public ModelAndView toYibanAuth(){
        return new ModelAndView("redirect:https://openapi.yiban.cn/oauth/authorize?client_id=" + client_id + "&redirect_uri=" + redirect_uri);
    }

    /**
     * 判断用户是否登陆
     * @return
     */
    public boolean isLogin(){return httpSession.getAttribute("userid")!=null; }

    /**
     * 处理授权.成功则跳转至/,失败则重新引导用户至易班授权模块
     * @param verify_request 易班服务器提供的信息,加密的.
     * @return
     * @throws Exception
     */
    public String processAuth(String verify_request) throws Exception {
        MCrypt mCrypt = new MCrypt();
        String res = new String(mCrypt.decrypt(verify_request));
        if (saveSession(res)) {
            return "redirect:/";
        } else {
            return "redirect:/yibanauth";
        }
    }


    /**
     * 完成对解密后的json数据的解析,存在session里.解析失败则是未授权用户,引导其至授权界面.
     *
     * @param str
     */
    private boolean saveSession(String str) {
        Gson gson = new Gson();
        try {
            SessionUser sessionUser = gson.fromJson(str, SessionUser.class);
            httpSession.setAttribute("visit_time", sessionUser.visit_time);
            httpSession.setAttribute("userid", sessionUser.visit_user.userid);
            httpSession.setAttribute("username", sessionUser.visit_user.username);
            httpSession.setAttribute("usernick", sessionUser.visit_user.usernick);
            httpSession.setAttribute("usersex", sessionUser.visit_user.usersex);
            httpSession.setAttribute("access_token", sessionUser.visit_oauth.access_token);
            httpSession.setAttribute("token_expires", sessionUser.visit_oauth.token_expires);
            return true;
        } catch (Exception ex) {
            //ex.printStackTrace();
            return false;
        }
    }

}

