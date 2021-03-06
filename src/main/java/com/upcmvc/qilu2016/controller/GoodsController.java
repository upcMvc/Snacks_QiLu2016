package com.upcmvc.qilu2016.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upcmvc.qilu2016.dao.ShopDao;
import com.upcmvc.qilu2016.dao.UserDao;
import com.upcmvc.qilu2016.dto.JsonMes;
import com.upcmvc.qilu2016.model.Goods;
import com.upcmvc.qilu2016.dao.GoodsDao;
import com.upcmvc.qilu2016.model.Shop;
import com.upcmvc.qilu2016.model.User;
import com.upcmvc.qilu2016.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by 陈子枫 on 2016/7/11.
 * 个人商店
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ShopDao shopDao;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private UserDao userDao;


    @RequestMapping("/delete")
    public Object deleteGood(@RequestParam(value = "id", defaultValue = "0") int id) {
        System.out.println("id:" + id);
        Goods goods = goodsDao.findOne(id);
        goods.delete();
        goodsDao.save(goods);
        return new JsonMes(1, "删除成功");
    }

    @RequestMapping("/update")
    @JsonIgnore
    public Object updateGood(@RequestParam(value = "id", defaultValue = "1") int id, @RequestParam(value = "number", defaultValue = "0") int number, String price, String detail, String imgurl) {
        Goods goods = goodsDao.findOne(id);
        goods.update(number, price, detail, imgurl);
        goodsDao.save(goods);
        return new JsonMes(1, "更新成功");
    }
    /*
    根据shopid寻找店铺
    */
    @RequestMapping(value = "/show", method = RequestMethod.GET)
    @JsonIgnore
    public Object showGoods(@RequestParam(value = "shopid", defaultValue = "0") int shopid) {

        return goodsDao.findByShopidAndIsdelete(shopid,false);
    }
}
