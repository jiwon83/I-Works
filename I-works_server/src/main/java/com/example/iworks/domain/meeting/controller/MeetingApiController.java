package com.example.iworks.domain.meeting.controller;

import com.example.iworks.global.util.Response;
import com.example.iworks.global.util.OpenViduUtil;
import io.openvidu.java.client.OpenViduException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/meeting")
@RequiredArgsConstructor
public class MeetingApiController {
    private final OpenViduUtil openViduUtil;
    private final Response response;

    @GetMapping("/create")
    public ResponseEntity<Map<String,Object>> createMeeting() {
        try {
            return response.handleSuccess(openViduUtil.createSession());
        }
        catch(OpenViduException e){
            System.out.println(e.getMessage());
            return response.handleFail("방 생성 실패 code: " + e.getMessage(),null);
        }
    }
    @GetMapping("/info/{sessionId}")
    public ResponseEntity<Map<String,Object>> getMeeting(@PathVariable(name = "sessionId") String sessionId) {
        try{
        return response.handleSuccess(openViduUtil.getConnections(sessionId));

        }catch (Exception e){
            return response.handleFail("존재하지 않는 세션입니다.",null);
        }
    }

    @GetMapping("/connect/{sessionId}")
    public ResponseEntity<Map<String,Object>> connectMeeting(@PathVariable(name = "sessionId") String sessionId){
        try {
            return response.handleSuccess(openViduUtil.connectSession(sessionId));
        } catch (Exception e) {
            return response.handleFail("존재하지 않는 세션입니다.",null);
        }
    }

}