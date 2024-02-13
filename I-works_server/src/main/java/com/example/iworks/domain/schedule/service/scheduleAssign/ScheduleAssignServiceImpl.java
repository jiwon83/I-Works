package com.example.iworks.domain.schedule.service.scheduleAssign;

import com.example.iworks.domain.schedule.domain.ScheduleAssign;
import com.example.iworks.domain.schedule.dto.scheduleAssign.response.ScheduleAssignResponseDto;
import com.example.iworks.domain.schedule.dto.scheduleAssign.request.AssigneeInfo;
import com.example.iworks.domain.schedule.repository.scheduleAssign.ScheduleAssignRepository;
import com.example.iworks.domain.team.domain.TeamUser;
import com.example.iworks.domain.team.repository.teamuser.TeamUserRepository;
import com.example.iworks.domain.user.domain.User;
import com.example.iworks.domain.user.repository.UserRepository;
import com.example.iworks.global.dto.DateCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.iworks.global.common.CodeDef.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleAssignServiceImpl implements ScheduleAssignService{

    private final UserRepository userRepository;
    private final ScheduleAssignRepository scheduleAssignRepository;
    private final TeamUserRepository teamUserRepository;

    @Override
    public List<ScheduleAssignResponseDto> findTaskByUser(int userId, DateCondition dateCondition) {

        return findScheduleAssignsBySearchParameter(findUserBelongs(userId), dateCondition, true)
                .stream()
                .map(ScheduleAssignResponseDto::new)
                .toList();
    }

    /** 유저의 모든 소속에 대하여 할일 배정 및 할일 조회 */
    @Override
    public List<ScheduleAssignResponseDto> findByUser(int userId, DateCondition dateCondition) {
        return findScheduleAssignsBySearchParameter(findUserBelongs(userId), dateCondition, false)
                .stream()
                .map(ScheduleAssignResponseDto::new)
                .toList();
    }

    /** 할일 생성에서 선택된 소속의 할일 배정 및 할일 조회 */
    @Override
    public List<ScheduleAssignResponseDto> findByAssignees(List<AssigneeInfo> assigneeInfos, DateCondition dateCondition) {
        return findScheduleAssignsBySearchParameter(assigneeInfos,  dateCondition,false)
                .stream()
                .map(ScheduleAssignResponseDto::new)
                .toList();
    }

    /** 유저의 모든 소속에 대한 할일 배정 검색 조건 조회*/
    @Override
    public List<AssigneeInfo> findUserBelongs(int userId) {

        List<AssigneeInfo> searchParameterDtoList = new ArrayList<>();
        User user = userRepository.findById(userId).orElseThrow(IllegalAccessError::new);

        searchParameterDtoList.add(new AssigneeInfo(TARGET_USER_CODE_ID, userId));
        searchParameterDtoList.add(new AssigneeInfo(TARGET_DEPARTMENT_CODE_ID, user.getUserDepartment().getDepartmentId()));

        List<TeamUser> teamUsersByUser = teamUserRepository.findTeamUserByUserId(userId);
        for (TeamUser teamUser: teamUsersByUser){
            searchParameterDtoList.add(new AssigneeInfo(TARGET_TEAM_CODE_ID, teamUser.getTeamUserTeam().getTeamId()));
        }
        return searchParameterDtoList;
    }

    /** 할일 배정 검색 조건에 대한 할일 배정 및 할일 조회 */
    @Override
    public List<ScheduleAssign> findScheduleAssignsBySearchParameter(List<AssigneeInfo> requestDtoList, DateCondition dateCondition, boolean onlyTask) {
        return scheduleAssignRepository.findScheduleAssignsBySearchParameter(requestDtoList, dateCondition, onlyTask);
    }


}
