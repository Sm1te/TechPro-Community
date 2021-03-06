package com.robin.community.controller;

import com.robin.community.entity.DiscussPost;
import com.robin.community.entity.Page;
import com.robin.community.entity.User;
import com.robin.community.service.DiscussPostService;
import com.robin.community.service.LikeService;
import com.robin.community.service.UserService;
import com.robin.community.utility.CommunityConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @Autowired
    LikeService likeService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){
        // 方法调用前，SpringMVC会自动化实例Model和Page 会被装入model中
        // 在THYMELEAF中可以直接访问数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for(DiscussPost post : list){
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User userById = userService.findUserById(post.getUserId());
                map.put("user", userById);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);

        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }
}
