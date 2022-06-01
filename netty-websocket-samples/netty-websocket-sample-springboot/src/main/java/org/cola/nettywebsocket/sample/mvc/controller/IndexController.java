package org.cola.nettywebsocket.sample.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class IndexController {
    @ResponseBody
    @RequestMapping("/index")
    String index() {
        return "xxl job executor running.";
    }

}
