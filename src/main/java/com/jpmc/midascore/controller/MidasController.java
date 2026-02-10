package com.jpmc.midascore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.service.BalanceProvider;


@RestController
public class MidasController {
	
	@Autowired
	private BalanceProvider balanceProvider ;
	
	@GetMapping("/balance")
	public Balance getMethodName(@RequestParam Long userId) {
		return balanceProvider.getBalance(userId);
		
	}
	

}
