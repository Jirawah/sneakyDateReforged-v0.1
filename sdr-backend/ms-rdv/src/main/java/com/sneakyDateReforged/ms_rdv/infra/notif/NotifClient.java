package com.sneakyDateReforged.ms_rdv.infra.notif;

import com.sneakyDateReforged.ms_rdv.infra.notif.dto.NotificationEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ms-notif",                 // résolu via Eureka
        configuration = NotifFeignConfig.class
)
public interface NotifClient {

    @PostMapping(value = "/events", consumes = "application/json")
    void send(@RequestBody NotificationEventDTO dto);
}
