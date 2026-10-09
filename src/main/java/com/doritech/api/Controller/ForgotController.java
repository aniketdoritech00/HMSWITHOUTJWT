package com.doritech.api.Controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.EmployeeMasterService;
//import com.doritech.api.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
//@SessionAttributes("/otp")
public class ForgotController {
    @Autowired
    private EmployeeMasterService employeeService;

    @GetMapping("/sendOTP")
    @ResponseBody
    public ResponseEntity sendOTP(@RequestParam("email") String email, HttpSession session) {
        return employeeService.sendOTP(email, session);
    }
    @PostMapping("/verifyOTP")
    @ResponseBody
    public ResponseEntity verifyOtp(@RequestParam("otps") Integer otps, HttpSession session) {
        return employeeService.verifyOtp(otps, session);
    }

    @PostMapping("/updatePasswordByEmail")
    @ResponseBody
    public ResponseEntity updatePasswordByEmail(@RequestParam("email") String email, @RequestParam("password") String newPassword) {
        return employeeService.updatePasswordByEmail(email, newPassword);
    }

//    @PostMapping("/changePassword")
//    public String changePassword(@RequestParam("password") String newPassword, HttpSession session) {
//        String email = (String) session.getAttribute("email");
//        boolean passwordChanged = (boolean) employeeService.updatePasswordByEmail(email, newPassword).getPayload();
//        if (passwordChanged) {
//            return "loginPage";
//        } else {
//            session.setAttribute("message", "Failed to change the password! Please try again.");
//            return "changePassword";
//        }
//    }

}