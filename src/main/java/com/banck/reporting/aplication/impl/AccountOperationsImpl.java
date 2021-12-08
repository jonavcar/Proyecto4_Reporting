/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banck.reporting.aplication.impl;

import com.banck.reporting.domain.ProductSummaryDto;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;
import com.banck.reporting.aplication.AccountOperations;

/**
 *
 * @author jonavcar
 */
@Service
@RequiredArgsConstructor
public class AccountOperationsImpl implements AccountOperations {

    Logger logger = LoggerFactory.getLogger(AccountOperationsImpl.class);

    @Override
    public Flux<ProductSummaryDto> listByCustomer(String customer) {

        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 6000)
                .doOnConnected(connection
                        -> connection.addHandlerLast(new ReadTimeoutHandler(3))
                        .addHandlerLast(new WriteTimeoutHandler(3)));

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                //.clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient))) // timeout
                .build();

        return webClient.get()
                .uri("/banck-account/summary/"+customer+"/list")
                .retrieve()
                .bodyToFlux(ProductSummaryDto.class).flatMap(o -> {
            return Mono.just(o);
        });
    }

}
