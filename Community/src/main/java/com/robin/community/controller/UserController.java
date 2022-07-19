package com.robin.community.controller;

import com.robin.community.annotation.LoginRequired;
import com.robin.community.entity.User;
import com.robin.community.service.FollowService;
import com.robin.community.service.LikeService;
import com.robin.community.service.UserService;
import com.robin.community.utility.CommunityConstant;
import com.robin.community.utility.CommunityUtil;
import com.robin.community.utility.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    //个人设置页面修改密码功能
    //这里形参用Model类和User类即可，SpringMVC会把传入内容按照User属性填入user
    @LoginRequired
    @PostMapping("/setting")
    public String updatePassword(Model model, String password, String newPassword, String confirmPassword){
        if(StringUtils.isBlank(password)){
            model.addAttribute("passwordMsg", "请输入原始密码！");
            return "/site/setting";
        }

        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg", "请输入新密码！");
            return "/site/setting";
        }

        if(StringUtils.isBlank(confirmPassword)){
            model.addAttribute("confirmPasswordMsg", "请再次输入新密码！");
            return "/site/setting";
        }

        if(!newPassword.equals(confirmPassword)){
            model.addAttribute("newPasswordMsg", "两次输入的密码不相同！");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(password, newPassword, user.getId());

        if(map == null || map.isEmpty()){
            //传给templates注册成功信息
            model.addAttribute("msg", "密码修改成功");
            //跳到回个人设置页面
            model.addAttribute("target", "/user/setting");
            return "/site/operate-result";
        }else{
            //失败了传失败信息，跳到到原来的页面
            model.addAttribute("passwordMsg", "输入的原始密码错误");
            return "/site/setting";
        }
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error", "No image");
            return "/site/setting";
        }

        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","Wrong Format!");
            return "/site/setting";
        }

        // Random name
        String fileName = CommunityUtil.generateUUID() + suffix;
        // make sure path
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("Fail to upload" + e.getMessage());
            throw new RuntimeException("Fail to uplaod", e);
        }

        // Update the current headpath of user(web path)
        // https://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;

        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    // 个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

}
