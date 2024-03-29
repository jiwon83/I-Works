package com.example.iworks.domain.meeting.domain;

import com.example.iworks.domain.schedule.domain.Schedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting")
@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Meeting {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private int meetingId; // 회의방 아이디

    @OneToOne(mappedBy = "scheduleMeeting", fetch = FetchType.LAZY)
    private Schedule schedule; // 할 일 아이디(외래키)

    @Column(name = "meeting_date", nullable = false)
    private LocalDateTime meetingDate; // 회의 일시

    @Column(name = "meeting_sessionId", length = 2000, nullable = false, updatable = false)
    private String meetingSessionId; // 회의 세션 아이디

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
        if (schedule.getScheduleMeeting() != this){
            schedule.setMeeting(this);
        }
    }

    public void updateMeeting(LocalDateTime meetingDate){
        this.meetingDate = meetingDate;
    }
}
