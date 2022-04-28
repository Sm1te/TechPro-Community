package com.robin.community.controller;

import com.robin.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


@Controller
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @GetMapping("/hello")
    public String sayHello(){
        return "hello";
    }

    @GetMapping("/alphadata")
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String s = headerNames.nextElement();
            String header = request.getHeader(s);
            System.out.println(s + header);
        }

        System.out.println(request.getParameter("code"));

        //response
        response.setContentType(("text.html;charset=utf-8"));
        PrintWriter writer = response.getWriter();
        writer.write("<h1>robin</h1>");
        writer.close();
    }

    //Get
    //     /students?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(value = "current", required = false, defaultValue = "1") int current, @RequestParam(value = "limit", required = false, defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "wuhu";
    }

    //   /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id")int id){
        System.out.println(id);
        return "robin";
    }

    // Post
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name + "   " +  age);
        return "success";
    }

    // return HTML
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "robin");
        mav.addObject("age", 22);
        mav.setViewName("/demo/view");
        return mav;
    }

    @GetMapping("/school")
    public String getSchool(Model model){
        model.addAttribute("name", "yijian");
        model.addAttribute("age", 21);
        return "/demo/view";
    }

    //response to json(异步请求)
    // Java Object -> JSON String -> JS Object
    @GetMapping("/emp")
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "robin");
        emp.put("age", 20);
        emp.put("salary", 200000);
        return emp;
    }

    @GetMapping("/emps")
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> res = new ArrayList<>();

        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "robin");
        emp.put("age", 20);
        emp.put("salary", 200000);

        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("name", "YIJIANN");
        emp1.put("age", 20);
        emp1.put("salary", 2000);
        res.add(emp);
        res.add(emp1);

        return res;
    }

}
