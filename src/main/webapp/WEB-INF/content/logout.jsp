<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Logged Out - Pharmacy System</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .logout-container {
            background-color: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            text-align: center;
            max-width: 500px;
        }
        .success-icon {
            font-size: 64px;
            color: #27ae60;
            margin-bottom: 20px;
        }
        h1 {
            color: #2c3e50;
            margin-bottom: 15px;
        }
        p {
            color: #7f8c8d;
            margin-bottom: 30px;
            font-size: 16px;
        }
        .btn {
            padding: 12px 30px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            font-size: 16px;
        }
        .btn:hover {
            background-color: #2980b9;
        }
        .message {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
    </style>
    
</head>
<body>
    <div class="logout-container">
        <div class="success-icon">✓</div>
        <h1>Successfully Logged Out</h1>
        
        <s:if test="hasActionMessages()">
            <div class="message">
                <s:actionmessage/>
            </div>
        </s:if>
        
        <p>You have been successfully logged out of the Pharmacy System.</p>
        <p style="font-size: 14px; color: #95a5a6;">You will be redirected to the dashboard in 5 seconds...</p>
        
        <a href="<s:url action='dashboard'/>" class="btn">Return to Dashboard</a>
    </div>
</body>
</html>