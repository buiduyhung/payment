package com.paypalpay.paypalpayment.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.paypalpay.paypalpayment.config.PaypalPaymentIntent;
import com.paypalpay.paypalpayment.config.PaypalPaymentMethod;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.paypalpay.paypalpayment.service.PaypalService;
import com.paypalpay.paypalpayment.utils.Utils;


@Controller
public class PaymentController {
    public static final String PAYPAL_SUCCESS_URL = "pay/success";
    public static final String PAYPAL_CANCEL_URL = "pay/cancel";

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PaypalService paypalService;

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping("pay")
    public String pay(HttpServletRequest request, @RequestParam("price") double price){
        String cancelUrl = Utils.getBaseURL(request) + "/" + PAYPAL_CANCEL_URL;
        String successUrl = Utils.getBaseURL(request) + "/" + PAYPAL_SUCCESS_URL;

        try {
            Payment payment = paypalService.createPayment(
                    price,
                    "USD",
                    PaypalPaymentMethod.paypal,
                    PaypalPaymentIntent.sale,
                    "payment description",
                    cancelUrl,
                    successUrl);
            for(Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    return "redirect:" + links.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            log.error("Error occurred during payment creation: {}", e.getDetails());
            log.error("PayPal Error Message: {}", e.getMessage());
        }

        return "redirect:/";
    }

    @RequestMapping(PAYPAL_CANCEL_URL)
    public String cancelPay(){
        return "cancel";
    }

    @RequestMapping(PAYPAL_SUCCESS_URL)
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId){
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){
                return "success";
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        return "redirect:/";
    }
}
