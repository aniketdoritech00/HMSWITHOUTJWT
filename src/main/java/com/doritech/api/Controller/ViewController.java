package com.doritech.api.Controller;

import com.doritech.api.Service.LogoService;
import com.doritech.api.Service.ReadMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViewController {
   @Autowired
   LogoService logoService;
   @Autowired
   ReadMail readMailService;

   @RequestMapping({"/", "/{path:[^\\.]*}", "/**/{path:[^\\.]*}"})
   public String loginTMS() {
      return "forward:/index.html";
   }

   @PostMapping({"/hitUrlForHealthCommunicatorHealth"})
   @ResponseBody
   public String hitUrlForHealthCommunicatorHealth() {
      return this.logoService.hitUrlForHealthCommunicatorHealth();
   }

   @PostMapping({"/hitUrlForStatus"})
   @ResponseBody
   public String hitUrlForStatus(String deviceSerialNumber) {
      this.readMailService.hitUrlForStatus(deviceSerialNumber);
      return "done";
   }

   @PostMapping({"/hitUrlForReset"})
   @ResponseBody
   public String hitUrlForReset(@RequestParam String deviceSerialNumber) {
      this.readMailService.hitUrlForReset(deviceSerialNumber);
      return "done";
   }

   @PostMapping({"/hitUrlForStatusSBI"})
   @ResponseBody
   public String hitUrlForStatusSBI(String deviceSerialNumber) {
      this.logoService.hitUrlForStatusSBI(deviceSerialNumber);
      return "done";
   }
}