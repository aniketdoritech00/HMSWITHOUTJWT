<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact Us | Help Center</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --primary-color: #028bd7;
            --primary-light: #e6f4fb;
            --white: #ffffff;
            --light-gray: #f8f9fa;
            --text-dark: #333333;
            --text-light: #666666;
        }
        
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            background-color: var(--light-gray);
            color: var(--text-dark);
        }
        
        .container {
            width: 90%;
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        header {
            background: var(--primary-color);
            color: var(--white);
            padding: 60px 0;
            text-align: center;
            position: relative;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        header::before {
            content: "";
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0) 100%);
            z-index: 1;
        }
        
        header .container {
            position: relative;
            z-index: 2;
        }
        
        header h1 {
            font-size: 3rem;
            margin-bottom: 15px;
            font-weight: 600;
            letter-spacing: 1px;
        }
        
        header p {
            font-size: 1.2rem;
            max-width: 600px;
            margin: 0 auto;
            opacity: 0.9;
        }
        
        .contact-section {
            background: var(--white);
            margin: 30px 0;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.05);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        
        .contact-section:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
        }
        
        .contact-section h2 {
            color: var(--primary-color);
            margin-bottom: 20px;
            font-size: 2rem;
            position: relative;
            padding-bottom: 15px;
        }
        
        .contact-section h2::after {
            content: "";
            position: absolute;
            bottom: 0;
            left: 0;
            width: 60px;
            height: 4px;
            background: var(--primary-color);
            border-radius: 2px;
        }
        
        .contact-section > p {
            margin-bottom: 30px;
            color: var(--text-light);
            font-size: 1.1rem;
        }
        
        .contact-info {
            display: flex;
            flex-wrap: wrap;
            gap: 25px;
        }
        
        .contact-card {
            flex: 1 1 300px;
            padding: 30px;
            background: var(--light-gray);
            border-radius: 12px;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }
        
        .contact-card::before {
            content: "";
            position: absolute;
            top: 0;
            left: 0;
            width: 6px;
            height: 100%;
            background: var(--primary-color);
            transition: width 0.3s ease;
        }
        
        .contact-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.05);
        }
        
        .contact-card:hover::before {
            width: 10px;
        }
        
        .contact-card h3 {
            margin-bottom: 15px;
            color: var(--primary-color);
            font-size: 1.4rem;
        }
        
        .contact-links a {
            display: block;
            margin: 15px 0;
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
            position: relative;
            padding-left: 30px;
        }
        
        .contact-links a i {
            position: absolute;
            left: 0;
            top: 50%;
            transform: translateY(-50%);
            font-size: 1.2rem;
        }
        
        .contact-links a:hover {
            color: var(--primary-color);
            transform: translateX(5px);
        }
        
        .phone-number {
            font-size: 1.3rem;
            font-weight: bold;
            color: var(--text-dark);
            margin: 15px 0;
            display: flex;
            align-items: center;
        }
        
        .phone-number i {
            margin-right: 10px;
            color: var(--primary-color);
            font-size: 1.4rem;
        }
        
        .hours {
            font-style: italic;
            color: var(--text-light);
            margin: 8px 0;
            padding-left: 30px;
        }
        
        .emergency-section {
            background: linear-gradient(to right, var(--primary-color), #0371b3);
            color: var(--white);
            padding: 30px;
            border-radius: 12px;
            margin-top: 40px;
        }
        
        .emergency-section h2 {
            color: var(--white);
        }
        
        .emergency-section h2::after {
            background: var(--white);
        }
        
        .emergency-section .phone-number {
            color: var(--white);
            font-size: 1.5rem;
        }
        
        .emergency-section .phone-number i {
            color: var(--white);
        }
        
        footer {
            text-align: center;
            padding: 40px 0;
            background: var(--primary-color);
            color: var(--white);
            margin-top: 40px;
        }
        
        footer p {
            opacity: 0.9;
            font-size: 1rem;
        }
        
        @media (max-width: 768px) {
            header {
                padding: 40px 0;
            }
            
            header h1 {
                font-size: 2.2rem;
            }
            
            .contact-section {
                padding: 25px;
            }
            
            .contact-card {
                flex-basis: 100%;
            }
        }
    </style>
</head>
<body>
    <!-- <header>
        <div class="container">
            <h1>Contact Us</h1>
            <p>We're here to help you with any questions or concerns</p>
        </div>
    </header> -->

    <div class="container">
        <section class="contact-section">
            <h2>Help Center</h2>
            <p>Our support team is available to assist you with any questions or issues you may have. Choose the contact method that works best for you.</p>
            
            <div class="contact-info">
                <div class="contact-card">
                    <h3>Customer Support</h3>
                    <div class="phone-number"><i class="fas fa-phone-alt"></i>+91 8448990237</div>
                    <div class="hours">Monday - Friday: 8:00 AM - 8:00 PM EST</div>
                    <div class="hours">Saturday: 9:00 AM - 5:00 PM EST</div>
                    <div class="hours">Sunday: Closed</div>
                </div>
                
                <div class="contact-card">
                    <h3>Technical Support</h3>
                    <div class="phone-number"><i class="fas fa-headset"></i>+91 9355830974</div>
                    <div class="hours">24/7 Support Available</div>
                </div>
                
                <div class="contact-card">
                    <h3>Online Resources</h3>
                    <div class="contact-links">
                        <a href="<c:url value='#' />"><i class="fas fa-question-circle"></i> Frequently Asked Questions</a>
                        <a href="<c:url value='#' />"><i class="fas fa-book"></i> Knowledge Base</a>
                        <a href="<c:url value='#' />"><i class="fas fa-video"></i> Video Tutorials</a>
                    </div>
                </div>
                
                <div class="contact-card">
                    <h3>Other Ways to Reach Us</h3>
                    <div class="contact-links">
                        <a href="<c:url value='#' />"><i class="fas fa-comments"></i> Live Chat</a>
                        <a href="mailto:support@example.com"><i class="fas fa-envelope"></i> Email Support</a>
                        <a href="<c:url value='#' />"><i class="fas fa-ticket-alt"></i> Submit a Ticket</a>
                    </div>
                </div>
            </div>
        </section>
      
</body>
</html>