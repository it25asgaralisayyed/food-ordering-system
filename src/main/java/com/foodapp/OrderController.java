package com.foodapp;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

@Controller
public class OrderController {

    private static final Map<String, Integer> MENU = Map.of(
            "Margherita Pizza", 250,
            "Veg Burger", 120,
            "Paneer Wrap", 150,
            "Masala Dosa", 100
    );

    @PostMapping("/order")
    @ResponseBody
    public String placeOrder(@RequestParam String customerName,
                             @RequestParam String item,
                             @RequestParam int quantity) {

        int price = MENU.getOrDefault(item, 0);
        int total = price * quantity;
        String safeName = HtmlUtils.htmlEscape(customerName);
        String safeItem = HtmlUtils.htmlEscape(item);

        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>"
             + "<title>Order Confirmation</title></head><body>"
             + "<h2 id='message'>Order placed successfully!</h2>"
             + "<p id='customer'>Customer: " + safeName + "</p>"
             + "<p id='item'>Item: " + safeItem + "</p>"
             + "<p id='qty'>Quantity: " + quantity + "</p>"
             + "<p id='total'>Total Amount: Rs. " + total + "</p>"
             + "<a id='back' href='/'>Order again</a>"
             + "</body></html>";
    }
}