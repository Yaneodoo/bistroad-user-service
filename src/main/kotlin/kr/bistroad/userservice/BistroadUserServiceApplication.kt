package kr.bistroad.userservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.ribbon.RibbonClient

@SpringBootApplication
@EnableDiscoveryClient
@RibbonClient(name = "user-service")
class BistroadUserServiceApplication

fun main(args: Array<String>) {
    runApplication<BistroadUserServiceApplication>(*args)
}
